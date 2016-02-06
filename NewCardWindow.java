package eb;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

/**
 * [CC] NewCardWindow allows the user to create a new card in the GUI, and to
 * send it for checking and storage to the Deck.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class NewCardWindow extends JFrame {
  // [CCCC] Default serialization ID (not used).
  private static final long serialVersionUID = 3419171802910744055L;

  // [CCCC] Allows the creation/editing of the content on the front of the
  // card
  private JTextPane m_frontOfCard;

  // [CCCC] Allows the creation/editing of the contents of the back of the card
  private JTextPane m_backOfCard;

  // [CCCC] The button to cancel creating this card, and return to the calling
  // window
  private JButton m_cancelButton;

  // [CCCC] The button to press that requests the current deck to check whether
  // this card is a valid/useable card (so no duplicate of an existing card,
  // for example) and if so, to add it.
  private JButton m_okButton;

  /**
   * [CC] The <code>EnterKeyListener</code> object enables a text field to
   * listen for the enter key, in this case initializing the card storage
   * procedure when it is pressed.
   * 
   * @author Eric-Wubbo Lameijer
   */
  class EnterKeyListener implements KeyListener {

    /**
     * [CPPRCCC] If the user presses the enter key, save the card (same as if
     * the user clicks the OK button)
     */
    @Override
    public void keyPressed(KeyEvent e) {
      // preconditions: none
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        e.consume();
        m_okButton.doClick();
      }
      // postconditions: none
    }

    /**
     * [CPPRCCC] Special handling of key being released (dummy method: does
     * nothing)
     */
    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    /**
     * [CPPRCCC] Special handling of key being typed (dummy method, does
     * nothing).
     */
    @Override
    public void keyTyped(KeyEvent arg0) {
    }
  }

  /**
   * [CC] Listens for the escape key, closes the screen if it is pressed
   * 
   * @author Eric-Wubbo Lameijer
   */
  class EscapeKeyListener implements KeyListener {

    /**
     * [CPPRCCC] If the user presses the escape key, dispose of the candidate
     * card and close the 'New Card' window (same as if the user clicks the
     * Cancel button)
     */
    @Override
    public void keyPressed(KeyEvent e) {
      // preconditions: none
      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        e.consume();
        m_cancelButton.doClick();
      }
      // postconditions: none
    }

    /**
     * [CPPRCCC] Special handling of key being released (dummy method: does
     * nothing)
     */
    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    /**
     * [CPPRCCC] Special handling of key being typed (dummy method, does
     * nothing).
     */
    @Override
    public void keyTyped(KeyEvent arg0) {
    }
  }

  /**
   * [CPPRCCC] Closes the frame, removing all its contents.
   */
  private void close() {
    // preconditions: none
    this.dispose();
    // postconditions: none
  }

  /**
   * [CPPRCCC] Converts the current contents of the NewCardWindow into a card
   * (with front and back as defined by the contents of the front and back
   * panels) and submit it to the current deck. The card may or may not be
   * accepted, depending on the front being valid, and not a duplicate of
   * another front.
   */
  private void submitCandidateCardToDeck() {
    // preconditions: none: this is a button-press-response function,
    // and should therefore always activate when the associated button
    // (in this case the OK button) is pressed.

    // logging
    System.out.printf("Front: %s, back %s\n", m_frontOfCard.getText(),
        m_backOfCard.getText());

    // create a new card
    Card candidateCard = new Card(m_frontOfCard.getText(),
        m_backOfCard.getText());
    if (Deck.canAddCard(candidateCard)) {
      Deck.addCard(candidateCard);
      m_frontOfCard.setText("");
      m_backOfCard.setText("");
    } else {
      // basically, two things can go wrong; either the front is empty
      // or it is a duplicate
      String errorMessage;
      if (!Utilities.isStringValidIdentifier(m_frontOfCard.getText())) {
        errorMessage = "Cannot add card: the front of a card cannot be blank.";
      } else {
        // must be duplicate card
        errorMessage = "Cannot add card: there already is another card with "
            + "the same front";
      }
      JOptionPane.showMessageDialog(null, errorMessage, "Cannot add card",
          JOptionPane.ERROR_MESSAGE);
    }
    m_frontOfCard.requestFocusInWindow();
    // postconditions: If adding succeeded, the front and back should
    // be blank again, if it didn't, they should be the same as they were
    // before (so nothing changed). Since the logic of the postcondition
    // would be as complex as the logic of the function itself, it's kind
    // of double and I skip it here.
  }

  /**
   * [CPPRCCC] Creates a <code>NewCardWindow</code> to add cards to the current
   * deck.
   */
  NewCardWindow() {
    // preconditions: none (we can assume the user clicked the appropriate
    // button, and even otherwise there is not a big problem)

    setLayout(new GridBagLayout());

    // Create the panel to edit the front of the card, and make enter
    // and tab transfer focus to the panel for editing the back of the card.
    // Escape should cancel the card-creating process and close the
    // NewCardWindow
    m_frontOfCard = new JTextPane();
    Utilities.makeTabAndEnterTransferFocus(m_frontOfCard);
    m_frontOfCard.addKeyListener(new EscapeKeyListener());

    // Now create the panel to edit the back of the card; make tab transfer
    // focus to the front (for editing that), escape should (like for the
    // front panel) again cancel editing and close the NewCardWindow.
    // Pressing the Enter key, however, should try save the card, and i
    m_backOfCard = new JTextPane();
    Utilities.makeTabTransferFocus(m_backOfCard);
    m_backOfCard.addKeyListener(new EnterKeyListener());
    m_backOfCard.addKeyListener(new EscapeKeyListener());

    // Now ensure that front and back of card are shown nicely in the
    // window.
    // Also add the two buttons (Cancel and OK).
    JSplitPane upperPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        m_frontOfCard, m_backOfCard);
    upperPanel.setResizeWeight(0.5);
    GridBagConstraints frontConstraints = new GridBagConstraints();
    frontConstraints.gridx = 0;
    frontConstraints.gridy = 0;
    frontConstraints.weightx = 1;
    frontConstraints.weighty = 1;
    frontConstraints.insets = new Insets(0, 0, 5, 0);
    frontConstraints.fill = GridBagConstraints.BOTH;
    add(upperPanel, frontConstraints);
    m_cancelButton = new JButton("Cancel");
    m_cancelButton.addActionListener(e -> close());
    m_okButton = new JButton("Ok");
    m_okButton.addActionListener(e -> submitCandidateCardToDeck());

    // we just want tab to cycle from the front to the back of the card,
    // and vice versa, and not hit the buttons
    m_cancelButton.setFocusable(false);
    m_okButton.setFocusable(false);

    // add the buttons to the window
    JPanel buttonPane = new JPanel();
    buttonPane.add(m_cancelButton);
    buttonPane.add(m_okButton);
    GridBagConstraints buttonPaneConstraints = new GridBagConstraints();
    buttonPaneConstraints.gridx = 0;
    buttonPaneConstraints.gridy = 1;
    buttonPaneConstraints.weightx = 0;
    buttonPaneConstraints.weighty = 0;
    buttonPaneConstraints.insets = new Insets(10, 10, 10, 10);
    add(buttonPane, buttonPaneConstraints);

    // And finally set the general settings of the 'new card'-window.
    setSize(400, 400);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);

    // postconditions: none. The window exists and should henceforth handle
    // its own business using the appropriate GUI elements.
  }
}
