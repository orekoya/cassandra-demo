package com.datastax.spark.example

import java.util

import com.datastax.spark.connector._
import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.sql.SparkSession

object WriteRead extends App {

  val random = new util.Random

  val subset: Array[Char] = "0123456789abcdefghijklmnopqrstuvwxyzAZERTYUIOPMLKJHGFDSQWXCVBN".toCharArray

  def randomStr(length: Int): String = {
    val buf = new Array[Char](length)
    for (i <- 0 to buf.length - 1) {
      val index = random.nextInt(subset.length)
      buf(i) = subset(index)
    }
    new String(buf)
  }

  val spark = SparkSession.builder
    .appName("Datastax Scala example")
    .master("local[2]")
    .config("spark.cassandra.connection.host", "localhost")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")


  import spark.implicits._

  // Create keyspace and table
  CassandraConnector(spark.sparkContext).withSessionDo { session =>
    session.execute(
      """CREATE KEYSPACE IF NOT EXISTS ks WITH
        | replication = {'class': 'SimpleStrategy', 'replication_factor': 2 }""".stripMargin)
    session.execute("""CREATE TABLE IF NOT EXISTS ks.kv1 (id text, data text, PRIMARY KEY (id))""")
    session.execute("""CREATE TABLE IF NOT EXISTS ks.kv2 (id text, time text, data text, PRIMARY KEY (id,time))""")

  }

  // Write some data
  spark.range(1, 100)
    .map(x => (x, randomStr(100)))
    .rdd
    .saveToCassandra("ks", "kv1")


  spark.range(1, 100)
    .map(x => (4 , x, randomStr(2000000)))
    .rdd
    .saveToCassandra("ks", "kv2")

  spark.stop()
  sys.exit(0)
}
