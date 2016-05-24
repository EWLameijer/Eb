package eb.subwindow;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import eb.data.Card;
import eb.data.Deck;
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
	 * Stores which card is to me modified. If card is null, this means that we
	 * are in the process of creating a new card.
	 * 
	 * @param card
	 *          the card to be edited, null in the case that a new card is being
	 *          created.
	 */
	public CardEditingManager(Card card) {

		m_cardToBeModified = card;
		// also open the window with front and back.
	}

	public CardEditingManager() {
		m_cardToBeModified = null;
	}

	private void closeOptionPane() {
		JOptionPane.getRootFrame().dispose();
	}

	private boolean inCardCreatingMode() {
		return m_cardToBeModified == null;
	}

	public void activateCardEditingWindow(Card card) {
		if (!c_cardsBeingEdited.contains(card)) {
			m_cardEditingWindow = CardEditingWindow.display(card.getFront(),
			    card.getBack(), this);
		}
	}

	public void activateCardCreationWindow() {
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
		// Case 1 of 4: If both fields are blank, simply dispose of the window;
		// the user has changed his or her mind
		if (frontText.equals("") && backText.equals("")) {
			endEditing();
			return;
		}

		// Case 2 of 4: the front is empty, the back is not. This is not hasty
		// closing (pressing ENTER instead of ESCAPE), this is an error.
		if (!Utilities.isStringValidIdentifier(frontText)) {
			String verb = inCardCreatingMode() ? "add" : "modify";
			JOptionPane.showMessageDialog(null,
			    "Cannot " + verb + " card: the front of a card cannot be blank.",
			    "Cannot " + verb + " card", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Case 3 of 4: the front of the card is new or the front is the same
		// as the old front (when editing). Add the card and be done with it.
		// (well, when adding cards one should not close the new card window)
		Optional<Card> currentCardWithThisFront = Deck.getCardWithFront(frontText);
		if (frontText.equals(getCurrentFront())
		    || !currentCardWithThisFront.isPresent()) {
			submitCardContents(frontText, backText);
			return;
		}

		// Case 4 of 4: there is a current (but different) card with this exact
		// same front. Resolve this conflict.
		Card duplicate = currentCardWithThisFront.get();
		JButton reeditButton = new JButton("Re-edit card");
		reeditButton.addActionListener(e -> {
			closeOptionPane();
		});

		JButton mergeButton = new JButton("Merge backs of cards");
		mergeButton.addActionListener(e -> {
			String currentBack = backText;
			String otherBack = duplicate.getBack();
			String newBack = currentBack + "; " + otherBack;
			m_cardEditingWindow.updateContents(frontText, newBack);
			Deck.removeCard(duplicate);
			closeOptionPane();
		});
		JButton deleteThisButton = new JButton("Delete this card");
		deleteThisButton.addActionListener(e -> {
			closeOptionPane();
			if (inCardCreatingMode()) {
				m_cardEditingWindow.updateContents("", "");
			} else {
				Deck.removeCard(m_cardToBeModified);
				endEditing();
			}
		});
		JButton deleteOtherButton = new JButton("Delete the other card");
		deleteOtherButton.addActionListener(e -> {
			Deck.removeCard(duplicate);
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
			Deck.addCard(candidateCard);
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
