import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		QueryOptions queryOptions = new QueryOptions().setConsistencyLevel(ConsistencyLevel.EACH_QUORUM)
				.setDefaultIdempotence(true).setFetchSize(Integer.MAX_VALUE);
		  LoadBalancingPolicy loadBalancingPolicy = new TokenAwarePolicy(
	                DCAwareRoundRobinPolicy.builder().withLocalDc("DC1").withUsedHostsPerRemoteDc(2).allowRemoteDCsForLocalConsistencyLevel().build());
		DseCluster.Builder builder = DseCluster.builder().withQueryOptions(queryOptions).addContactPoints("seed_node_DC1").withLoadBalancingPolicy(loadBalancingPolicy).withRetryPolicy(CustomRetryPolicy.INSTANCE);
		DseCluster cluster = builder.build();
		DseSession session = cluster.connect();
		Statement st = new SimpleStatement("create KEYSPACE IF NOT EXISTS ks WITH replication = {'class': 'NetworkTopologyStrategy' , 'DC1': 3, 'DC2': 3 };");
		session.execute(st);
		st = new SimpleStatement("create TABLE IF NOT EXISTS ks.test ( id text PRIMARY KEY , data text ); ");
		session.execute(st);
		st = new SimpleStatement("INSERT INTO ks.test ( id , data ) VALUES ('1','data'); ");
		session.execute(st);		
		while (true) {
			st = new SimpleStatement("select * from ks.test where id='1'");
			st.setIdempotent(true);
			ResultSet rows = session.execute(st);
			for (Row row : rows) {
				System.out.println(row.getString("id"));
				System.out.println(row.getString("data"));
			}
			Thread.sleep(1000);
		}

	}
}
