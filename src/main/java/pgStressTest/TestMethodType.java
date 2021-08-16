package pgStressTest;

import java.sql.Connection;
import java.sql.DriverManager;

enum TestMethods {
	INSERT, UPDATE, UPDATE_ONE;
}

public abstract class TestMethodType {
	protected Connection dbc;
	private String URI;
	protected int ops = 0;
	protected int batchSize = 1;

	protected TestMethodType (String URI, int dmlOps) {
		this.URI = URI;
		this.ops = dmlOps;
	}

	protected TestMethodType (String URI) {
		this.URI = URI;
	}

	protected void openConnection () throws Exception {
		Class.forName("org.postgresql.Driver");
		dbc = DriverManager.getConnection(URI);
		dbc.setAutoCommit(false);
	}

	protected void closeConnection() throws Exception {
		if ( dbc != null )
			dbc.close();
	}

	public void setOperationsCount(int ops) {
		this.ops = ops;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	abstract public void runTest(int threadId) throws Exception;
}
