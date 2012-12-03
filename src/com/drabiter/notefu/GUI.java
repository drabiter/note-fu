package com.drabiter.notefu;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import com.drabiter.notefu.gui.JSearchTextField;
import com.drabiter.notefu.listeners.HotkeyListener;
import com.drabiter.notefu.listeners.MenuListener;
import com.drabiter.notefu.listeners.MenuListener.Command;
import com.drabiter.notefu.utils.Config;
import com.tulskiy.keymaster.common.Provider;

public class GUI {

	public enum ActionKeys {
		NOTEUP, NOTEDOWN, FOCUSSEARCH, FORCESAVE, FOCUSEDITOR, FOCUSNOTE, PUSH, PULL;
	}

	public enum Notification {
		NewNote, Deleted, Pulled, Pushed;
	};

	private static JList noteList;
	private static JEditorPane editorPane;
	private static JSearchTextField searchField;
	private JLabel currentStatus, currentNote;
	private JFrame frame;
	private JDialog optionDialog;
	private TrayIcon trayIcon = null;
	private DefaultHighlightPainter dhp;

	private Application app;
	private JMenuItem pushAll;
	private JMenuItem pullAll;
	private JMenuItem dbLogin;

	private Provider provider;

	public GUI(Application app) {
		this.app = app;

		createAndShowGUI();
		registerWindowsKey();
	}

	public void exit() {
		//TODO Auto-generated method stub
		provider.reset();
		provider.stop();
		SystemTray.getSystemTray().remove(trayIcon);
		System.exit(0);
	}

	private void createAndShowGUI() {
		// TODO Auto-generated method stub
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		frame = new JFrame("Note-Fu 0.3 beta");
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		frame.setContentPane(createUI());
		frame.setJMenuBar(createMenuBar());
		Image img = Toolkit
				.getDefaultToolkit()
				.createImage(
						ClassLoader
								.getSystemResource("com/drabiter/notefu/gui/temp-logo.png"));
		frame.setIconImage(img);
		frame.pack();
		frame.setVisible(true);
		frame.setLocation(app.getConfig().xWinPos, app.getConfig().yWinPos);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				app.exit();
			}
		});

		dhp = new DefaultHighlightPainter(Color.yellow);

		createOptionDialog();
		createTray();
	}

	private void createOptionDialog() {
		// TODO Auto-generated method stub
		optionDialog = new JDialog(frame, "Option");
		optionDialog.setLocationRelativeTo(frame);
		optionDialog.setAlwaysOnTop(true);
		optionDialog.setMinimumSize(new Dimension(250, 100));

		JPanel panel = new JPanel(new FlowLayout());
		panel.setMinimumSize(optionDialog.getMinimumSize());

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				optionDialog.setVisible(false);
			}

		});

		optionDialog.add(panel, BorderLayout.CENTER);
		optionDialog.add(ok, BorderLayout.SOUTH);
		optionDialog.setVisible(false);
	}

	private JPanel createUI() {
		// TODO Auto-generated method stub
		JPanel mainPanel = new JPanel(new BorderLayout());

		JPanel searchPanel = new JPanel(new BorderLayout());
		searchField = new JSearchTextField();
		searchField.addActionListener(app.getSearchFieldListener());
		searchField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				editorPane.getHighlighter().removeAllHighlights();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		searchPanel.setMinimumSize(new Dimension(500, 25));
		searchPanel.add(searchField, BorderLayout.CENTER);

		noteList = new JList();
		noteList.setSelectedIndex(0);
		noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		noteList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (noteList.getSelectedValue() != null) {
					if (Application.setWorkNote(noteList.getSelectedValue()
							.toString())) {
						// TODO Auto-generated method stub
						app.saveLastCurrent();
						app.refreshEditorNote();
					}
				}
			}
		});

		JScrollPane noteListScroll = new JScrollPane(noteList);
		noteListScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		noteListScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		noteListScroll.setPreferredSize(new Dimension(150, 300));
		noteList.requestFocusInWindow();

		editorPane = new JEditorPane();
		editorPane.setFocusable(true);

		JScrollPane editorPaneScroll = new JScrollPane(editorPane);
		editorPaneScroll
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		editorPaneScroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				noteListScroll, editorPaneScroll);
		splitPane.setMinimumSize(new Dimension(500, 300));
		splitPane.setPreferredSize(new Dimension(500, 300));

		mainPanel.add(searchPanel, BorderLayout.PAGE_START);
		mainPanel.add(splitPane, BorderLayout.CENTER);
		mainPanel.add(createStatusBar(), BorderLayout.SOUTH);
		mainPanel.setOpaque(true);

		return mainPanel;
	}

	private JPanel createStatusBar() {
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panel.setPreferredSize(new Dimension(frame.getWidth(), 16));
		panel.setLayout(new BorderLayout());
		currentStatus = new JLabel();
		currentStatus.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(currentStatus, BorderLayout.WEST);
		currentNote = new JLabel();
		currentNote.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(currentNote, BorderLayout.EAST);
		return panel;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		MenuListener menuListener = app.getMenuListener();

		JMenu menu = new JMenu("Note");
		menu.add(createMenuItem("New ", Command.NEW, Config.newNote, menuListener));
		menu.add(createMenuItem("Delete ", Command.DEL, Config.delNote, menuListener));
		menu.addSeparator();
		menu.add(createMenuItem("Exit ", Command.EXIT, Config.exitApp, menuListener));
		menuBar.add(menu);

		menu = new JMenu("Nav");
		menu.add(createMenuItem("Next Note", Command.NEXT, Config.nextNote, menuListener));
		menu.add(createMenuItem("Prev Note", Command.PREV, Config.prevNote, menuListener));
		menu.addSeparator();
		menu.add(createMenuItem("Newest Note", Command.NEWEST, Config.newestNote, menuListener));
		menu.add(createMenuItem("Oldest Note", Command.OLDEST, Config.oldestNote, menuListener));
		menu.addSeparator();
		
		menu.add(createMenuItem("To Search", Command.FSEARCH, Config.focusSearch, menuListener));
		menu.add(createMenuItem("To Editor", Command.FEDITOR, Config.focusEditor, menuListener));
		menuBar.add(menu);

		menu = new JMenu("Dropbox");
		pushAll = createMenuItem("Push All", Command.PUSH_ALL, Config.dbPush, menuListener);
		menu.add(pushAll);
		pullAll = createMenuItem("Pull All", Command.PULL_ALL, Config.dbPull, menuListener);
		menu.add(pullAll);
		menu.addSeparator();
		dbLogin = createMenuItem("Login ", Command.LOGIN, Config.dbLogin, menuListener);
		menu.add(dbLogin);
		menuBar.add(menu);

		menuBar.add(Box.createHorizontalGlue());

		menu = new JMenu("About ");
		menu.add(createMenuItem("About ", Command.USER, null, menuListener));
		menu.add(createMenuItem("Note-Fu ", Command.ABOUT, null, menuListener));
		menuBar.add(menu);

		return menuBar;
	}
	
	private JMenuItem createMenuItem(String title, Command command, String keystroke, 
			MenuListener listener) {
		// TODO Auto-generated method stub
		JMenuItem item = new JMenuItem(title);
		item.setActionCommand(command.toString());
		item.addActionListener(listener);
		if (keystroke != null)
			item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
		return item;
	}

	private void createTray() {
		ImageIcon image = new ImageIcon(this.getClass().getClassLoader()
				.getResource("com/drabiter/notefu/gui/temp-logo.png"));
		trayIcon = new TrayIcon(image.getImage(), "myn");
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void registerWindowsKey() {
		provider = Provider.getCurrentProvider(false);
		HotkeyListener hotkeyListener = new HotkeyListener(app);
		provider.register(KeyStroke.getKeyStroke(Config.hideWindow), hotkeyListener);
	}

	public void highlight(String content, Point point) {
		if (point != null) {
			setNote(point.x);
			setEditorContent(content);
			try {
				editorPane.getHighlighter().removeAllHighlights();
				editorPane.getHighlighter().addHighlight(point.y,
						point.y + searchField.getText().length(), dhp);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setStatus(String status) {
		currentStatus.setText(status);
	}

	public void setCurrentNote(String noteTitle) {
		currentNote.setText(noteTitle);
	}

	public void showOptionDialog() {
		// TODO Auto-generated method stub
		optionDialog.setVisible(true);
	}

	public void showTrayNotif(Notification notif, String text) {
		trayIcon.displayMessage(notif.toString(), text, MessageType.INFO);
	}

	public String getSearchText() {
		// TODO Auto-generated method stub
		return searchField.getText();
	}

	public String getEditorText() {
		return editorPane.getText();
	}

	public String getSelectedNote() {
		return (String) noteList.getSelectedValue();
	}

	public int getSelectedIndex() {
		return noteList.getSelectedIndex();
	}

	public Component getContainer() {
		return frame;
	}

	public void focusOnEditor() {
		editorPane.requestFocus();
	}

	public void focusOnSearch() {
		searchField.requestFocus();
	}

	public void setNoteList(DefaultListModel listModel) {
		// TODO Auto-generated method stub
		noteList.setModel(listModel);
	}

	public void setEditorContent(String content) {
		// TODO Auto-generated method stub
		editorPane.setText(content);
	}

	public void setNote(int index) {
		// TODO Auto-generated method stub
		noteList.setSelectedIndex(index);
	}

}
