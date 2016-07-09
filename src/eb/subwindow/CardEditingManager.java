package eb.subwindow;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import eb.data.Card;
import eb.data.DeckManager;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.utilities.Utilities;

/**
 * CardEditingManager coordinates the flow of information from the window that
 * requests a card to be created/edited to the UI-element that actually does the
 * editing.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class CardEditingManager {

	// is null in case of creating a new card.
	private Card m_cardToBeModified;

	private CardEditingWindow m_cardEditingWindow;

	// prevent a card from being edited in two windows at the same time.
	private static Set<Card> c_cardsBeingEdited = new HashSet<>();

	/**
	 * Stores which card is to me modified.
	 * 
	 * @param card
	 *          the card to be edited
	 */
	public CardEditingManager(Card card) {
		Utilities.require(card != null, "CardEditingManager constructor error: "
		    + "the card to be edited should not be null.");
		m_cardToBeModified = card;
		activateCardEditingWindow(card);
	}

	/**
	 * Creates a CardEditingManager that allows the user to create a new card.
	 */
	public CardEditingManager() {
		m_cardToBeModified = null;
		activateCardCreationWindow();
	}

	private void closeOptionPane() {
		JOptionPane.getRootFrame().dispose();
	}

	public boolean inCardCreatingMode() {
		return m_cardToBeModified == null;

	}

	/**
	 * Shows the card editing window; however, has a guard that prevents the same
	 * card from being edited in two different windows.
	 * 
	 * @param card
	 *          the card to be edited.
	 */
	private void activateCardEditingWindow(Card card) {
		if (!c_cardsBeingEdited.contains(card)) {
			m_cardEditingWindow = CardEditingWindow.display(card.getFront(),
			    card.getBack(), this);
		}
	}

	private void activateCardCreationWindow() {
		m_cardEditingWindow = CardEditingWindow.display("", "", this);
	}

	private String getCurrentFront() {
		if (m_cardToBeModified == null) {
			return "";
		} else {
			return m_cardToBeModified.getFront();
		}
	}

	public void processProposedContents(String frontText, String backText) {
		// Case 1 of 3: there are empty fields. Or at least: the front is empty.
		// Investigate the exact problem.
		if (frontText.isEmpty()) {
			// if back is empty, then this is just a hasty return. Is okay.
			if (backText.isEmpty()) {
				endEditing();
			} else {
				// back is filled: so there is an error
				String verb = inCardCreatingMode() ? "add" : "modify";
				JOptionPane.showMessageDialog(null,
				    "Cannot " + verb + " card: the front of a card cannot be blank.",
				    "Cannot " + verb + " card", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			// front text is not empty. Now, this can either be problematic or not.

			// Case 2 of 3: the front of the card is new or the front is the same
			// as the old front (when editing). Add the card and be done with it.
			// (well, when adding cards one should not close the new card window)
			Optional<Card> currentCardWithThisFront = DeckManager.getCurrentDeck()
			    .getCards().getCardWithFront(frontText);
			if (frontText.equals(getCurrentFront())
			    || !currentCardWithThisFront.isPresent()) {
				submitCardContents(frontText, backText);
			} else {

				// Case 3 of 3: there is a current (but different) card with this exact
				// same front. Resolve this conflict.
				Card duplicate = currentCardWithThisFront.get();
				handleCardBeingDuplicate(frontText, backText, duplicate);
			}
		}
	}

	private void handleCardBeingDuplicate(String frontText, String backText,
	    Card duplicate) {

		JButton reeditButton = new JButton("Re-edit card");
		reeditButton.addActionListener(e -> closeOptionPane());

		JButton mergeButton = new JButton("Merge backs of cards");
		mergeButton.addActionListener(e -> {
			String currentBack = backText;
			String otherBack = duplicate.getBack();
			String newBack = currentBack + "; " + otherBack;
			closeOptionPane();
			m_cardEditingWindow.updateContents(frontText, newBack);
			DeckManager.getCurrentDeck().getCards().removeCard(duplicate);

		});
		JButton deleteThisButton = new JButton("Delete this card");
		deleteThisButton.addActionListener(e -> {
			closeOptionPane();
			if (inCardCreatingMode()) {
				m_cardEditingWindow.updateContents("", "");
			} else {
				DeckManager.getCurrentDeck().getCards().removeCard(m_cardToBeModified);
				endEditing();
			}
		});
		JButton deleteOtherButton = new JButton("Delete the other card");
		deleteOtherButton.addActionListener(e -> {
			DeckManager.getCurrentDeck().getCards().removeCard(duplicate);
			closeOptionPane();
			submitCardContents(frontText, backText);
		});
		Object[] buttons = { reeditButton, mergeButton, deleteThisButton,
		    deleteOtherButton };
		JOptionPane.showOptionDialog(null,
		    "A card with this front already exists; on the back is the text '"
		        + duplicate.getBack() + "'",
		    "A card with this front already exists. What do you want to do?", 0,
		    JOptionPane.QUESTION_MESSAGE, null, buttons, null);
	}

	/**
	 * Submits these contents to the deck, and closes the editing window if
	 * appropriate.
	 * 
	 * @param frontText
	 * @param backText
	 */
	private void submitCardContents(String frontText, String backText) {
		if (inCardCreatingMode()) {
			final Card candidateCard = new Card(frontText, backText);
			DeckManager.getCurrentDeck().getCards().addCard(candidateCard);
			m_cardEditingWindow.updateContents("", "");
			m_cardEditingWindow.focusFront();
		} else {
			// in editing mode
			m_cardToBeModified.setFront(frontText);
			m_cardToBeModified.setBack(backText);
			c_cardsBeingEdited.remove(m_cardToBeModified);
			m_cardEditingWindow.dispose();
		}
		BlackBoard.post(new Update(UpdateType.CARD_CHANGED));
	}

	public void endEditing() {
		if (!inCardCreatingMode()) {
			c_cardsBeingEdited.remove(m_cardToBeModified);
		}
		m_cardEditingWindow.dispose();
	}

}
