package learning_software;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

// MainWindow
// Handles the construction of the application's main window and all its contents
// (menu, tabbed pane, etc.)
// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, November 6th, 2015
public class MainWindow extends JFrame {

	static final long serialVersionUID = 0;
	static final String NAME_OF_SETTINGS_FILE = "eb_generalsettings.txt";
	static final String LOOK_AND_FEEL_SETTINGS_ID = "look_and_feel";
	//Properties guiProperties = new Properties();
	//private static Collection m_currentCollection;
	private static Properties m_generalSettings = new Properties();
	
	// constructor
	MainWindow(String windowName) {
		super(windowName);
	}
	

	
	void saveAndQuit() {
		ChangePropagator.getCurrentCollection().save();
		SaveGUISettings();
		System.exit(0);
	}
	
	private void SaveGUISettings() {
		try (FileOutputStream guiSettingsOStream = new
				FileOutputStream( NAME_OF_SETTINGS_FILE );){ 
			
			m_generalSettings.store(guiSettingsOStream,"Eb: GUI settings");
			guiSettingsOStream.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot write to the media - please install this program "+
					"somewhere else",
					"Error writing GUI-settings file",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static Deck getCurrentDeck() {
		return ChangePropagator.getCurrentDeck();
	}
	
	private void LoadGUISettings() {
		try {
			String lookAndFeel = 
					m_generalSettings.getProperty("look_and_feel",null);
			if (lookAndFeel != null) {
				UIManager.setLookAndFeel(lookAndFeel);
			}
		} 
		catch (UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(this, 
					"The provided GUI Look and feel is not available for your"+
					" system. Please choose a new Look and Feel in the" +
					" relevant menu. ",
				    "Preconfigured Look and Feel not available",
				    JOptionPane.WARNING_MESSAGE);
		} catch (ClassNotFoundException|
				InstantiationException|
				IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 			
	}
	
	// main
	// loads the main window
	// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, November 6th, 2015
	public static void main(String[] args) {
		
		try ( FileInputStream settingsFile = 
				new FileInputStream(NAME_OF_SETTINGS_FILE)) {
			m_generalSettings.load(settingsFile);
			settingsFile.close();
		} catch (Exception e) {
			Utilities.createEmptyFile(NAME_OF_SETTINGS_FILE);
		}
			
			
			String collectionName = m_generalSettings.getProperty("default_collection", "default");
			ChangePropagator.setCurrentCollection( Collection.loadCollection(collectionName));
			String mainWindowTitle;
			if (getCurrentDeck() == null) {
				mainWindowTitle = "No deck available";
			} else {
				mainWindowTitle = getCurrentDeck().getName();
			}
			MainWindow mainWindow = new MainWindow(mainWindowTitle);
			mainWindow.LoadGUISettings();
		
		
		// Now add the components!
		// Component 1 of 2: the menu bar
		JMenu fileMenu = new JMenu("File");
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setAccelerator(KeyStroke.getKeyStroke(new Character('Q'), 
				InputEvent.CTRL_DOWN_MASK ));
		quitItem.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent arg0) {
				mainWindow.saveAndQuit();
			}		
		});
		fileMenu.add(quitItem);
		
		JMenu deckManagementMenu = new JMenu("Manage Decks");
		deckManagementMenu.add(new JMenuItem("Next Deck"));
		deckManagementMenu.add(new JMenuItem("Previous Deck"));
		JMenuItem createDeckItem = new JMenuItem("Create Deck");
		createDeckItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.createDeck();	
			}
		});
		deckManagementMenu.add(createDeckItem);
		deckManagementMenu.add(new JMenuItem("Change Deck Name"));
		deckManagementMenu.add(new JMenuItem("Delete Deck"));
		
		JMenu cardManagementMenu = new JMenu("Manage Cards");
		cardManagementMenu.add(new JMenuItem("Undo Action"));
		cardManagementMenu.add(new JMenuItem("Redo Action"));
		JMenuItem createCardItem = new JMenuItem("Create Card");
		createCardItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ChangePropagator.getCurrentCollection().noDeckActive()) {
					ChangePropagator.getCurrentCollection().createDeck();
					// now, creating a deck can have been successful or 
					// unsuccessful. If unsuccessful, just stop here.
					if (ChangePropagator.getCurrentCollection().noDeckActive()) {
						return;
					}
				}
				NewCardFrame newCardFrame= new NewCardFrame(getCurrentDeck());
			}
		});
				
		cardManagementMenu.add(createCardItem);
		cardManagementMenu.add(new JMenuItem("Edit Card"));
		cardManagementMenu.add(new JMenuItem("Suspend Card"));
		cardManagementMenu.add(new JMenuItem("Delete Card"));
		
		JMenu lookAndFeelMenu = new JMenu("Look & Feel", true);
		ButtonGroup lookAndFeelButtonGroup = new ButtonGroup();
		final UIManager.LookAndFeelInfo[] lookAndFeels = 
				UIManager.getInstalledLookAndFeels();
		for (int lookAndFeelIndex = 0;  lookAndFeelIndex < lookAndFeels.length;
				lookAndFeelIndex++) {
			String savedLookAndFeel = 
					m_generalSettings.getProperty(LOOK_AND_FEEL_SETTINGS_ID);
			JRadioButtonMenuItem newLookAndFeelRadioButton = 
				new JRadioButtonMenuItem(
					lookAndFeels[ lookAndFeelIndex].getName(),
							 // default
					(savedLookAndFeel ==null && lookAndFeelIndex == 0) || 
							// or... in case that settings are available..
					lookAndFeels[lookAndFeelIndex].getClassName().equals(savedLookAndFeel)); // initialize first radio 
			                // button to true, rest to false.
			final String lookAndFeelsClassName = 
					lookAndFeels[lookAndFeelIndex].getClassName(); 
			newLookAndFeelRadioButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						UIManager.setLookAndFeel(lookAndFeelsClassName);
						m_generalSettings.setProperty(
							LOOK_AND_FEEL_SETTINGS_ID, lookAndFeelsClassName);
					} catch (Exception e) {
						System.out.println(e);
					}
					SwingUtilities.updateComponentTreeUI(mainWindow);
				}
			});
			lookAndFeelButtonGroup.add(newLookAndFeelRadioButton);
			lookAndFeelMenu.add(newLookAndFeelRadioButton);
		}
		
		JMenuBar mainMenuBar = new JMenuBar();
		mainMenuBar.add(fileMenu);
		mainMenuBar.add(deckManagementMenu);
		mainMenuBar.add(cardManagementMenu);
		mainMenuBar.add(lookAndFeelMenu);
		
		mainWindow.setJMenuBar( mainMenuBar );
		
		JTabbedPane tabContainer = new JTabbedPane();
		tabContainer.add(new StudyTab());
		tabContainer.add(new EditTab());
		BrowseTab browseTab = new BrowseTab();
		tabContainer.add(browseTab);
		ChangePropagator.registerDeckChangeListener(browseTab);
		

	    //this.windowAdapter = 

	    // when you press "X" the WINDOW_CLOSING event is called but that is it
	    // nothing else happens
	    //this.setDefaultCloseOperation(ClosableFrame.DO_NOTHING_ON_CLOSE);
	    // don't forget this
		mainWindow.addWindowListener(new WindowAdapter() {
	        // WINDOW_CLOSING event handler
	        @Override
	        public void windowClosing(WindowEvent e) {
	        	super.windowClosing(e);
	        	mainWindow.saveAndQuit();
	        	System.out.println("The weird close\n");
	            
	            
	            // You can still stop closing if you want to
	        }
	    });
		
		
		
		mainWindow.add(tabContainer);
		mainWindow.setSize(1000,700);
		mainWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
		
	}


}
