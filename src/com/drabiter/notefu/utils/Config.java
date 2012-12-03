package com.drabiter.notefu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Config implements Serializable {
	
	static final long serialVersionUID = 5552L;

	public static final String PATH = "user.config";
	
	public int xWinPos = 0;
	public int yWinPos = 0;
	public int lastNoteIndex = 0;
	
	public static String hideWindow = "alt Z ",
			newNote = "control N ",
			delNote = "control D ",
			option = "control P ",
			exitApp = "alt F4 ",
			nextNote = "control UP ",
			prevNote = "control DOWN ",
			newestNote = "control shift UP ",
			oldestNote = "control shift DOWN ",
			focusSearch = "alt S ",
			focusEditor = "alt E ",
			dbPush = "control shift S ",
			dbPull = "control shift O ",
			dbLogin = "control shift L "
			;
	
	public Config(){
		persist();
	}

	public void persist() {
		try {
			FileOutputStream fos = new FileOutputStream(PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static Config load() {
		Config ret = null;
		try {
			File file = new File(PATH);
			if (!file.exists() || !file.canRead()) return ret;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ret = (Config) ois.readObject();
			System.out.println("load config!");
			ois.close();
			fis.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

}
