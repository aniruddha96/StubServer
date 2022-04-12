package com.vizier.stub;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.util.FileSystemUtils;

public class BackgroundStubGen implements Runnable {

	ServerState state;

	public BackgroundStubGen(ServerState state) {
		super();
		this.state = state;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		for (String s : state.installedPackages) {
			
			String sLC = s.toLowerCase();
			
			if(TypeshedHandler.getTypeshedList().contains(sLC)) {
				System.out.println(s+" from typeshed");
				fetchPreBuildStubs("types-"+sLC);
			}
			else if(s.toLowerCase().equals("pandas")) {
				fetchPreBuildStubs("pandas-stubs");
			}else {
				System.out.println("generating for "+s);
				createStubsFor(s);
			}
			
		}
		createVizierDBStubs();
		Path source = Paths.get("out");
		try {
			createTarGzipFolder(source);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
		System.out.println("total time : "+(System.currentTimeMillis()-start));


	}

	private void createTarGzipFolder(Path source) throws IOException {
		String tarFileName = "all.tar.gz";

		try (OutputStream fOut = Files.newOutputStream(Paths.get(tarFileName));
				BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
				GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
				TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

			Files.walkFileTree(source, new SimpleFileVisitor<>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
					if (attributes.isSymbolicLink()) {
						return FileVisitResult.CONTINUE;
					}
					Path targetFile = source.relativize(file);
					try {
						TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), targetFile.toString());
						tOut.putArchiveEntry(tarEntry);
						Files.copy(file, tOut);
						tOut.closeArchiveEntry();
						//System.out.printf("file : %s%n", file);
					} catch (IOException e) {
						System.err.printf("Unable to tar.gz : %s%n%s%n", file, e);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					System.err.printf("Unable to tar.gz : %s%n%s%n", file, exc);
					return FileVisitResult.CONTINUE;
				}

			});

			tOut.finish();
		}
	}

	private void createVizierDBStubs() {
		try {
			File sourceDirectory = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "pycell");
			File destinationDirectory = new File("out"+ File.separator + "pycell");
			FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
		}catch (IOException e){ote
			System.out.println("Exception while generating VizierDB stubs");
		}
	}

	private void createStubsFor(String libname) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("stubgen", "-p", libname);
			Process process = processBuilder.start();
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int exitVal = process.waitFor();
			//System.out.println(output);
		} catch (IOException | InterruptedException e) {
			System.out.println("Exception while generating stubs for "+libname);
		}	
	}
	
	private void fetchPreBuildStubs(String packageName) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("pip","install","--target=out",packageName);
			Process process = processBuilder.start();
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			
		} catch (IOException e) {
			System.out.println("Exception while generating stubs for "+packageName);
		}
	}

	
}
