package eb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The window in which the user can set how he/she wants to study; like which
 * time to take before the initial repetition, or what scheme repetitions will
 * have (every day, or with increasing intervals, or whatever).
 * 
 * @author Eric-Wubbo Lameijer
 */
public class StudyOptionsWindow extends JFrame {

	// Automatically generated serialVersionUID.
	private static final long serialVersionUID = -907266672997684012L;

	// Button that closes this window, not saving any changes made.
	private JButton m_cancelButton;

	// Button that restores the defaults to those of Eb.
	private JButton m_loadEbDefaultsButton;

	// Button that reloads the current settings of the deck (undoing non-saved
	// changes).
	private JButton m_loadCurrentDeckSettingsButton;

	// Button that sets the study settings of the deck to the values currently
	// displayed in this window.
	private JButton m_setToTheseValuesButton;

	// Input element that allows users to view and set the interval between the
	// creation of the card and the first time it is put up for review.
	private TimeInputElement m_initialIntervalBox;

	/**
	 * Creates a new Study Options window.
	 */
	private StudyOptionsWindow() {
		// preconditions: none (default constructor...)
		super("Study Options");
		m_initialIntervalBox = TimeInputElement
		    .createInstance("Initial review after", Deck.getInitialInterval());
		m_cancelButton = new JButton("Discard changes and close");
		m_loadEbDefaultsButton = new JButton("Load Eb's default values");
		m_loadCurrentDeckSettingsButton = new JButton(
		    "Load settings of current deck");
		m_setToTheseValuesButton = new JButton(
		    "Set study parameters of this deck to these values");
	}

	/**
	 * Closes the frame, removing all its contents. NOTE: EVIL DUPLICATION. CAN I
	 * AVOID THAT?
	 */
	private void close() {
		// preconditions: none
		this.dispose();
		// postconditions: none
	}

	/**
	 * Loads Eb's default values.
	 */
	private void loadEbDefaults() {
		// preconditions: none
		StudyOptions defaultOptions = StudyOptions.getDefault();
		TimeInterval defaultInitialInterval = defaultOptions.getInitialInterval();
		m_initialIntervalBox.setInterval(defaultInitialInterval);
		// postconditions: none (should have worked)
	}

	/**
	 * Loads the study options settings of the current deck.
	 */
	private void loadCurrentDeckSettings() {
		// preconditions: none
		m_initialIntervalBox.setInterval(Deck.getInitialInterval());
		// postconditions: none
	}

	/**
	 * Saves the settings to the deck
	 */

	private void saveSettingsToDeck() {
		if (Deck.isValidInitialInterval(m_initialIntervalBox.g)) {

		}
	}

	/**
	 * Initializes the study options window, performing those actions which are
	 * only permissible (for a nullness checker) after the window has been
	 * created.
	 */
	void init() {

		setLayout(new BorderLayout());

		// first: make the buttons do something
		m_cancelButton.addActionListener(e -> close());
		m_loadCurrentDeckSettingsButton
		    .addActionListener(e -> loadCurrentDeckSettings());
		m_loadEbDefaultsButton.addActionListener(e -> loadEbDefaults());
		m_setToTheseValuesButton.addActionListener(e -> saveSettingsToDeck());

		// Then create two panels: one for setting the correct values for the study
		// options, and one to contain the reset/confirm/reload etc. buttons.
		JPanel settingsPane = new JPanel();
		JPanel buttonsPane = new JPanel();

		// Give the panes appropriate layouts
		settingsPane.setLayout(new BorderLayout());

		buttonsPane.setLayout(new GridLayout(2, 2));

		// now fill the panes
		Container settingsBox = Box.createVerticalBox();
		settingsBox.add(m_initialIntervalBox);
		settingsPane.add(settingsBox, BorderLayout.NORTH);

		buttonsPane.add(m_cancelButton);
		buttonsPane.add(m_loadEbDefaultsButton);
		buttonsPane.add(m_loadCurrentDeckSettingsButton);
		buttonsPane.add(m_setToTheseValuesButton);

		add(settingsPane, BorderLayout.NORTH);
		add(buttonsPane, BorderLayout.SOUTH);

		setSize(700, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Displays the study options window. In order to pacify the nullness checker,
	 * separates creation and display of the window.
	 */
	static void display() {
		StudyOptionsWindow studyOptionsWindow = new StudyOptionsWindow();
		studyOptionsWindow.init();
		// postconditions: none
	}
}
