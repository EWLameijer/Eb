package learning_software;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

class Utilities {
	public static boolean createEmptyFile(String fileName){ 
		try {
			// try create a settings file, even one of 0 bytes!
			FileOutputStream settingsFileOStream = 
					new FileOutputStream(fileName);
			settingsFileOStream.close();
			return true;
		} catch ( IOException e ) {
			// should warn the user here. Dialog box?
			JOptionPane.showMessageDialog(null, 
					"Cannot write to the media - please install this program "+
					"somewhere else",
					"Error creating settings file",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	public static boolean noStringDataAvailable( String text ) {
		return (text == null || text.equals(""));
	}
	/**
	public static String safeGetString( Object... objects ) {
		int numberOfArguments = objects.length;
		if ((numberOfArguments < 3) || objects[0]==null || objects[1]==null ||
				objects[2]==null) {
			return ""; 
		} else {
			Class.forName(objects[0]).getMethod(objects[1])
			
		}
		
	}**/
}
