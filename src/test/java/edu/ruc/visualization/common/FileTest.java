package edu.ruc.visualization.common;

import java.io.File;

public class FileTest {

	public static void main(String[] args) {
		FileTest fTest = new FileTest();
		fTest.filePath();
	}

	private void filePath() {
		String aStr = this.getClass().getClassLoader()
				.getResource(("data/real_estate.csv")).getFile();
		File file = new File(aStr);
		System.out.println(file.getAbsolutePath());
	}
}
