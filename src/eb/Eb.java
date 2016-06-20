package eb;

import eb.mainwindow.MainWindow;

/**
 * Runs Eb.
 *
 * @author Eric-Wubbo Lameijer
 */
public class Eb {

	public static String VERSION_STRING = "1.3";

	/**
	 * Hide the implicit public constructor.
	 */
	private Eb() {
	}

	/**
	 * Runs Eb.
	 *
	 * @param args
	 *          not used
	 */
	public static void main(String[] args) {
		MainWindow.display();
	}
}
