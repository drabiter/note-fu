package com.drabiter.notefu;

import java.awt.Point;

import com.drabiter.notefu.GUI.Notification;
import com.drabiter.notefu.data.NoteManager;
import com.drabiter.notefu.dropbox.DBManager;
import com.drabiter.notefu.listeners.MenuListener;
import com.drabiter.notefu.listeners.SearchFieldListener;
import com.drabiter.notefu.utils.Config;

public class Application {
	
	private SearchFieldListener searchFieldListener;
	private MenuListener menuListener;
	
	private static String workNote = "", lastNote;
	
	private Config config = null;
	private NoteManager noteMan = null;
	private DBManager dbMan;
	
	private GUI gui;
	
	public Application(){
		noteMan = NoteManager.load();
		
		config = Config.load();
		if (config == null) config = new Config();
		
		dbMan = new DBManager(this);
		
		searchFieldListener = new SearchFieldListener(this);
		menuListener = new MenuListener(this);
		
		gui = new GUI(this);
		gui.setNoteList(noteMan.getListModel());
		gui.setNote(0);
		gui.setEditorContent(noteMan.getContent(0));
		gui.setStatus((dbMan.isLinked()) ? "Logged In."
				: "Not Logged In.");
		gui.setCurrentNote(gui.getSelectedNote());
		gui.focusOnEditor();
	}
	
	public void newNote(String newNoteName){
		gui.setNote(noteMan.addNewNote(newNoteName));
		gui.setEditorContent(null);
//		noteMan.persist();
		saveCurrent();
		gui.showTrayNotif(Notification.NewNote, newNoteName);
	}
	
	public void delete(){
		noteMan.delete(gui.getSelectedNote());
		gui.setNoteList(noteMan.getListModel());
		if (noteMan.getSize() > 0){
			gui.setNote(0);
			gui.setEditorContent(noteMan.getContent(0));
		}
	}
	
	public void prevNote(){
		int targetIndex = gui.getSelectedIndex() + 1;
		if (targetIndex >= noteMan.getListModel().getSize()) return;
		toNoteIndex(targetIndex);
	}
	
	public void nextNote(){
		int targetIndex = gui.getSelectedIndex() - 1;
		if (targetIndex < 0) return;
		toNoteIndex(targetIndex);
	}
	
	public void newestNote(){
		toNoteIndex(0);
	}
	
	public void oldestNote(){
		toNoteIndex(noteMan.getListModel().getSize() - 1);
	}
	
	public void toNoteIndex(int targetIndex){
		gui.setNote(targetIndex);
		gui.setEditorContent(noteMan.getContent(targetIndex));
		gui.setCurrentNote(gui.getSelectedNote());
		gui.focusOnEditor();
	}
	
	public void saveLastCurrent() {
		// TODO Auto-generated method stub
		if (lastNote != null){
//			System.out.println(lastNote + " ** " + gui.getEditorText());
			noteMan.saveContent(lastNote, gui.getEditorText());
		}
	}
	
	public void saveCurrent(){
		noteMan.saveContent(gui.getSelectedNote(), gui.getEditorText());
	}
	
	public void search(){
		Point point = noteMan.search(gui.getSearchText().toCharArray());
		if (point.x >= 0) gui.highlight(noteMan.getContent(point.x), point);
	}
	
	public void push(){
//		noteMan.persist();
		saveCurrent();
		if (dbMan.push()) gui.showTrayNotif(Notification.Pushed, null);
	}

	public void pull() {
		// TODO Auto-generated method stub
		if (dbMan.pull()){
			noteMan = NoteManager.load();
			gui.setNoteList(noteMan.getListModel());
			gui.setNote(0);
			gui.setEditorContent(noteMan.getContent(0));
			gui.showTrayNotif(Notification.Pulled, null);
		}
	}
	
	public void login(){
		dbMan.login();
	}
	
	public String accountInfo() {
		// TODO Auto-generated method stub
		return dbMan.accountInfo();
	}
	
	public void refreshEditorNote(){
		gui.setEditorContent(noteMan.getContent(gui.getSelectedIndex()));
		gui.focusOnEditor();
	}

	public static boolean setWorkNote(String workNote) {
		if (Application.workNote.equals(workNote)) return false;
		Application.lastNote = Application.workNote;
		Application.workNote = workNote;
		return true;
	}

	public SearchFieldListener getSearchFieldListener() {
		return searchFieldListener;
	}
	
	public MenuListener getMenuListener() {
		// TODO Auto-generated method stub
		return menuListener;
	}

	public Config getConfig() {
		return config;
	}

	public GUI getGui() {
		return gui;
	}
	
	public static String getWorkNote() {
		return workNote;
	}

	public static String getLastNote() {
		return lastNote;
	}

	public void hide() {
		saveCurrent();
		
		config.xWinPos = gui.getContainer().getX();
		config.yWinPos = gui.getContainer().getY();
		config.lastNoteIndex = gui.getSelectedIndex();
		
//		noteMan.persist();
		config.persist();
	}

	public void exit() {
		// TODO Auto-generated method stub
		hide();
		gui.exit();
	}
}
