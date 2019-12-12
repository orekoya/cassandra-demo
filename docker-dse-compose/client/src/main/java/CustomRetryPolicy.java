

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.WriteType;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.policies.RetryPolicy;

public class CustomRetryPolicy implements RetryPolicy {

	public static final CustomRetryPolicy INSTANCE = new CustomRetryPolicy();

	private CustomRetryPolicy() {
	}

	@Override
	public RetryDecision onReadTimeout(Statement statement, ConsistencyLevel cl, int requiredResponses,
			int receivedResponses, boolean dataRetrieved, int nbRetry) {
		if (nbRetry != 0) {
			System.out.println("NB_RETRY onReadTimeout ================>" + nbRetry);
			return RetryDecision.rethrow();
		}
		System.out.println("onReadTimeout: Current Consistency Level= " + cl + " \n NbRetry= " + nbRetry);
		return RetryDecision.tryNextHost(cl == ConsistencyLevel.EACH_QUORUM ? ConsistencyLevel.LOCAL_QUORUM : cl);
	}

	@Override
	public RetryDecision onWriteTimeout(Statement statement, ConsistencyLevel cl, WriteType writeType, int requiredAcks,
			int receivedAcks, int nbRetry) {
		if (nbRetry != 0) {
			System.out.println("NB_RETRY onWriteTimeout ================>" + nbRetry);
			return RetryDecision.rethrow();
		}
		System.out.println("onWriteTimeout: Current Consistency Level= " + cl + " \n NbRetry= " + nbRetry);
		return RetryDecision.tryNextHost(cl == ConsistencyLevel.EACH_QUORUM ? ConsistencyLevel.LOCAL_QUORUM : cl);
	}

	@Override
	public RetryDecision onUnavailable(Statement statement, ConsistencyLevel cl, int requiredReplica, int aliveReplica,
			int nbRetry) {
		if (nbRetry != 0) {
			System.out.println("NB_RETRY onUnavailable ================>" + nbRetry);
			return RetryDecision.rethrow();
		}
		System.out.println("onUnavailable: Current Consistency Level= " + cl + " \n NbRetry= " + nbRetry);
		return RetryDecision.tryNextHost(cl == ConsistencyLevel.EACH_QUORUM ? ConsistencyLevel.LOCAL_QUORUM : cl);
	}

	@Override
	public RetryDecision onRequestError(Statement statement, ConsistencyLevel cl, DriverException e, int nbRetry) {
		if (nbRetry != 0) {
			System.out.println("NB_RETRY onRequestError ================>" + nbRetry);
			return RetryDecision.rethrow();
		}
		System.out.println("onRequestError: Current Consistency Level= " + cl + " \n NbRetry= " + nbRetry);
		return RetryDecision.tryNextHost(cl == ConsistencyLevel.EACH_QUORUM ? ConsistencyLevel.LOCAL_QUORUM : cl);
	}

	@Override
	public void init(Cluster cluster) {
		// nothing to do
	}

	@Override
	public void close() {
		// nothing to do
	}

}
