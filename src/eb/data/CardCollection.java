package eb.data;

import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import eb.disk_io.CardConverter;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.utilities.Utilities;

/**
 * CardCollection contains a collection of cards, which forms the content of the
 * deck (so what is stored, what should be learned, not how it should be learned
 * or the file system logistics).
 * 
 * Note that while Cards themselves are considered unique/distinctive if they
 * have either different fronts or different backs, a CardCollection has the
 * additional requirement that all fronts should be unique in the collection (so
 * the number of cards equals the the size of the set of fronts) and there
 * should not be any "empty" fronts.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class CardCollection implements Serializable {

	// the proper auto-generated serialVersionUID as CardCollection should be
	// serializable.
	private static final long serialVersionUID = -6526056675010032709L;

	// the cards in this collection
	private List<Card> m_cards;

	/**
	 * Creates a new CardCollection
	 */
	public CardCollection() {
		m_cards = new LinkedList<>();
	}

	/**
	 * Returns the size of the CardCollection (=the number of cards in the
	 * CardCollection)
	 * 
	 * @return the number of cards in the CardCollection
	 */
	public int getSize() {
		return m_cards.size();
	}

	/**
	 * Write the cards of this collection, in alphabetical order (well, the order
	 * imposed by the default character encoding) to a given writer.
	 * 
	 * @param writer
	 *          the writer to which the cards have to be written.
	 */
	public void writeCards(Writer writer, Function<Card, String> outputter) {
		m_cards.stream()
		    .sorted((firstCard, secondCard) -> firstCard.getFront()
		        .compareTo(secondCard.getFront()))
		    .forEach(card -> CardConverter.writeLine(writer, card, outputter));
	}

	/**
	 * Returns whether this new front (text) does not already occur in the deck -
	 * after all, the deck is a kind of map, with a stimulus (front) leading to
	 * only one response (back).
	 *
	 * @param front
	 *          the front of the card to be checked for uniqueness in the deck
	 *
	 * @return whether the card would be a valid addition to the deck (true) or
	 *         whether the front would be a duplicate of a front already present
	 *         (false)
	 */
	private boolean isNotYetPresentInDeck(String front) {
		return !getCardWithFront(front).isPresent();
	}

	/**
	 * Checks if a certain card can be added to the deck. In practice, this means
	 * that the front is a valid identifier that is not already present in the
	 * deck, and the back is not a null pointer.
	 *
	 * @param card
	 *          the candidate card to be added.
	 *
	 * @return whether the card can legally be added to the deck.
	 */
	private boolean canAddCard(Card card) {
		// preconditions: the card should not be null. Otherwise, all cards,
		// even invalid ones, should be able to be handled by this method.
		Utilities.require(card != null, "LogicalDeck.canAddCard() error: "
		    + "the card to be tested cannot be null.");

		return Utilities.isStringValidIdentifier(card.getFront())
		    && (isNotYetPresentInDeck(card.getFront())) && (card.getBack() != null);

		// postconditions: none; a boolean will be returned.
	}

	/**
	 * Adds a card to the deck. Note that one has to call canAddCard() beforehand.
	 *
	 * @param card
	 *          the card to add to the deck.
	 */
	public void addCard(Card card) {
		// preconditions: card must be 'addable'
		Utilities.require(canAddCard(card),
		    "LogicalDeck.addCard() error: the card "
		        + "that is intended to be added is invalid. The 'canAddCard' "
		        + "method has to be invoked first to check the possibility of the "
		        + "current method.");

		final boolean cardAddSuccessful = m_cards.add(card);

		// postconditions: the deck should have been grown by one.
		Utilities.require(cardAddSuccessful, "LogicalDeck.addCard() error: "
		    + " something has gone wrong while adding the card to the deck.");
		BlackBoard.post(new Update(UpdateType.DECK_CHANGED));
	}

	/**
	 * Returns an optional that contains the card with the given front text - if
	 * such a card exists in the collection, or an empty optional if no card with
	 * such front is present.
	 * 
	 * @param frontText
	 *          the text on the front of the card that is sought
	 * @return an optional that is empty if there is no card with the given front
	 *         in the current collection; and which is 'present' when such a card
	 *         actually exists.
	 */
	public Optional<Card> getCardWithFront(String frontText) {
		// Preconditions: front is a valid identifier.
		Utilities.require(Utilities.isStringValidIdentifier(frontText),
		    "LogicalDeck.isNotYetPresentInDeck() error: the text on the front of the "
		        + "card needs to be a valid identifier, not null or a string with "
		        + "only whitespace characters.");

		for (final Card card : m_cards) {
			if (card.getFront().equals(frontText)) {
				return Optional.of(card); // a card with the same front IS present in
				                          // the deck.
			}
		}

		// if you get here, no card with the checked-for front has been found
		return Optional.empty();

		// Postconditions: none, really. Simple return of a boolean.
	}

	/**
	 * Removes a given card from the collection.
	 * 
	 * @param card
	 *          the card to be removed from the collection
	 */
	public void removeCard(Card card) {
		boolean collectionContainedCard = m_cards.remove(card);
		if (collectionContainedCard) {
			BlackBoard.post(new Update(UpdateType.DECK_CHANGED));
		} else {
			Utilities.require(false, "CardCollection.removeCard() error: "
			    + "the card cannot be removed, as it is not in the deck!");
		}
	}

	/**
	 * Returns the number of reviewing points of a deck, being the sum of the
	 * latest "success streaks" of all cards in the deck. For example a fresh deck
	 * will have 0 points, a 100 card deck where each card has has 2 successful
	 * reviews 200 points, failing a review would bring that back to 2x99=198
	 * points, and so on.
	 * 
	 * @return the number of reviewing points of this deck.
	 */
	public int getReviewingPoints() {
		int totalPoints = 0;
		for (Card card : m_cards) {
			totalPoints += card.streakSize();
		}
		return totalPoints;
	}

	/**
	 * Returns an iterator to the collection, so for example the Deck can loop
	 * over the individual cards.
	 * 
	 * @return an iterator to the cards.
	 */
	public Iterator<Card> getIterator() {
		return m_cards.iterator();
	}

}