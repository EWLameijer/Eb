package eb;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The main window of Eb.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class MainWindow extends JFrame implements DeckChangeListener {

	// Automatically generated ID for serialization (not used).
	private static final long serialVersionUID = 5327238918756780751L;

	// The name of the program.
	private static final String PROGRAM_NAME = "Eb";

	// The label that is to be shown if there is no card that needs to be
	// reviewed currently, or if there is an error. Is the alternative to
	// the regular "reviewing" window, which should be active most of the
	// time.
	private JLabel m_messageLabel = null;

	/**
	 * Implements the DeckChangeListener interface to respond to deck change
	 * events
	 */
	public void respondToChangedDeck() {
		// preconditions: none (the deck has changed, but basically, that would
		// be the reason why this function is called in the first place
		updateMessageLabel();
		// postconditions: none (well, I suppose the message label may have
		// changed,
		// but possibly in future card edits will also call this function which
		// won't update the label. Leaving out postconditions for now.
	}

	/**
	 * Returns the commands of the user interface as a string, which can be used
	 * for example to instruct the user on Eb's use.
	 * 
	 * @return the commands of the user interface
	 */
	private String getUICommands() {
		// preconditions: none
		// postconditions: none
		return "Ctrl+N to add a card. Ctrl+Q to quit. "
		    + "Ctrl+T to view/edit the study options.";
	}

	/**
	 * Returns a message about the size of the current deck.
	 * 
	 * @return a message about the size of the current deck.
	 */
	private String getDeckSizeMessage() {
		// preconditions: none
		return "The current deck contains " + Deck.getCardCount() + " cards.";
		// postconditions: none
	}

	/**
	 * Gives the message label its correct (possibly updated) value.
	 */
	void updateMessageLabel() {
		// preconditions: none
		m_messageLabel.setText(getDeckSizeMessage() + " " + getUICommands());
		// postconditions: none (well, the label should have some text, but I'm
		// willing to trust that that happens.
	}

	/**
	 * Opens the study options window, at which one can set the study options for
	 * a deck (after which interval the first card should be studied, etc.)
	 */
	private void openStudyOptionsWindow() {
		// preconditions: none (this method will simply be called when the user
		// presses the correct button.

		new StudyOptionsWindow();

		// postconditions: none (the user does not have to do anything with the
		// settings)
	}

	/**
	 * MainWindow constructor.
	 */
	MainWindow() {
		// preconditions: none
		super(PROGRAM_NAME);

		// add menu
		JMenu fileMenu = new JMenu("File");
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quitItem.addActionListener(e -> saveAndQuit());
		fileMenu.add(quitItem);
		JMenu deckManagementMenu = new JMenu("Manage Deck");
		JMenuItem addCardItem = new JMenuItem("Add Card");
		addCardItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		addCardItem.addActionListener(e -> openNewCardWindow());
		deckManagementMenu.add(addCardItem);
		JMenuItem studyOptionsItem = new JMenuItem("Study Options");
		studyOptionsItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		studyOptionsItem.addActionListener(e -> openStudyOptionsWindow());
		deckManagementMenu.add(studyOptionsItem);
		JMenuBar mainMenuBar = new JMenuBar();
		mainMenuBar.add(fileMenu);
		mainMenuBar.add(deckManagementMenu);
		setJMenuBar(mainMenuBar);

		m_messageLabel = new JLabel();
		updateMessageLabel();

		add(m_messageLabel);
		setSize(1000, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		Deck.addDeckChangeListener(this);
		// postconditions: none
	}

	/**
	 * Saves the current deck and its status, and quits Eb.
	 */
	private void saveAndQuit() {
		// preconditions: (well, Eb is necessarily running)
		Deck.save();
		System.exit(0);
		// preconditions: none
	}

	/**
	 * Opens a window in which the user can create a new card.
	 */
	private void openNewCardWindow() {
		// preconditions: none (the button has been pressed, but okay, I assume
		// this function is only called when that has happened, and otherwise
		// it is not terrible either
		new NewCardWindow();
		// postconditions: none
	}

	/**
	 * Shows the main window of Eb.
	 * 
	 * @param args
	 *          the command-line arguments given to Eb (not used yet).
	 */
	public static void main(String[] args) {
		// preconditions: none (args are ignored for the moment)
		new MainWindow();
		// postconditions: none
	}

}
