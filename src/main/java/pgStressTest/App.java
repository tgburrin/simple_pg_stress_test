package pgStressTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

public class App extends Thread {

	private static String URI = "jdbc:postgresql:";
	private static int records = 50000;
	private static TestMethods m = TestMethods.INSERT;
	private static int batchSize = 1;
	private int threadId = 0;

	private App (int tid) {
		this.threadId = tid;
	}

	@Override
	public void run () {
		System.out.println("Thread "+this.threadId+" started");
		try {
			TestMethodType tmt = TestMethodTypeFactory.create(m, URI);
			tmt.setOperationsCount(records);
			tmt.setBatchSize(batchSize);
			if ( tmt == null )
				throw new Exception("Unsupported test type "+m.toString());
			//System.out.println("Starting test for "+m.toString());
			tmt.runTest(this.threadId);
			System.out.println("Thread "+this.threadId+" done");
		} catch (Exception e) {
			System.err.println("Error: "+e.toString());
		}
	}

	public static void setup() throws Exception {
		Class.forName("org.postgresql.Driver");
		Connection dbc = DriverManager.getConnection(URI);

		dbc.setAutoCommit(false);

		Statement cursor = dbc.createStatement();

		String tbl = "create table if not exists public.pg_stress_test (\n"
					+ "\tid uuid primary key,\n"
					+ "\ttotal bigint not null default 0,\n"
					+ "\tpayload jsonb not null default '{}'::jsonb\n"
					+ ")";

		cursor.execute(tbl);
		cursor.close();

		UUID nil = new UUID( 0 , 0 );

		PreparedStatement ps = dbc.prepareStatement("delete from public.pg_stress_test where id = ?::uuid");
		ps.setString(1, nil.toString());
		ps.executeUpdate();
		ps.close();

		if(m == TestMethods.UPDATE_ONE) {
			ps = dbc.prepareStatement("insert into public.pg_stress_test (id) values (?::uuid)");
			ps.setString(1, nil.toString());
			ps.executeUpdate();
			ps.close();
		}

		dbc.commit();
		dbc.close();
	}

	public static void main(String[] args) throws Exception {
		int threads = 10;
		ArrayList<App> children = new ArrayList<App>();

		Hashtable<String,String> cmd = GetOpts.getopts("ht:r:m:b:", args);

		if ( cmd.containsKey("opt_t") ) {
			try {
				int t = Integer.parseInt(cmd.get("opt_t"));
				if ( t > 0 )
					threads = t;
				else
					throw new NumberFormatException("Value may not be <= 0");
			} catch (NumberFormatException e) {
				System.err.println("Invalid thread count '"+cmd.get("opt_t")+"' using default value "+threads);
			}
		}

		if ( cmd.containsKey("opt_r") ) {
			try {
				int r = Integer.parseInt(cmd.get("opt_r"));
				if ( r > 0 )
					records = r;
				else
					throw new NumberFormatException("Value may not be <= 0");
			} catch (NumberFormatException e) {
				System.err.println("Invalid record count '"+cmd.get("opt_r")+"' using default value "+records);
			}
		}

		if ( cmd.containsKey("opt_b") ) {
			try {
				int b = Integer.parseInt(cmd.get("opt_b"));
				if ( b > 0 )
					batchSize = b;
				else
					throw new NumberFormatException("Value may not be <= 0");
			} catch (NumberFormatException e) {
				System.err.println("Invalid batch size '"+cmd.get("opt_b")+"' using default value "+batchSize);
			}
		}

		if ( cmd.containsKey("opt_m"))
			m = TestMethods.valueOf(cmd.get("opt_m").toUpperCase());

		setup();
		System.out.println("Threads: "+threads);

		long starttime = System.nanoTime();
		for(int i=0; i<threads; i++) {
			App a = new App((i+1));
			a.start();
			children.add(a);
		}

		for(App a : children)
			a.join();

		long endtime = System.nanoTime();

		System.out.println("Runtime: "+String.format("%.3f", (endtime - starttime)/1000000000.0)+"s");
		System.out.println("Rec/Sec: "+String.format("%.3f", (records * threads)/((endtime - starttime)/1000000000.0))+"rps");

	}
}
