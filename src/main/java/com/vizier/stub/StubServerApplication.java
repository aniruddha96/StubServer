package com.vizier.stub;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StubServerApplication {

	
	public static void main(String[] args) {
		// SpringApplication.run(StubServerApplication.class, args);

		SpringApplication application = new SpringApplication(StubServerApplication.class);
		addInitHooks(application);
		application.run(args);
	}
	
	static void addInitHooks(SpringApplication application) {	
		ProcessBuilder processBuilder = new ProcessBuilder();	
		File file = new File("out");
		if(!file.exists()){
            file.mkdirs();
        }
		TypeshedHandler.getTypeshedList();
		processBuilder.command("pip", "freeze");
		ServerState state = new ServerState();
		try {
			Process process = processBuilder.start();
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int exitVal = process.waitFor();

			for(String s : output.toString().split("\n")) {
				if(s.contains("==")) {
					String[] nameAndVersion = s.split("==");
					state.installedPackages.add(nameAndVersion[0]);
				}else if(s.contains("@")) {
					String[] nameAndLocation = s.split("@");
					state.installedPackages.add(nameAndLocation[0]);
				}
			}
			new BackgroundStubGen(state).runStubGen();
			System.out.println(state.installedPackages);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			}

}
