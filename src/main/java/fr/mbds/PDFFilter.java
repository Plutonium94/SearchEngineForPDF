package fr.mbds;

import java.io.*;

public class PDFFilter implements FileFilter {

	public PDFFilter() {}

	public boolean accept(File file) {
		return file.getName().endsWith(".pdf");
	}
}