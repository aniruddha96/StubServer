/**
 * 
 */
package com.vizier.stub.client.impl;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import com.vizier.stub.client.VizierStubServerClient;

/**
 * @author aniruddha
 *
 */
public class VizierStubServerClientImpl implements VizierStubServerClient {

	@Override
	public boolean getAllStubs(String serverAddress, String extractTo) throws MalformedURLException {
		URL url = new URL(serverAddress+"getallstubs");
		try (InputStream in = url.openStream();
				ReadableByteChannel rbc = Channels.newChannel(in);
				FileOutputStream fos = new FileOutputStream("temp.tar.gz")) {
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		Path ap = Paths.get("").toAbsolutePath().normalize();
		Path source = ap.resolve("temp.tar.gz").normalize();
		
		Path target = Paths.get(extractTo).toAbsolutePath().normalize();
		
		try {
			decompressTarGzipFile(source,target);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public static void decompressTarGzipFile(Path source, Path target)
	        throws IOException {

	        if (Files.notExists(source)) {
	            throw new IOException("File doesn't exists!");
	        }

	        try (InputStream fi = Files.newInputStream(source);
	             BufferedInputStream bi = new BufferedInputStream(fi);
	             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
	             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

	            ArchiveEntry entry;
	            while ((entry = ti.getNextEntry()) != null) {

	                // create a new path, zip slip validate
	            	Path newPath = zipSlipProtect(entry, target);

	                if (entry.isDirectory()) {
	                    Files.createDirectories(newPath);
	                } else {

	                    // check parent folder again
	                    Path parent = newPath.getParent();
	                    if (parent != null) {
	                        if (Files.notExists(parent)) {
	                            Files.createDirectories(parent);
	                        }
	                    }

	                    // copy TarArchiveInputStream to Path newPath
	                    Files.copy(ti, newPath, StandardCopyOption.REPLACE_EXISTING);

	                }
	            }
	        }
	    }
	
	private static Path zipSlipProtect(ArchiveEntry entry, Path targetDir)
	        throws IOException {

	        Path targetDirResolved = targetDir.resolve(entry.getName());

	        // make sure normalized file still has targetDir as its prefix,
	        // else throws exception
	        Path normalizePath = targetDirResolved.normalize();

	        if (!normalizePath.startsWith(targetDir)) {
	            throw new IOException("Bad entry: " + entry.getName());
	        }

	        return normalizePath;
	    }

}
