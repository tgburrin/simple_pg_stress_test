package pgStressTest;

import java.sql.PreparedStatement;
import java.util.UUID;

public class UpdateTestMethod extends TestMethodType {

	UpdateTestMethod(String URI) {super(URI);}

	@Override
	public void runTest(int threadId) throws Exception {
		System.out.println("Running "+ops+" operations in thread "+threadId);
		openConnection();
		UUID id = UUID.randomUUID();

		PreparedStatement ps = dbc.prepareStatement("delete from public.pg_stress_test where id = ?::uuid");
		ps.setString(1, id.toString());
		ps.executeUpdate();
		ps.close();

		ps = dbc.prepareStatement("insert into public.pg_stress_test (id) values (?::uuid)");
		ps.setString(1, id.toString());
		ps.executeUpdate();
		ps.close();

		dbc.commit();

		ps = dbc.prepareStatement("update public.pg_stress_test set total = total+1 where id = ?::uuid");
		for(int i=0; i<ops; i++) {
			ps.setString(1, id.toString());
			ps.executeUpdate();
			if ( (i+1) % batchSize == 0)
				dbc.commit();
		}
		dbc.commit();
		ps.close();
		closeConnection();
	}

}
