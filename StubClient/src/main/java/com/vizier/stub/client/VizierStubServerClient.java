package com.vizier.stub.client;

import java.net.MalformedURLException;

public interface VizierStubServerClient {

	public boolean getAllStubs(String serverAddress, String extractTo) throws MalformedURLException;
}
