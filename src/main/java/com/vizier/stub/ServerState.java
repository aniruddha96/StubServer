package com.vizier.stub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerState {

	public List<String> installedPackages = new ArrayList<String>();
	
	public Set<String> alreadyGenerated = new HashSet<String>();
}
