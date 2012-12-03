package com.drabiter.notefu.dropbox;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import javax.swing.JOptionPane;

import com.drabiter.notefu.Application;
import com.drabiter.notefu.data.NoteManager;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DBManager {

	final static private String APP_KEY = "yrr86eo2g94v5r4"; // "41yo462c4dbp2fc";
	final static private String APP_SECRET = "jc946f89dm9xqug"; // "ikm817pa880w9wv";

	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private DropboxAPI<WebAuthSession> mDBApi;
	private AuthData authData;

	private Application app;

	AppKeyPair appKeys;
	WebAuthSession session;

	public DBManager(Application app) {
		super();
		this.app = app;
	}

	public boolean login() {
		try {
			AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
			WebAuthSession was = new WebAuthSession(appKeyPair, ACCESS_TYPE);
			WebAuthSession.WebAuthInfo info = was.getAuthInfo();

			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(URI.create(info.url));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JOptionPane
					.showMessageDialog(
							app.getGui().getContainer(),
							"Log in to your dropbox account and approve Myn on the opened page.\nAfter you done it, press OK.",
							"Auth", JOptionPane.PLAIN_MESSAGE);

			app.getGui().setStatus("Checking...");
			was.retrieveWebAccessToken(info.requestTokenPair);
			AccessTokenPair accessToken = was.getAccessTokenPair();
			authData = new AuthData(appKeyPair, accessToken);
			authData.persist();
			app.getGui().setStatus("Logged In.");
			return true;
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			app.getGui().setStatus("Couldn't connect to server.");
		}
		return false;
	}

	public boolean push() {
		if (!isLinked())
			return false;

		FileInputStream inputStream = null;
		try {
			app.getGui().setStatus("Uploading...");
			File[] files = new File(NoteManager.defaultPath).listFiles();
			for (int i = 0; i < files.length; i++) {
				inputStream = new FileInputStream(files[i]);
				mDBApi.putFileOverwrite("/".concat(files[i].getName()),
						inputStream, files[i].length(), null);
				app.getGui().setStatus("Uploaded ".concat(files[i].getName()));
				inputStream.close();
			}
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			app.getGui().setStatus("Files not found.");
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			app.getGui().setStatus("Couldn't connect to server.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			app.getGui().setStatus("IO errors.");
		}
		return false;
	}

	public boolean pull() {
		// TODO Auto-generated method stub
		if (!isLinked())
			return false;

		File file;
		FileOutputStream outputStream = null;
		try {
			Entry entry = mDBApi.metadata("/", 0, null, true, null);
			for (Entry e : entry.contents) {
				if (!e.isDir) {
					app.getGui().setStatus("Downloading...");
					file = new File(
							NoteManager.defaultPath.concat(e.fileName()));
					outputStream = new FileOutputStream(file);
					DropboxFileInfo info = mDBApi.getFile(
							"/".concat(e.fileName()), null, outputStream, null);
					app.getGui().setStatus("Downloaded ".concat(info.getMetadata()
							.fileName()));
					outputStream.close();
				}
			}
			return true;
		} catch (DropboxException e) {
			e.printStackTrace();
			app.getGui().setStatus("Couldn't connect to server.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			app.getGui().setStatus("Files not found.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			app.getGui().setStatus("IO errors.");
		}
		return false;
	}

	public String accountInfo() {
		// TODO Auto-generated method stub
		if (!isLinked())
			return "You haven't been logged yet.\nPress Dropbox -> Login.";

		try {
			Account account = mDBApi.accountInfo();
			StringBuilder user = new StringBuilder(account.displayName);
			user.append("\nCountry : ");
			user.append(account.country);
			user.append("\nUsage   : ");
			user.append(account.quota);
			return user.toString();
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean isLinked() {
		authData = AuthData.load();
		if (authData == null)
			// link();
			return false;

		WebAuthSession session = new WebAuthSession(authData.getKeyPair(),
				ACCESS_TYPE);
		session.setAccessTokenPair(authData.getTokenPair());
		mDBApi = new DropboxAPI<WebAuthSession>(session);
		if (!session.isLinked())
			// link();
			return false;
		return true;
	}

}
