package com.onlineBookStore.BooksStore.HelperClasses;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

public class ImageSaver {
	static public boolean save(MultipartFile file, String filePath, String fileName) {
		boolean flag = false;
		try {
			String path = new ClassPathResource(filePath).getFile().getAbsolutePath();
			System.out.println(path);
			Files.copy(file.getInputStream(), Paths.get(path + File.separator + fileName),
					StandardCopyOption.REPLACE_EXISTING);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return flag;
	}
}
