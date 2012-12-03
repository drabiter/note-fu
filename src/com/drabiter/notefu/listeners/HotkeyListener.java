package com.drabiter.notefu.listeners;

import com.drabiter.notefu.Application;
import com.drabiter.notefu.utils.Config;
import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

public class HotkeyListener implements HotKeyListener {

	private Application app;

	public HotkeyListener(Application app) {
		// TODO Auto-generated constructor stub
		this.app = app;
	}

	@Override
	public void onHotKey(HotKey arg0) {
		// TODO Auto-generated method stub
//		System.out.println(arg0.keyStroke.toString());
		if (Config.hideWindow.equals(arg0.keyStroke)){
			if (app.getGui().getContainer().isVisible()) app.hide();
			app.getGui().getContainer().setVisible(!app.getGui().getContainer().isVisible());
		}
//		else if (identifier == 2){
//			app.nextNote();
//		}
	}

}
