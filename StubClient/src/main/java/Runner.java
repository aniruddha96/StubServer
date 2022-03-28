import java.net.MalformedURLException;

import com.vizier.stub.client.impl.VizierStubServerClientImpl;

public class Runner {

	public static void main(String[] args) {
		try {
			new VizierStubServerClientImpl().getAllStubs("http://localhost:8080/", "D:\\temp\\stubs");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
