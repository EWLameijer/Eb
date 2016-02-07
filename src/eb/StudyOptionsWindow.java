package eb;

import javax.swing.JFrame;

/**
 * [CC] The window in which the user can set how he/she wants to study; like
 * which time to take before the initial repetition, or what scheme repetitions
 * will take (every day, or with increasing intervals, or whatever).
 * 
 * @author Eric-Wubbo Lameijer
 */
public class StudyOptionsWindow extends JFrame {

	// [CCCC] Automatically generated serialVersionUID.
	private static final long serialVersionUID = -907266672997684012L;

	/**
	 * [CPPRCCC] Creates a new Study Options window.
	 */
	public StudyOptionsWindow() {
		// preconditions: none (default constructor...)
		super("Study Options");

		setSize(400, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		// postconditions: none

	}
}
