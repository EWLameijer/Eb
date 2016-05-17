package eb;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 * The main window of Eb.
 *
 * @author Eric-Wubbo Lameijer
 */
public class MainWindow extends JFrame
    implements DeckChangeListener, ProgramStateChangeListener {

	// Automatically generated ID for serialization (not used).
	private static final long serialVersionUID = 5327238918756780751L;

	// The name of the program.
	private static final String PROGRAM_NAME = "Eb";

	// The label that has to be shown if there is no card that needs to be
	// reviewed currently, or if there is an error. Is the alternative to
	// the regular "reviewing" window, which should be active most of the
	// time.
	private final JLabel m_messageLabel;
	private static final String REVIEW_PANEL_ID = "REVIEWING_PANEL";
	private static final String INFORMATION_PANEL_ID = "INFORMATION_PANEL";
	private static final String SUMMARIZING_PANEL_ID = "SUMMARIZING_PANEL";

	private static final String EB_STATUS_FILE = "eb_status.txt";

	private final JButton m_startReviewingButton = new JButton("Review now");

	// Contains the REVIEWING_PANEL and the INFORMATION_PANEL, using a CardLayout.
	private final JPanel m_modesContainer;

	private ReviewPanel m_reviewPanel;

	/**
	 * MainWindow constructor.
	 */
	MainWindow() {
		// preconditions: none
		super(PROGRAM_NAME);
		m_messageLabel = new JLabel();
		m_modesContainer = new JPanel();
		m_modesContainer.setLayout(new CardLayout());
	}

	/**
	 * Implements the DeckChangeListener interface to respond to deck change
	 * events
	 */
	@Override
	public void respondToChangedDeck() {
		// preconditions: none (the deck has changed, but basically, that would
		// be the reason why this function is called in the first place
		updateWindow();
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
		return "Ctrl+N to add a card. Ctrl+Q to quit. Ctrl+K to create a deck. "
		    + "Ctrl+L to load a deck. Ctrl+T to view/edit the study options.";
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
		StringBuilder message = new StringBuilder("");
		message.append("<html>");
		message.append(getDeckSizeMessage());
		message.append("<br>");
		if (Deck.getCardCount() > 0) {
			message.append("Time till next review: ");
			Duration timeUntilNextReviewAsDuration = Deck.getTimeTillNextReview();
			String timeUntilNextReviewAsText = Utilities
			    .durationToString(timeUntilNextReviewAsDuration);
			message.append(timeUntilNextReviewAsText);
			message.append("<br>");
			m_startReviewingButton
			    .setVisible(timeUntilNextReviewAsDuration.isNegative());
		}
		message.append(getUICommands());
		message.append("</html>");
		m_messageLabel.setText(message.toString());
		this.setTitle("Eb: " + Deck.getName());

		// postconditions: none (well, the label should have some text, but I'm
		// willing to trust that that happens.
	}

	void updateWindow() {
		ProgramState programState = ProgramController.getProgramState();
		if (programState == ProgramState.REACTIVE) {
			showReactivePanel();
		} else if (programState == ProgramState.INFORMATIONAL) {
			showInformationPanel();
		}
	}

	private boolean mustReviewNow() {
		// case 1: there are no cards in the deck - so nothing to review either
		if (Deck.getCardCount() == 0) {
			return false;
		} else {
			Duration timeUntilNextReviewAsDuration = Deck.getTimeTillNextReview();
			return timeUntilNextReviewAsDuration.isNegative();
		}
	}

	/**
	 * Opens the study options window, at which one can set the study options for
	 * a deck (after which interval the first card should be studied, etc.)
	 */
	private void openStudyOptionsWindow() {
		// preconditions: none (this method will simply be called when the user
		// presses the correct button).

		StudyOptionsWindow.display();

		// postconditions: none (the user does not have to do anything with the
		// settings)
	}

	private void createDeck() {
		do {
			String deckName = JOptionPane.showInputDialog(null,
			    "Please give name " + "for deck to be created");
			if (deckName == null) {
				// cancel button has been pressed
				return;
			} else {
				if (!Utilities.isStringValidIdentifier(deckName)) {
					JOptionPane.showMessageDialog(null, "Sorry, \"" + deckName
					    + "\" is not a valid name for a deck. Please choose another name.");
				} else if (Deck.exists(deckName)) {
					JOptionPane.showMessageDialog(null, "Sorry, the deck \"" + deckName
					    + "\" already exists. Please choose another name.");
				} else {
					// The deckname is valid!
					Deck.createDeckWithName(deckName);
					return;
				}
			}
		} while (true);
	}

	/**
	 * Performs the proper buildup of the window (after the construction has
	 * initialized all components properly).
	 */
	private void init() {
		// add menu
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem createItem = new JMenuItem("Create deck");
		createItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK));
		createItem.addActionListener(e -> createDeck());
		fileMenu.add(createItem);
		final JMenuItem loadItem = new JMenuItem("Load deck");
		loadItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		loadItem.addActionListener(e -> loadDeck());
		fileMenu.add(loadItem);
		final JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quitItem.addActionListener(e -> saveAndQuit());
		fileMenu.add(quitItem);
		final JMenu deckManagementMenu = new JMenu("Manage Deck");
		final JMenuItem addCardItem = new JMenuItem("Add Card");
		addCardItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		addCardItem.addActionListener(e -> {
			CardEditingManager editingManager = new CardEditingManager();
			editingManager.activateCardCreationWindow();
		});
		deckManagementMenu.add(addCardItem);
		final JMenuItem studyOptionsItem = new JMenuItem("Study Options");
		studyOptionsItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		studyOptionsItem.addActionListener(e -> openStudyOptionsWindow());
		deckManagementMenu.add(studyOptionsItem);
		final JMenuBar mainMenuBar = new JMenuBar();
		mainMenuBar.add(fileMenu);
		mainMenuBar.add(deckManagementMenu);
		setJMenuBar(mainMenuBar);

		// add message label (or show cards-to-be-reviewed)

		JPanel informationPanel = createInformationPanel();
		m_modesContainer.add(informationPanel, INFORMATION_PANEL_ID);
		m_reviewPanel = new ReviewPanel();
		m_modesContainer.add(m_reviewPanel, REVIEW_PANEL_ID);
		JPanel summarizingPanel = new SummarizingPanel();
		m_modesContainer.add(summarizingPanel, SUMMARIZING_PANEL_ID);
		add(m_modesContainer);

		setNameOfLastReviewedDeck();

		updateWindow();

		// now show the window itself.
		setSize(1000, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Instead of using the WindowAdapter class, use the EventHandler utility
		// class to create a class with default noop methods, just overriding the
		// windowClosing.
		addWindowListener(EventHandler.create(WindowListener.class, this,
		    "saveAndQuit", null, "windowClosing"));
		setVisible(true);
		Deck.addDeckChangeListener(this);
		ProgramController.addProgramStateChangeListener(this);
		Timer messageUpdater = new Timer(100, e -> updateWindow());
		messageUpdater.start();
		updateMessageLabel();
		// postconditions: none
	}

	private void setNameOfLastReviewedDeck() {
		Path statusFilePath = Paths.get(EB_STATUS_FILE);
		List<String> lines;
		try {
			lines = Files.readAllLines(statusFilePath, Charset.forName("UTF-8"));
			Optional<String> fileLine = lines.stream()
			    .filter(e -> e.startsWith("most_recently_reviewed_deck: "))
			    .findFirst();
			if (fileLine.isPresent()) {
				String deckName = fileLine.get()
				    .substring("most_recently_reviewed_deck: ".length());
				Deck.setNameOfLastReviewedDeck(deckName);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Deck.setNameOfLastReviewedDeck("");
		}

	}

	private void loadDeck() {
		do {
			String deckName = JOptionPane.showInputDialog(null,
			    "Please give name for deck to be loaded");
			if (deckName == null) {
				// cancel button has been pressed
				return;
			} else {
				if (!Utilities.isStringValidIdentifier(deckName)) {
					JOptionPane.showMessageDialog(null, "Sorry, \"" + deckName
					    + "\" is not a valid name for a deck. Please choose another name.");
				} else if (!Deck.exists(deckName)) {
					JOptionPane.showMessageDialog(null,
					    "Sorry, the deck \"" + deckName + "\" does not exist yet.");
				} else {
					// we have a valid deck here
					Deck.loadDeck(deckName);
					return;
				}
			}
		} while (true);
	}

	private JPanel createInformationPanel() {
		JPanel informationPanel = new JPanel();
		informationPanel.setLayout(new BorderLayout());
		informationPanel.add(m_messageLabel, BorderLayout.CENTER);
		m_startReviewingButton.setVisible(false);
		m_startReviewingButton.addActionListener(
		    e -> ProgramController.setProgramState(ProgramState.REVIEWING));
		informationPanel.add(m_startReviewingButton, BorderLayout.SOUTH);
		return informationPanel;
	}

	/**
	 * Displays the main window. Necessary since the Checker framework dislikes
	 * initializing values and doing things like 'add' in the same method.
	 */
	public static void display() {
		final MainWindow mainWindow = new MainWindow();
		mainWindow.init();
	}

	/**
	 * Saves the current deck and its status, and quits Eb.
	 */
	public void saveAndQuit() {
		// preconditions: (well, Eb is necessarily running)
		saveEbStatus();
		Deck.save();
		dispose();
		// preconditions: none
	}

	private void saveEbStatus() {
		List<String> lines = new ArrayList<>();
		lines.add("most_recently_reviewed_deck: " + Deck.getName());
		Path statusFilePath = Paths.get(EB_STATUS_FILE);
		try {
			Files.write(statusFilePath, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void switchToPanel(String panelId) {
		CardLayout cardLayout = (CardLayout) m_modesContainer.getLayout();
		cardLayout.show(m_modesContainer, panelId);
	}

	private void showInformationPanel() {
		switchToPanel(INFORMATION_PANEL_ID);
		updateMessageLabel();
	}

	private void showReviewingPanel() {
		Reviewer.activate();
		switchToPanel(REVIEW_PANEL_ID);
	}

	/**
	 * Shows the 'reactive' panel, which means the informational panel if no
	 * reviews need to be conducted, and the reviewing panel when cards need to be
	 * reviewed.
	 */
	private void showReactivePanel() {
		if (mustReviewNow()) {
			ProgramController.setProgramState(ProgramState.REVIEWING);
			showReviewingPanel();
		} else {
			showInformationPanel();
		}
	}

	@Override
	public void respondToProgramStateChange() {
		ProgramState newProgramState = ProgramController.getProgramState();
		m_reviewPanel.refresh(); // there may be new cards to refresh
		updateWindow();
		updateMessageLabel();
		switch (newProgramState) {
		case INFORMATIONAL:
			showInformationPanel();
			break;
		case REACTIVE:
			showReactivePanel();
			break;
		case REVIEWING:
			showReviewingPanel();
			break;
		case SUMMARIZING:
			showSummarizingPanel();
			break;
		default:
			Utilities.require(false, "MainWindow.respondToProgramStateChange "
			    + "error: receiving wrong program state.");
		}
	}

	private void showSummarizingPanel() {
		switchToPanel(SUMMARIZING_PANEL_ID);
	}
}
