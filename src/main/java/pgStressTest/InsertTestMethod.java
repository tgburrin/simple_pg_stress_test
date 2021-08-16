package pgStressTest;

import java.sql.PreparedStatement;
import java.util.UUID;

public class InsertTestMethod extends TestMethodType {
	private static String payload = "{\"msg\": \"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}";

	InsertTestMethod(String URI) {super(URI);}

	@Override
	public void runTest(int threadId) throws Exception {
		System.out.println("Running "+ops+" operations in thread "+threadId);
		openConnection();
		PreparedStatement ps = dbc.prepareStatement("insert into public.pg_stress_test (id, payload) values (?::uuid, ?::jsonb)");
		for(int i=0; i<ops; i++) {
			UUID id = UUID.randomUUID();
			ps.setString(1, id.toString());
			ps.setString(2, payload);
			ps.execute();
			if ( (i+1) % batchSize == 0)
				dbc.commit();
		}
		dbc.commit();
		ps.close();

		closeConnection();
	}

}
