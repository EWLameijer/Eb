package learning_software;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;

// DeckCollectionSettings
//
// Handles the global settings, of the entire collection of decks
//
// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, 
// November 6th, 2015
public class GlobalSettings {
	private final static String nameOfGlobalSettingsFile =  "global_settings.txt";
	private static boolean deckHasBeenLoaded = false;
	
	private static String m_lookAndFeelClassName = null;
	
	
	// createSettingsFile
	// Create the global settings file if it does not exist yet
	// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, 
	// November 6th, 2015
	private static void createSettingsFile()
	{
		Utilities.createEmptyFile( nameOfGlobalSettingsFile );

	}
	
	public static void setLookAndFeel(String lookAndFeelClassName) {
		m_lookAndFeelClassName = lookAndFeelClassName;
	}
	
	public static void saveGlobalSettings() {
		try {
			// try create a settings file, even one of 0 bytes!
			PrintWriter settingsFileWriter = 
					new PrintWriter(nameOfGlobalSettingsFile);
			settingsFileWriter.println("[GLO-LNF]" + m_lookAndFeelClassName);
			//settingsFileWriter
			//Collection.saveDeckNames(settingsFileWriter);
			
			settingsFileWriter.close();
		} catch ( IOException e ) {
			// should warn the user here. Dialog box?
			JOptionPane.showMessageDialog(null, 
					"Cannot write to the media - please install this program "+
					"somewhere else",
					"Error creating settings file",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// loadSettingsFileIfNecessary
	// Loads the global settings file if it has not been loaded yet
	// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, 
	// November 6th, 2015
	/*private static void loadSettingsFileIfNecessary()
	{
		if (!deckHasBeenLoaded) {
			m_settingsParser = new SettingsParser();
			m_settingsParser.initializeStatic(GlobalSettings.class);
			//m_settingsParser.initializeStatic(String.class);
			m_deckNames=new ArrayList<String>();
			try (FileInputStream settingsFileIStream = new
					FileInputStream( nameOfGlobalSettingsFile );){ 
				
				InputStreamReader settingsFileReader = new
						InputStreamReader(settingsFileIStream);
				BufferedReader settingsFileBReader = new
					BufferedReader( settingsFileReader );
				
			} catch (FileNotFoundException e){
				// settings-file does not exist yet, try to create it
				createSettingsFile();
			}
			catch (SecurityException|IOException e) {
				handleSecurityException();
			}
			// even if there were problems, we at least have an empty
			// deck now. No need for reload.
			deckHasBeenLoaded = true;
		}
	}*/
	
	// handleSecurityException
	// Reports on problems on loading/writing a file due to security managers
	// or such
	// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, 
	// November 6th, 2015
	private static void handleSecurityException() {
		JOptionPane.showMessageDialog(null, 
				"Cannot read from the media - please install this program "+
				"somewhere else",
				"Error reading settings file",
				JOptionPane.ERROR_MESSAGE);
		
	}
	
	// getCurrentDeckName
	// returns the name of the last deck the user has used, or 
	// "No deck available" if no decks exist
	// Created by Eric-Wubbo Lameijer, e.m.w.lameijer@gmail.com, 
	// November 6th, 2015
	/*static String getCurrentDeckName()
	{
		loadSettingsFileIfNecessary();
		if (m_deckNames.isEmpty()) {
			return "No deck available";
		} else {
			return m_deckNames.get(0);
		}
	}*/
}
