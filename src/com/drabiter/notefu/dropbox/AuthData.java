package com.drabiter.notefu.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class AuthData implements Serializable {
	
	static final long serialVersionUID = 123L;
	
	private static final String PATH = "user.account";
	
	private AppKeyPair keyPair;
	private AccessTokenPair tokenPair;
	
	public AuthData(AppKeyPair keyPair, AccessTokenPair tokenPair) {
		this.keyPair = keyPair;
		this.tokenPair = tokenPair;
		
		persist();
	}
	
	public void persist() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(PATH);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public AppKeyPair getKeyPair() {
		return keyPair;
	}

	public AccessTokenPair getTokenPair() {
		return tokenPair;
	}
	
	public static AuthData load() {
		// TODO Auto-generated method stub
		AuthData ret = null;
		File file = new File(PATH);
		if (!file.exists() || !file.canRead()) return ret;
		try {
			FileInputStream fis = new FileInputStream(PATH);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ret = (AuthData) ois.readObject();
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
