package com.vizier.stub.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StubsController implements InitializingBean{

	private Path fileStorageLocation = null;
	
	@GetMapping("/getallstubs")
	public ResponseEntity<Resource> downloadStubs(HttpServletRequest request) {
		Resource resource = loadFile("all.tar.gz");
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	public Resource loadFile(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists()) {
				return resource;
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		fileStorageLocation=Paths.get("")
        .toAbsolutePath().normalize();
		
	}

}
