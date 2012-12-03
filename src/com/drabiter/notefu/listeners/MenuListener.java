package com.drabiter.notefu.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.drabiter.notefu.Application;

public class MenuListener implements ActionListener {

	public static final String ABOUT = "Simplest way to take note.\n\nHendra G. (https://github.com/drabiter/note-fu)";

	public enum Command {
		EXIT, USER, ABOUT, DEL, NEW, PUSH_ALL, PULL_ALL, PREF, LOGIN, NEXT, PREV, PUSH_ONE, PULL_ONE, FSEARCH, FEDITOR, OLDEST, NEWEST;
	}

	private Application app;

	public MenuListener(Application app) {
		this.app = app;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (Command.NEW.toString().equals(e.getActionCommand())) {
			app.newNote(JOptionPane.showInputDialog("Note name : "));
		} else if (Command.USER.toString().equals(e.getActionCommand())) {
			JOptionPane.showMessageDialog(app.getGui().getContainer(),
					app.accountInfo(), "Author", JOptionPane.PLAIN_MESSAGE);
		} else if (Command.ABOUT.toString().equals(e.getActionCommand())) {
			JOptionPane.showMessageDialog(app.getGui().getContainer(), ABOUT,
					"Note-Fu", JOptionPane.PLAIN_MESSAGE);
		} else if (Command.DEL.toString().equals(e.getActionCommand())) {
			app.delete();
		} else if (Command.PUSH_ALL.toString().equals(e.getActionCommand())) {
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					app.push();
				}
			}.start();
		} else if (Command.PULL_ALL.toString().equals(e.getActionCommand())) {
			new Thread() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					app.pull();
				}
			}.start();
		} else if (Command.LOGIN.toString().equals(e.getActionCommand())) {
			app.login();
		} else if (Command.PREV.toString().equals(e.getActionCommand())) {
			app.prevNote();
		} else if (Command.NEXT.toString().equals(e.getActionCommand())) {
			app.nextNote();
		} else if (Command.OLDEST.toString().equals(e.getActionCommand())) {
			app.oldestNote();
		} else if (Command.NEWEST.toString().equals(e.getActionCommand())) {
			app.newestNote();
		} else if (Command.FSEARCH.toString().equals(e.getActionCommand())) {
			app.getGui().focusOnSearch();
		} else if (Command.FEDITOR.toString().equals(e.getActionCommand())) {
			app.getGui().focusOnEditor();
		} else if (Command.PREF.toString().equals(e.getActionCommand())) {
			app.getGui().showOptionDialog();
		} else if (Command.EXIT.toString().equals(e.getActionCommand())) {
			app.exit();
		}
	}

}
