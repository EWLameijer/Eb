package eb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * The window in which the user can set how he/she wants to study; like which
 * time to take before the initial repetition, or what scheme repetitions will
 * have (every day, or with increasing intervals, or whatever).
 *
 * @author Eric-Wubbo Lameijer
 */
/**
 * @author Eric-Wubbo Lameijer
 *
 */
public class StudyOptionsWindow extends JFrame
    implements DataFieldChangeListener, TextFieldChangeListener {

	// Automatically generated serialVersionUID.
	private static final long serialVersionUID = -907266672997684012L;

	// Button that closes this window, not saving any changes made.
	private final JButton m_cancelButton;

	// Button that restores the defaults to those of Eb.
	private final JButton m_loadEbDefaultsButton;

	// Button that reloads the current settings of the deck (undoing non-saved
	// changes).
	private final JButton m_loadCurrentDeckSettingsButton;

	// Button that sets the study settings of the deck to the values currently
	// displayed in this window.
	private final JButton m_setToTheseValuesButton;

	// Input element that allows users to view and set the interval between the
	// creation of the card and the first time it is put up for review.
	private final TimeInputElement m_initialIntervalBox;

	private final LabelledTextField m_sizeOfReview;

	/**
	 * Updates the title of the frame in response to changes to indicate to the
	 * user whether there are unsaved changes.
	 */
	private void updateTitle() {
		// preconditions: none. Is by definition only called when the object
		// has been constructed already.
		StudyOptions guiStudyOptions = gatherUIDataIntoStudyOptionsObject();
		String title = "Study Options";
		StudyOptions deckStudyOptions = Deck.getStudyOptions();
		if (guiStudyOptions.equals(deckStudyOptions)) {
			// if (guiStudyOptions.equals(Deck.getStudyOptions())) {
			title += " - no unsaved changes";
		} else {
			title += " - UNSAVED CHANGES";
		}
		setTitle(title);
		// postconditions: none. Simply changes the frame's title.
	}

	/**
	 * Creates a new Study Options window.
	 */
	private StudyOptionsWindow() {
		// preconditions: none (default constructor...)
		super();
		StudyOptions studyOptions = Deck.getStudyOptions();
		m_initialIntervalBox = TimeInputElement.createInstance(
		    "Initial review after", studyOptions.getInitialInterval());
		m_sizeOfReview = new LabelledTextField(
		    "number of cards per " + "reviewing session",
		    String.valueOf(studyOptions.getReviewSessionSize()));
		m_cancelButton = new JButton("Discard unsaved changes and close");
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

	private void loadSettings(StudyOptions settings) {
		m_initialIntervalBox.setInterval(settings.getInitialInterval());
		m_sizeOfReview.setContents(settings.getReviewSessionSize());
	}

	/**
	 * Loads Eb's default values.
	 */
	private void loadEbDefaults() {
		// preconditions: none
		loadSettings(StudyOptions.getDefault());
		// postconditions: none (should have worked)
	}

	/**
	 * Loads the study options settings of the current deck.
	 */
	private void loadCurrentDeckSettings() {
		// preconditions: none
		loadSettings(Deck.getStudyOptions());
		// postconditions: none
	}

	/**
	 * Collects the data from the GUI, and packages it nicely into a StudyOptions
	 * object.
	 * 
	 * @return a StudyOptions object reflecting the settings created in this
	 *         window's GUI.
	 */
	private StudyOptions gatherUIDataIntoStudyOptionsObject() {
		return new StudyOptions(m_initialIntervalBox.getInterval(),
		    Utilities.stringToInt(m_sizeOfReview.getContents()));
	}

	/**
	 * Saves the settings to the deck.
	 */
	private void saveSettingsToDeck() {
		StudyOptions guiStudyOptions = gatherUIDataIntoStudyOptionsObject();
		Deck.setStudyOptions(guiStudyOptions);
		updateTitle(); // Should be set to 'no unsaved changes' again.
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
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
		getRootPane().getActionMap().put("Cancel", new AbstractAction() { //$NON-NLS-1$
			private static final long serialVersionUID = 5281385300708334272L;

			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		m_loadCurrentDeckSettingsButton
		    .addActionListener(e -> loadCurrentDeckSettings());
		m_loadEbDefaultsButton.addActionListener(e -> loadEbDefaults());
		m_setToTheseValuesButton.addActionListener(e -> saveSettingsToDeck());
		m_initialIntervalBox.addDataFieldChangeListener(this);
		m_sizeOfReview.addListener(this, TextFieldChangeListener.ID);

		// Then create two panels: one for setting the correct values for the study
		// options, and one to contain the reset/confirm/reload etc. buttons.
		final JPanel settingsPane = new JPanel();
		final JPanel buttonsPane = new JPanel();

		// Give the panes appropriate layouts
		settingsPane.setLayout(new BorderLayout());

		buttonsPane.setLayout(new GridLayout(2, 2));

		// now fill the panes
		final Container settingsBox = Box.createVerticalBox();
		settingsBox.add(m_initialIntervalBox);
		settingsBox.add(m_sizeOfReview);
		settingsPane.add(settingsBox, BorderLayout.NORTH);

		buttonsPane.add(m_cancelButton);
		buttonsPane.add(m_loadEbDefaultsButton);
		buttonsPane.add(m_loadCurrentDeckSettingsButton);
		buttonsPane.add(m_setToTheseValuesButton);

		add(settingsPane, BorderLayout.NORTH);
		add(buttonsPane, BorderLayout.SOUTH);

		setSize(700, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		updateTitle();
		setVisible(true);
	}

	/**
	 * Displays the study options window. In order to pacify the nullness checker,
	 * separates creation and display of the window.
	 */
	static void display() {
		final StudyOptionsWindow studyOptionsWindow = new StudyOptionsWindow();
		studyOptionsWindow.init();
		// postconditions: none
	}

	public void respondToChangedDataField() {
		updateTitle();
	}

	@Override
	public void respondToEventType(String eventId) {
		if (eventId.equals(TextFieldChangeListener.ID)) {
			updateTitle();
		}

	}
}
