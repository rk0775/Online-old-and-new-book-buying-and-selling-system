package com.onlineBookStore.BooksStore.HelperClasses;

import java.io.File;

import org.springframework.core.io.ClassPathResource;

public class ImageDelete {
	static public boolean delete(String filePath, String fileName) {
		boolean flag = false;
		try {
			String path = new ClassPathResource(filePath).getFile().getAbsolutePath();
			System.out.println(path);
			File file = new File(path + File.separator + fileName);
			if (file.exists()) {
				file.delete();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return flag;
	}
}
