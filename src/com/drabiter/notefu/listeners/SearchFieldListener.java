package com.drabiter.notefu.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.drabiter.notefu.Application;

public class SearchFieldListener implements ActionListener {
	
	private Application app;

	public SearchFieldListener(Application main) {
		this.app = main;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		app.search();
	}

}
