# lauch ELK and DSE
This repository show how to monitore DSE with ELK. JMX with insight monitoring (https://docs.datastax.com/en/dse/6.7/dse-dev/datastax_enterprise/tools/metricsCollector/mcIntroduction.html) DSE 6.7 -> collectd -> logstash and Cassandra log -> filebeat.
I don't use docker logs features, docker is used only to provide an easy way to create a DSE and ELK cluster.

## download filebeat locally
curl -L -o bin/filebeat-6.5.4-amd64.deb https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-6.5.4-amd64.deb

## Launch the DSE and ELK clusters
`docker-compose up -d`

## display indices from ES
`curl -X GET 'http://localhost:9200/_cat/indices'`

## bash into node1
`docker exec -it --user=root elk_node1 bash`

## logs
`docker-compose logs`

## kibana URL
http://localhost:5601/app/kibana

# enable filebeat
```bash
docker exec -it --user=root elk_node1 bash
cat filebeat/filebeat_install.sh | bash
```

```bash
docker exec -it --user=root elk_node2 bash
cat filebeat/filebeat_install.sh | bash
```


# enable collectd 
```bash
docker exec -it --user=root elk_node1 bash
cat > /opt/dse/resources/dse/collectd/etc/collectd/10-statsd.conf <<EOF
LoadPlugin network
<Plugin network>
    <Server "logstash" "25826">
    </Server>
</Plugin>
EOF

dsetool insights_config --mode DISABLED
dsetool insights_config --mode ENABLED_WITH_LOCAL_STORAGE
```


idem for node2


## generate Data
```bash
cd generateData
mvn clean compile
mvn exec:java -Dexec.mainClass="com.datastax.spark.example.WriteRead"
```

https://datastax.jira.com/browse/DSP-17319

https://datastax.jira.com/browse/DSP-17869







