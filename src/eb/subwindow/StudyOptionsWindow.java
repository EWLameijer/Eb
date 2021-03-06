package eb.subwindow;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import eb.data.DeckManager;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Listener;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.mainwindow.MainWindowState;
import eb.utilities.ProgrammableAction;
import eb.utilities.Utilities;
import eb.utilities.ui_elements.LabelledComboBox;
import eb.utilities.ui_elements.LabelledTextField;
import eb.utilities.ui_elements.TimeInputElement;

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
public class StudyOptionsWindow extends JFrame implements Listener {

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

	private final TimeInputElement m_timeToWaitAfterCorrectReview;

	private final LabelledTextField m_lengtheningFactor;

	private final TimeInputElement m_timeToWaitAfterIncorrectReview;

	private final LabelledComboBox m_timedModus;

	private final TimeInputElement m_timeForTimer;

	/**
	 * Creates a new Study Options window.
	 */
	private StudyOptionsWindow() {
		// preconditions: none (default constructor...)
		super();
		StudyOptions studyOptions = DeckManager.getCurrentDeck().getStudyOptions();
		m_initialIntervalBox = TimeInputElement.createInstance(
		    "Initial review after", studyOptions.getInitialInterval());
		m_sizeOfReview = new LabelledTextField(
		    "number of cards per " + "reviewing session",
		    String.valueOf(studyOptions.getReviewSessionSize()), 3, 0);
		m_timeToWaitAfterCorrectReview = TimeInputElement.createInstance(
		    "Time to wait for re-reviewing remembered card:",
		    studyOptions.getRememberedCardInterval());
		m_lengtheningFactor = new LabelledTextField(
		    "after each successful review, increase review time by a factor",
		    String.valueOf(studyOptions.getLengtheningFactor()), 5, 2);
		m_timeToWaitAfterIncorrectReview = TimeInputElement.createInstance(
		    "Time to wait for re-reviewing forgotten card:",
		    studyOptions.getForgottenCardInterval());
		String[] normalTimedOptions = new String[] { TimedModus.FALSE.getName(),
		    TimedModus.TRUE.getName() };
		m_timedModus = new LabelledComboBox("normal or timed", normalTimedOptions);
		m_timeForTimer = TimeInputElement.createInstance(
		    "Maximum time for answering a card:", studyOptions.getTimerInterval());

		m_cancelButton = new JButton("Discard unsaved changes and close");
		m_loadEbDefaultsButton = new JButton("Load Eb's default values");
		m_loadCurrentDeckSettingsButton = new JButton(
		    "Load settings of current deck");
		m_setToTheseValuesButton = new JButton(
		    "Set study parameters of this deck to these values");

	}

	/**
	 * Updates the title of the frame in response to changes to indicate to the
	 * user whether there are unsaved changes.
	 */
	private void updateFrame() {
		// preconditions: none. Is by definition only called when the object
		// has been constructed already.
		StudyOptions guiStudyOptions = gatherUIDataIntoStudyOptionsObject();
		String title = "Study Options";
		StudyOptions deckStudyOptions = DeckManager.getCurrentDeck()
		    .getStudyOptions();
		if (guiStudyOptions.equals(deckStudyOptions)) {
			title += " - no unsaved changes";
		} else {
			title += " - UNSAVED CHANGES";
		}
		setTitle(title);
		m_timeForTimer.setVisible(guiStudyOptions.isTimed());
		// postconditions: none. Simply changes the frame's title.
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
		m_timeToWaitAfterCorrectReview
		    .setInterval(settings.getRememberedCardInterval());
		m_lengtheningFactor.setContents(settings.getLengtheningFactor());
		m_timeToWaitAfterIncorrectReview
		    .setInterval(settings.getForgottenCardInterval());
		m_timedModus.setTo(settings.getTimedModus().getName());
		m_timeForTimer.setVisible(settings.isTimed());
		m_timeForTimer.setInterval(settings.getTimerInterval());
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
		loadSettings(DeckManager.getCurrentDeck().getStudyOptions());
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
		    Utilities.stringToInt(m_sizeOfReview.getContents()),
		    m_timeToWaitAfterCorrectReview.getInterval(),
		    m_timeToWaitAfterIncorrectReview.getInterval(),
		    Utilities.stringToDouble(m_lengtheningFactor.getContents()),
		    TimedModus.stringToTimedModus(m_timedModus.getValue()),
		    m_timeForTimer.getInterval());
	}

	/**
	 * Saves the settings to the deck.
	 */
	private void saveSettingsToDeck() {
		StudyOptions guiStudyOptions = gatherUIDataIntoStudyOptionsObject();
		DeckManager.setStudyOptions(guiStudyOptions);
		BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
		    MainWindowState.REACTIVE.name()));
		updateFrame(); // Should be set to 'no unsaved changes' again.
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
		getRootPane().getActionMap().put("Cancel",
		    new ProgrammableAction(() -> close()));
		m_loadCurrentDeckSettingsButton
		    .addActionListener(e -> loadCurrentDeckSettings());
		m_loadEbDefaultsButton.addActionListener(e -> loadEbDefaults());
		m_setToTheseValuesButton.addActionListener(e -> saveSettingsToDeck());
		BlackBoard.register(this, UpdateType.INPUTFIELD_CHANGED);

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
		settingsBox.add(m_timeToWaitAfterCorrectReview);
		settingsBox.add(m_lengtheningFactor);
		settingsBox.add(m_timeToWaitAfterIncorrectReview);
		StudyOptions currentStudyOptions = DeckManager.getCurrentDeck()
		    .getStudyOptions();
		TimedModus timedModus = currentStudyOptions.getTimedModus();
		m_timedModus.setTo(timedModus.getName());
		settingsBox.add(m_timedModus);
		settingsBox.add(m_timeForTimer);
		m_timeForTimer.setVisible(currentStudyOptions.isTimed());
		settingsPane.add(settingsBox, BorderLayout.NORTH);

		buttonsPane.add(m_cancelButton);
		buttonsPane.add(m_loadEbDefaultsButton);
		buttonsPane.add(m_loadCurrentDeckSettingsButton);
		buttonsPane.add(m_setToTheseValuesButton);

		add(settingsPane, BorderLayout.NORTH);
		add(buttonsPane, BorderLayout.SOUTH);

		setSize(700, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		updateFrame();
		setVisible(true);
	}

	/**
	 * Displays the study options window. In order to pacify the nullness checker,
	 * separates creation and display of the window.
	 */
	public static void display() {
		final StudyOptionsWindow studyOptionsWindow = new StudyOptionsWindow();
		studyOptionsWindow.init();
		// postconditions: none
	}

	@Override
	public void respondToUpdate(Update update) {
		if (update.getType() == UpdateType.INPUTFIELD_CHANGED) {
			updateFrame();
		}
	}
}
