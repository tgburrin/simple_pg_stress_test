package pgStressTest;

import java.sql.PreparedStatement;
import java.util.UUID;

public class UpdateOneTestMethod extends TestMethodType {
	private static UUID nil = new UUID( 0 , 0 );

	UpdateOneTestMethod(String URI) {super(URI);}

	@Override
	public void runTest(int threadId) throws Exception {
		System.out.println("Running "+ops+" operations in thread "+threadId);
		openConnection();
		PreparedStatement ps = dbc.prepareStatement("update public.pg_stress_test set total = total+1 where id = ?::uuid");
		for(int i=0; i<ops; i++) {
			ps.setString(1, nil.toString());
			ps.executeUpdate();
			if ( (i+1) % batchSize == 0)
				dbc.commit();
		}
		dbc.commit();
		ps.close();

		closeConnection();
	}

}
