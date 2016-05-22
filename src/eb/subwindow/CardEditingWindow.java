package eb.subwindow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import eb.utilities.Utilities;

/**
 * CardEditingWindow allows the user to add a new card to the deck, or to edit an 
 * existing card.
 * 
 * It is managed by a CardEditingManager object, which checks the returned contents
 * and opens/closes it.
 *
 * @author Eric-Wubbo Lameijer
 */
public class CardEditingWindow extends JFrame {
	// Default serialization ID (not used).
	private static final long serialVersionUID = 3419171802910744055L;

	// Allows the creation/editing of the content on the front of the card.
	private final JTextPane m_frontOfCard;

	// Allows the creation/editing of the contents of the back of the card.
	private final JTextPane m_backOfCard;

	// The button to cancel creating this card, and return to the calling window.
	private final JButton m_cancelButton;

	// The button to press that requests the current deck to check whether this
	// card is a valid/usable card (so no duplicate of an existing card, for
	// example) and if so, to add it.
	private final JButton m_okButton;

	// the managing object to send the resulting texts to.
	private final CardEditingManager m_manager;

	/**
	 * Creates a <code>NewCardWindow</code> to add cards to the current deck.
	 */
	CardEditingWindow(String frontText, String backText,
	    CardEditingManager manager) {
		// preconditions: none (we can assume the user clicked the appropriate
		// button, and even otherwise there is not a big problem)
		Utilities.require(frontText != null, "CardEditingWindow.constructor error: "
		    + " the text for the front of the card should not be null.");
		Utilities.require(backText != null, "CardEditingWindow.constructor error: "
		    + " the text for the back of the card should not be null.");
		Utilities.require(manager != null, "CardEditingWindow.constructor error: "
		    + " the card manager is undefined.");

		m_manager = manager;

		// Create the panel to edit the front of the card, and make enter
		// and tab transfer focus to the panel for editing the back of the card.
		// Escape should cancel the card-creating process and close the
		// NewCardWindow
		m_frontOfCard = new JTextPane();
		m_frontOfCard.setText(frontText);
		Utilities.makeTabAndEnterTransferFocus(m_frontOfCard);
		m_frontOfCard.addKeyListener(new EscapeKeyListener());
		m_frontOfCard.addFocusListener(new CleaningFocusListener());

		// Now create the panel to edit the back of the card; make tab transfer
		// focus to the front (for editing the front again), escape should (like
		// for the front panel) again cancel editing and close the NewCardWindow.
		// Pressing the Enter key, however, should try save the card instead of
		// transferring the focus back to the front-TextArea.
		m_backOfCard = new JTextPane();
		m_backOfCard.setText(backText);
		Utilities.makeTabTransferFocus(m_backOfCard);
		m_backOfCard.addKeyListener(new EnterKeyListener());
		m_backOfCard.addKeyListener(new EscapeKeyListener());
		m_backOfCard.addFocusListener(new CleaningFocusListener());

		// Also add the two buttons (Cancel and OK).
		m_cancelButton = new JButton("Cancel");
		m_okButton = new JButton("Ok");

		// we just want tab to cycle from the front to the back of the card,
		// and vice versa, and not hit the buttons
		m_cancelButton.setFocusable(false);
		m_okButton.setFocusable(false);

		// postconditions: none. The window exists and should henceforth handle
		// its own business using the appropriate GUI elements.
	}

	/**
	 * The <code>EnterKeyListener</code> object enables a text field to listen for
	 * the enter key, in this case initializing the card storage procedure when it
	 * is pressed.
	 *
	 * @author Eric-Wubbo Lameijer
	 */
	class EnterKeyListener implements KeyListener {

		/**
		 * If the user presses the enter key, save the card (same as if the user
		 * clicks the OK button).
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
		 * Special handling of key being released (dummy method: does nothing).
		 */
		@Override
		public void keyReleased(KeyEvent arg0) {
			// Do nothing: only needs to respond to the pressing of the key, not to
			// the release.
		}

		/**
		 * Special handling of key being typed (dummy method, does nothing).
		 */
		@Override
		public void keyTyped(KeyEvent arg0) {
			// Do nothing: only needs to respond to the pressing of the key, not to
			// typing it (or such)
		}
	}

	/**
	 * Listens for the escape key, closes the screen if it is pressed.
	 *
	 * @author Eric-Wubbo Lameijer
	 */
	class EscapeKeyListener implements KeyListener {

		/**
		 * If the user presses the escape key, dispose of the candidate card and
		 * close the 'New Card' window (same as if the user clicks the Cancel
		 * button).
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
		 * Special handling of key being released (dummy method: does nothing).
		 */
		@Override
		public void keyReleased(KeyEvent arg0) {
			// Do nothing: only needs to respond to the pressing of the key, not to
			// the release.
		}

		/**
		 * Special handling of key being typed (dummy method, does nothing).
		 */
		@Override
		public void keyTyped(KeyEvent arg0) {
			// Do nothing: only needs to respond to the pressing of the key, not to
			// typing it (or such)
		}
	}

	class CleaningFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent arg0) {
			// noop
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			trimFields();
		}

	}

	public void trimFields() {
		m_frontOfCard.setText(m_frontOfCard.getText().trim());
		m_backOfCard.setText(m_backOfCard.getText().trim());
	}

	/**
	 * Converts the current contents of the NewCardWindow into a card (with front
	 * and back as defined by the contents of the front and back panels) and
	 * submit it to the current deck. The card may or may not be accepted,
	 * depending on whether the front is valid, and not a duplicate of another
	 * front.
	 */
	private void submitCandidateCardToDeck() {
		// preconditions: none: this is a button-press-response function,
		// and should therefore always activate when the associated button
		// (in this case the OK button) is pressed.

		trimFields();
		final String frontText = m_frontOfCard.getText();
		final String backText = m_backOfCard.getText();

		m_manager.processProposedContents(frontText, backText);
// m_frontOfCard.requestFocusInWindow();
		
		// postconditions: If adding succeeded, the front and back should
		// be blank again, if it didn't, they should be the same as they were
		// before (so nothing changed). Since the logic of the postcondition
		// would be as complex as the logic of the function itself, it's kind
		// of double and I skip it here.
	}

	/**
	 * Initializes the components of the NewCardWindow.
	 */
	void init() {
		m_cancelButton.addActionListener(e -> m_manager.endEditing());
		m_okButton.addActionListener(e -> submitCandidateCardToDeck());

		// now add the buttons to the window
		final JPanel buttonPane = new JPanel();
		buttonPane.add(m_cancelButton);
		buttonPane.add(m_okButton);

		// Now create a nice (or at least acceptable-looking) layout.
		final JSplitPane upperPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		    new JScrollPane(m_frontOfCard), new JScrollPane(m_backOfCard));
		upperPanel.setResizeWeight(0.5);
		setLayout(new GridBagLayout());
		final GridBagConstraints frontConstraints = new GridBagConstraints();
		frontConstraints.gridx = 0;
		frontConstraints.gridy = 0;
		frontConstraints.weightx = 1;
		frontConstraints.weighty = 1;
		frontConstraints.insets = new Insets(0, 0, 5, 0);
		frontConstraints.fill = GridBagConstraints.BOTH;
		add(upperPanel, frontConstraints);

		final GridBagConstraints buttonPaneConstraints = new GridBagConstraints();
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
	}

	/**
	 * Shows the NewCardWindow. Is necessary to accommodate the nullness checker,
	 * which requires separation of the constructor and setting/manipulating its
	 * fields (of course, warnings could be suppressed, but programming around it
	 * seemed more elegant).
	 */
	static CardEditingWindow display(String frontText, String backText,
	    CardEditingManager manager) {
		// preconditions: inputs should not be null
		Utilities.require(frontText != null, "CardEditingWindow.display() error: "
		    + " the text for the front of the card should not be null.");
		Utilities.require(backText != null, "CardEditingWindow.display() error: "
		    + " the text for the back of the card should not be null.");
		Utilities.require(manager != null, "CardEditingWindow.display() error: "
		    + " the card manager is undefined.");

		final CardEditingWindow newCardWindow = new CardEditingWindow(frontText,
		    backText, manager);
		newCardWindow.init();
		return newCardWindow;
	}

	public void updateContents(String frontText, String backText) {
		Utilities.require(frontText != null,
		    "CardEditingWindow.updateContents() error: "
		        + " the text for the front of the card should not be null.");
		Utilities.require(backText != null,
		    "CardEditingWindow.updateContents() error: "
		        + " the text for the back of the card should not be null.");
		m_frontOfCard.setText(frontText);
		m_backOfCard.setText(backText);

	}
}
