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
 * [CCCC] The main window of Eb
 * 
 * @author Eric-Wubbo Lameijer
 */
public class MainWindow extends JFrame implements DeckChangeListener {

  // Automatically generated ID for serialization (not used). [CCCC]
  private static final long serialVersionUID = 5327238918756780751L;

  // The name of the program. [CCCC]
  private static final String PROGRAM_NAME = "Eb";

  // The label that is to be shown if there is no card that needs to be
  // reviewed currently, or if there is an error. Is the alternative to
  // the regular "reviewing" window, which should be active most of the
  // time. [CCCC]
  private JLabel m_messageLabel = null;

  /**
   * [CPPRCCC] Implements the DeckChangeListener interface to respond to deck
   * change events
   */
  public void respondToChangedDeck() {
    // preconditions: none (the deck has changed, but basically, that would
    // be the reason why this function is called in the first place
    updateMessageLabel();
    // postconditions: none (well, I suppose the message label may have changed,
    // but possibly in future card edits will also call this function which
    // won't update the label. Leaving out postconditions for now.
  }

  /**
   * [CPPRCCC] Returns the commands of the user interface as a string, which can
   * be used for example to instruct the user on Eb's use.
   * 
   * @return the commands of the user interface
   */
  private String getUICommands() {
    // preconditions: none
    // postconditions: none
    return "Ctrl+N to add a card. Ctrl+Q to quit.";
  }

  /**
   * [CPPRCCC] Returns a message about the size of the current deck.
   * 
   * @return a message about the size of the current deck.
   */
  private String getDeckSizeMessage() {
    // preconditions: none
    return "The current deck contains " + Deck.getCardCount() + " cards.";
    // postconditions: none
  }

  /**
   * [CPPRCCC] Gives the message label its correct (possibly updated) value
   */
  void updateMessageLabel() {
    // preconditions: none
    m_messageLabel.setText(getDeckSizeMessage() + " " + getUICommands());
    // postconditions: none (well, the label should have some text, but I'm
    // willing to trust that that happens.
  }

  /**
   * [CPPRCCC] MainWindow constructor
   */
  MainWindow() {
    // preconditions: none
    super(PROGRAM_NAME);

    // add menu
    JMenu fileMenu = new JMenu("File");
    JMenuItem quitItem = new JMenuItem("Quit");
    quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    quitItem.addActionListener(e -> saveAndQuit());
    fileMenu.add(quitItem);
    JMenu cardManagementMenu = new JMenu("Manage Cards");
    JMenuItem addCardItem = new JMenuItem("Add Card");
    addCardItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    addCardItem.addActionListener(e -> openNewCardWindow());
    cardManagementMenu.add(addCardItem);
    JMenuBar mainMenuBar = new JMenuBar();
    mainMenuBar.add(fileMenu);
    mainMenuBar.add(cardManagementMenu);
    setJMenuBar(mainMenuBar);

    m_messageLabel = new JLabel();
    updateMessageLabel();

    add(m_messageLabel);
    setSize(1000, 700);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
    // postconditions: none
  }

  /**
   * [CPPRCCC] Saves the current deck and its status, and quits Eb.
   */
  private void saveAndQuit() {
    // preconditions: (well, Eb is necessarily running)
    Deck.save();
    System.exit(0);
    // preconditions: none
  }

  /**
   * [CPPRCCC] Opens a window in which the user can create a new card.
   */
  private void openNewCardWindow() {
    // preconditions: none (the button has been pressed, but okay, I assume
    // this function is only called when that has happened, and otherwise
    // it is not terrible either
    new NewCardWindow();
    // postconditions: none
  }

  /**
   * [CPPRCCC] Shows the main window of Eb
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
