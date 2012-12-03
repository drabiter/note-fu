package com.drabiter.notefu.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class DataWriterReader {

	private static final int BUFFER = (int) Math.pow(1024, 2);

	public static String extension = ".txt";

	public static void saveNote(String name, String content, String parentPath) {
		try {
			content = content.replaceAll("\\n",
					System.getProperty("line.separator"));

			File filesPath = new File(parentPath);
			if (!filesPath.exists())
				filesPath.mkdir();
			File file = new File(parentPath, (name.endsWith(extension)) ? name
					: name.concat(extension));
			FileWriter writer = new FileWriter(file);
			BufferedWriter buff = new BufferedWriter(writer, BUFFER);
			buff.write(content);
			buff.flush();
			buff.close();
			System.out.println("save " + name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static HashMap<String, String> loadNotes(String defaultPath) {
		// TODO Auto-generated method stub
		HashMap<String, String> savedNotes = new HashMap<String, String>();

		File filesPath = new File(defaultPath);
		if (!filesPath.exists())
			filesPath.mkdir();
		if (filesPath.isDirectory()) {
			File[] files = filesPath.listFiles();
			Arrays.sort(files, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					// TODO Auto-generated method stub
					int ret = (int) (o1.lastModified() - o2.lastModified());
					System.out.println(ret + " " + o1.getName() + " "+o2.getName());
					return Math.abs(ret);
				}

			});

			for (int i = 0; i < files.length; i++) {
				if (files[i].canRead()) {
					savedNotes.put(
							files[i].getName().replaceFirst(extension, ""),
							readFile(files[i], BUFFER));
				}
			}
		}

		return savedNotes;
	}

	private static String readFile(File file, int buffer) {
		StringBuilder string = new StringBuilder();
		String line;
		try {
			FileReader writer = new FileReader(file);
			BufferedReader buff = new BufferedReader(writer, buffer);
			while ((line = buff.readLine()) != null) {
				string.append(line);
				if (!line.endsWith("\\n") && !line.endsWith("\\r")
						&& line.length() > 0)
					string.append(System.getProperty("line.separator"));
			}
			string.deleteCharAt(string.length() - 1);
			buff.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		return string.toString();
	}

	public static boolean deleteIfExists(String path, String delete) {
		// TODO Auto-generated method stub
		File file = new File(path, delete.concat(extension));
		if (file.exists() && file.canWrite())
			return file.delete();
		System.out.println(file.getPath());
		return false;
	}

}
