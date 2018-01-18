package eb.mainwindow;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import eb.data.Deck;
import eb.data.DeckManager;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Listener;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.mainwindow.reviewing.ReviewManager;
import eb.mainwindow.reviewing.ReviewPanel;
import eb.subwindow.ArchivingSettingsWindow;
import eb.subwindow.CardEditingManager;
import eb.subwindow.StudyOptions;
import eb.subwindow.StudyOptionsWindow;
import eb.utilities.TimeInterval;
import eb.utilities.Utilities;

/**
 * The main window of Eb.
 *
 * @author Eric-Wubbo Lameijer
 */
public class MainWindow extends JFrame implements Listener {

	// Automatically generated ID for serialization (not used).
	private static final long serialVersionUID = 5327238918756780751L;

	// The name of the program.
	private static final String PROGRAM_NAME = "Eb";

	// The label that has to be shown if there is no card that needs to be
	// reviewed currently, or if there is an error. Is the alternative to
	// the regular "reviewing" window, which should be active most of the
	// time.
	private final JLabel m_messageLabel;
	
	// IDs of the various panels, necessary for adding them to the CardLayout of the main panel
	private static final String REVIEW_PANEL_ID = "REVIEWING_PANEL";
	private static final String INFORMATION_PANEL_ID = "INFORMATION_PANEL";
	private static final String SUMMARIZING_PANEL_ID = "SUMMARIZING_PANEL";
	private static final String TIMED_REVIEW_START_PANEL_ID = "TIMER_START_PANEL";

	// the name of the file that contains which deck has been consulted last
	private static final String EB_STATUS_FILE = "eb_status.txt";

	// the initial state of the main window
	private MainWindowState m_state = MainWindowState.REACTIVE;

	// button the user can press to start reviewing. Only visible if the user for some reason
	// decides to not review cards yet (usually by having one rounds of review, and then 
	// stopping the reviewing)
	private final JButton m_startReviewingButton = new JButton();

	// Contains the REVIEWING_PANEL and the INFORMATION_PANEL, using a CardLayout.
	private final JPanel m_modesContainer;

	// The reviewing panel
	private ReviewPanel m_reviewPanel;

	// To regularly update how long it is until the next reviewing session
	private Timer m_messageUpdater;

	/**
	 * MainWindow constructor. Sets title of the window, and creates some widgets.
	 */
	MainWindow() {
		// preconditions: none
		super(PROGRAM_NAME);
		m_messageLabel = new JLabel();
		m_modesContainer = new JPanel();
		m_modesContainer.setLayout(new CardLayout());
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
		return "<br>Ctrl+N to add a card.<br>" + "Ctrl+Q to quit.<br>"
		    + "Ctrl+K to create a deck.<br>" + "Ctrl+L to load a deck.<br>"
		    + "Ctrl+T to view/edit the study options.<br>"
		    + "Ctrl+R to view/edit the deck archiving options.<br>";
	}

	/**
	 * Returns a message about the size of the current deck.
	 *
	 * @return a message about the size of the current deck.
	 */
	private String getDeckSizeMessage() {
		// preconditions: none
		return "The current deck contains "
		    + DeckManager.getCurrentDeck().getCards().getSize() + " cards.";
		// postconditions: none
	}
	
	/**
	 * Returns text indicating how long it will be to the next review
	 * 
	 * @return text indicating the time to the next review
	 */
	String getTimeToNextReviewMessage() {
		StringBuilder message = new StringBuilder();
    Deck currentDeck = DeckManager.getCurrentDeck();	
		int numCards = currentDeck.getCards().getSize();
		if (numCards > 0) {
			message.append("Time till next review: ");
			Duration timeUntilNextReviewAsDuration = currentDeck
			    .getTimeUntilNextReview();
			String timeUntilNextReviewAsText = Utilities
			    .durationToString(timeUntilNextReviewAsDuration);
			message.append(timeUntilNextReviewAsText);
			message.append("<br>");
			m_startReviewingButton
			    .setVisible(timeUntilNextReviewAsDuration.isNegative());
		} else {
			// no cards
			m_startReviewingButton.setVisible(false);
		}
		return message.toString();
	}

	/**
	 * Updates the message label (the information inside the main window, like time to next review)
	 */
	void updateMessageLabel() {
		StringBuilder message = new StringBuilder();
		message.append("<html>");
		message.append(getDeckSizeMessage());
		message.append("<br>");
		message.append(getTimeToNextReviewMessage());		
		message.append(getUICommands());
		message.append("</html>");
		m_messageLabel.setText(message.toString());
	}
	
	/**
	 * Updates the title of the window, which contains information like the number of cards in the deck
	 */
	void updateWindowTitle() {
		Deck currentDeck = DeckManager.getCurrentDeck();
		int numReviewingPoints = currentDeck.getCards().getReviewingPoints();

		int numReviewableCards = currentDeck.getReviewableCardList().size();
		String title = "Eb: " + currentDeck.getName() + " ("
		    + Utilities.pluralText(numReviewableCards, "card")
		    + " to be reviewed in total";
		if (m_state == MainWindowState.REVIEWING) {
			title += ", " + Utilities
			    .pluralText(ReviewManager.getInstance().cardsToGoYet(), "card")
			    + " yet to be reviewed in the current session";
		}
		int numCards = currentDeck.getCards().getSize();
		title += ", " + Utilities.pluralText(numCards, "card") + " in deck, "
		    + Utilities.pluralText(numReviewingPoints, "point") + ")";

		this.setTitle(title);
	}
	
	/**
	 * Updates the text on the review button
	 */
	void updateReviewButtonText() {
		String reviewButtonText;
		StudyOptions currentStudyOptions = DeckManager.getCurrentDeck().getStudyOptions();
		if (currentStudyOptions.isTimed()) {
			TimeInterval timeInterval = currentStudyOptions.getTimerInterval();
			reviewButtonText = "Review now (timed, " + timeInterval.getScalar() + " "
			    + timeInterval.getUnit().getUserInterfaceName() + ")";
		} else {
			reviewButtonText = "Review now";
		}
		m_startReviewingButton.setText(reviewButtonText);
	}
	
	/**
	 * Gives the message label its correct (possibly updated) value.
	 */
	void updateOnScreenInformation() {
		updateMessageLabel();
		updateWindowTitle();
		updateReviewButtonText();
	}

	void showCorrectPanel() {
		switch (m_state) {
		case REACTIVE:
			showReactivePanel();
			break;
		case INFORMATIONAL:
			showInformationPanel();
			break;
		case REVIEWING:
			showReviewingPanel();
			break;
		case SUMMARIZING:
			showSummarizingPanel();
			break;
		case WAITING_FOR_TIMER_START:
			switchToPanel(TIMED_REVIEW_START_PANEL_ID);
			break;
		default:
			Utilities.require(false, "MainWindow.respondToProgramStateChange "
			    + "error: receiving wrong program state.");
			break;
		}

	}

	private boolean mustReviewNow() {
		// case 1: there are no cards in the deck - so nothing to review either
		if (DeckManager.getCurrentDeck().getCards().getSize() == 0) {
			return false;
		} else {
			Duration timeUntilNextReviewAsDuration = DeckManager.getCurrentDeck()
			    .getTimeUntilNextReview();
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
				} else if (Deck.getDeckFileHandle(deckName).exists()) {
					JOptionPane.showMessageDialog(null, "Sorry, the deck \"" + deckName
					    + "\" already exists. Please choose another name.");
				} else {
					// The deckname is valid!
					m_messageUpdater.stop();
					DeckManager.createDeckWithName(deckName);
					// reset window
					m_state = MainWindowState.REACTIVE;
					updateOnScreenInformation();
					m_messageUpdater.start();

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
		final JMenuItem restoreItem = new JMenuItem("Restore from archive");
		restoreItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		restoreItem.addActionListener(e -> restoreDeck());
		fileMenu.add(restoreItem);
		final JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quitItem.addActionListener(e -> saveAndQuit());
		fileMenu.add(quitItem);
		final JMenu deckManagementMenu = new JMenu("Manage Deck");
		final JMenuItem addCardItem = new JMenuItem("Add Card");
		addCardItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		addCardItem.addActionListener(e -> new CardEditingManager());
		deckManagementMenu.add(addCardItem);
		final JMenuItem studyOptionsItem = new JMenuItem("Study Options");
		studyOptionsItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		studyOptionsItem.addActionListener(e -> openStudyOptionsWindow());
		deckManagementMenu.add(studyOptionsItem);
		final JMenuItem archivingOptionsItem = new JMenuItem(
		    "Deck Archiving Options");
		archivingOptionsItem.setAccelerator(
		    KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		archivingOptionsItem.addActionListener(e -> openDeckArchivingWindow());
		deckManagementMenu.add(archivingOptionsItem);
		final JMenuBar mainMenuBar = new JMenuBar();
		mainMenuBar.add(fileMenu);
		mainMenuBar.add(deckManagementMenu);
		setJMenuBar(mainMenuBar);

		// add message label (or show cards-to-be-reviewed)

		JPanel informationPanel = createInformationPanel();
		m_modesContainer.add(informationPanel, INFORMATION_PANEL_ID);
		m_reviewPanel = new ReviewPanel();
		m_modesContainer.add(m_reviewPanel, REVIEW_PANEL_ID);
		ReviewManager.getInstance().setPanel(m_reviewPanel);
		JPanel summarizingPanel = new SummarizingPanel();
		m_modesContainer.add(summarizingPanel, SUMMARIZING_PANEL_ID);
		TimedReviewStartPanel timedReviewStartPanel = new TimedReviewStartPanel();
		m_modesContainer.add(timedReviewStartPanel, TIMED_REVIEW_START_PANEL_ID);
		add(m_modesContainer);

		setNameOfLastReviewedDeck();

		showCorrectPanel();

		// now show the window itself.
		setSize(1000, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Instead of using the WindowAdapter class, use the EventHandler utility
		// class to create a class with default noop methods, just overriding the
		// windowClosing.
		addWindowListener(EventHandler.create(WindowListener.class, this,
		    "saveAndQuit", null, "windowClosing"));
		setVisible(true);
		// BlackBoard.register(this, UpdateType.DECK_SWAPPED);
		// BlackBoard.register(this, UpdateType.DECK_CHANGED);
		BlackBoard.register(this, UpdateType.PROGRAMSTATE_CHANGED);
		m_messageUpdater = new Timer(100, e -> showCorrectPanel());
		m_messageUpdater.start();
		updateOnScreenInformation();
		// postconditions: none
	}

	private void restoreDeck() {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		} else {
			File selectedFile = chooser.getSelectedFile();
			DeckManager.createDeckFromArchive(selectedFile);
		}
	}

	/**
	 * Opens a/the Deck archiving options window
	 */
	private void openDeckArchivingWindow() {
		ArchivingSettingsWindow.display();
	}

	private void setNameOfLastReviewedDeck() {
		Path statusFilePath = Paths.get(EB_STATUS_FILE);
		final String mostRecentDeckIdentifier = "most_recently_reviewed_deck: ";
		List<String> lines;
		try {
			lines = Files.readAllLines(statusFilePath, Charset.forName("UTF-8"));
			Optional<String> fileLine = lines.stream()
			    .filter(e -> e.startsWith(mostRecentDeckIdentifier)).findFirst();
			if (fileLine.isPresent()) {
				String deckName = fileLine.get()
				    .substring(mostRecentDeckIdentifier.length());
				DeckManager.setNameOfLastReviewedDeck(deckName);
			}
		} catch (IOException e) {
			// If input fails, set name to ""
			DeckManager.setNameOfLastReviewedDeck("");
			Logger.getGlobal().info(e + "");
		}

	}

	private void loadDeck() {
		do {
			String deckName = JOptionPane.showInputDialog(null,
			    "Please give name for deck to be loaded");
			if (deckName == null) {
				// Cancel button pressed
				return;
			}
			if (canDeckBeLoaded(deckName)) {
				m_messageUpdater.stop();
				DeckManager.loadDeck(deckName);
				// reset window
				m_state = MainWindowState.REACTIVE;
				updateOnScreenInformation();
				m_messageUpdater.start();
				return;
			}
		} while (true);
	}

	private boolean canDeckBeLoaded(String deckName) {
		if (!Utilities.isStringValidIdentifier(deckName)) {
			JOptionPane.showMessageDialog(null, "Sorry, \"" + deckName
			    + "\" is not a valid name for a deck. Please choose another name.");
		} else if (!Deck.getDeckFileHandle(deckName).exists()) {
			JOptionPane.showMessageDialog(null,
			    "Sorry, the deck \"" + deckName + "\" does not exist yet.");
		} else {
			// we have a valid deck here
			if (DeckManager.canLoadDeck(deckName)) {
				// the only 'happy path' - otherwise false should be returned.
				return true;
			} else {
				JOptionPane.showMessageDialog(null,
				    "An error occurred while loading the deck \"" + deckName
				        + "\". It may be an invalid file; possibly try restore it from an archive file?");
			}
		}
		return false;
	}

	private JPanel createInformationPanel() {
		JPanel informationPanel = new JPanel();
		informationPanel.setLayout(new BorderLayout());
		informationPanel.add(m_messageLabel, BorderLayout.CENTER);
		m_startReviewingButton.setVisible(false);
		m_startReviewingButton.addActionListener(
		    e -> BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
		        MainWindowState.REACTIVE.name())));
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
		DeckManager.save();
		dispose();
		// preconditions: none
	}

	private void saveEbStatus() {
		List<String> lines = new ArrayList<>();
		lines.add("most_recently_reviewed_deck: "
		    + DeckManager.getCurrentDeck().getName());
		Path statusFilePath = Paths.get(EB_STATUS_FILE);
		try {
			Files.write(statusFilePath, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			Logger.getGlobal().info(e + "");
		}
	}

	private void switchToPanel(String panelId) {
		CardLayout cardLayout = (CardLayout) m_modesContainer.getLayout();
		cardLayout.show(m_modesContainer, panelId);
	}

	private void showInformationPanel() {
		switchToPanel(INFORMATION_PANEL_ID);
		updateOnScreenInformation();
	}

	/**
	 * Shows the reviewing panel
	 */
	private void showReviewingPanel() {
		if (m_state != MainWindowState.REVIEWING) {
			ReviewManager.getInstance().start(m_reviewPanel);
			m_state = MainWindowState.REVIEWING;
		}
		switchToPanel(REVIEW_PANEL_ID);
		updateOnScreenInformation();
	}

	/**
	 * Shows the 'reactive' panel, which means the informational panel if no
	 * reviews need to be conducted, and the reviewing panel when cards need to be
	 * reviewed.
	 */
	private void showReactivePanel() {
		updateOnScreenInformation();
		if (mustReviewNow()) {
			if (DeckManager.getCurrentDeck().getStudyOptions().isTimed()) {
				m_state = MainWindowState.WAITING_FOR_TIMER_START;
			} else {
				showReviewingPanel();
			}
		} else {
			showInformationPanel();
		}
	}

	public void respondToUpdate(Update update) {
		if (update.getType() == UpdateType.DECK_CHANGED) {
			showCorrectPanel();
		} else if (update.getType() == UpdateType.PROGRAMSTATE_CHANGED) {
			m_state = MainWindowState.valueOf(update.getContents());
			m_reviewPanel.refresh(); // there may be new cards to refresh
			updateOnScreenInformation();
			showCorrectPanel();
		} else if (update.getType() == UpdateType.DECK_SWAPPED) {
			MainWindowState newState = (mustReviewNow()) ? MainWindowState.REVIEWING
			    : MainWindowState.REACTIVE;
			BlackBoard
			    .post(new Update(UpdateType.PROGRAMSTATE_CHANGED, newState.name()));
		}
	}

	private void showSummarizingPanel() {
		switchToPanel(SUMMARIZING_PANEL_ID);
	}
}
