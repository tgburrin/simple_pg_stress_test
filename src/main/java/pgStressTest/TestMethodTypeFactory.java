package pgStressTest;

public class TestMethodTypeFactory {

	public static TestMethodType create (TestMethods method, String URI) {
		TestMethodType rv = null;
		if ( method == TestMethods.INSERT )
			rv =  new InsertTestMethod(URI);

		else if ( method == TestMethods.UPDATE )
			rv = new UpdateTestMethod(URI);

		else if ( method == TestMethods.UPDATE_ONE )
			rv = new UpdateOneTestMethod(URI);

		return rv;
	}
}
