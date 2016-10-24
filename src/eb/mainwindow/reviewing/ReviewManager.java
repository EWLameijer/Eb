package eb.mainwindow.reviewing;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import eb.data.Card;
import eb.data.Deck;
import eb.data.DeckManager;
import eb.data.Review;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Listener;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.mainwindow.MainWindowState;
import eb.utilities.Utilities;

/**
 * FirstTimer registers a time the first time it is activated (set). Subsequent
 * settings do not change its value. Is useful if something has to happen
 * multiple times (like repainting) but only the instant of first usage is
 * important.
 * 
 * @author Eric-Wubbo Lameijer
 */
class FirstTimer {
	Instant m_firstInstant;

	FirstTimer() {
		reset();
	}

	void press() {
		if (m_firstInstant == null) {
			m_firstInstant = Instant.now();
		} // else: instant already recorded, no nothing
	}

	void reset() {
		m_firstInstant = null;
	}

	Instant getInstant() {
		Utilities.require(m_firstInstant != null, "FirstTimer.getInstant() "
		    + "error: attempt to use time object before any time has been registered.");
		return m_firstInstant;
	}

}

/**
 * Manages the review session much like Deck manages the LogicalDeck: there can
 * only be one review at a time
 * 
 * @author Eric-Wubbo Lameijer
 *
 */
public class ReviewManager implements Listener {

	// the instance needed for the Singleton pattern
	private static ReviewManager m_instance;

	private ReviewPanel m_reviewPanel;
	private Deck m_currentDeck;
	private List<Card> m_cardCollection;
	private int m_counter;
	private FirstTimer m_startTimer = new FirstTimer();
	private FirstTimer m_stopTimer = new FirstTimer();
	private boolean m_showAnswer;

	/**
	 * To disable implicit public constructor
	 */
	private ReviewManager() {
	}

	public static ReviewManager getInstance() {
		if (m_instance == null) {
			m_instance = new ReviewManager();
		}
		return m_instance;
	}

	public void start(ReviewPanel reviewPanel) {
		if (reviewPanel != null) {
			m_reviewPanel = reviewPanel;
			m_currentDeck = DeckManager.getContents();
		}
	}

	private void ensureReviewSessionIsValid() {
		if (m_currentDeck != DeckManager.getContents()
		    || m_cardCollection == null) {
			m_currentDeck = DeckManager.getContents();
			initializeReviewSession();
		}
	}

	public List<Review> getReviewResults() {
		ensureReviewSessionIsValid();
		List<Review> listOfReviews = new ArrayList<>();
		for (Card card : m_cardCollection) {
			listOfReviews.add(card.getLastReview());
		}
		return listOfReviews;
	}

	public String getCurrentFront() {
		ensureReviewSessionIsValid();
		if (activeCardExists()) {
			return getCurrentCard().getFront();
		} else {
			return "";
		}
	}

	public void wasRemembered(boolean wasRemembered) {
		ensureReviewSessionIsValid();
		Duration duration = Duration.between(m_startTimer.getInstant(),
		    m_stopTimer.getInstant());
		double duration_in_s = duration.getNano() / 1000_000_000.0
		    + duration.getSeconds();
		Logger.getGlobal().info(m_counter + " " + duration_in_s);
		Review review = new Review(duration, wasRemembered);
		getCurrentCard().addReview(review);
		moveToNextReviewOrEnd();
	}

	@Override
	public void respondToUpdate(Update update) {
		if (update.getType() == UpdateType.CARD_CHANGED) {
			updatePanels();
		} else if (update.getType() == UpdateType.DECK_CHANGED) {
			// It can be that the current card has been deleted, OR another card has
			// been deleted.
			// initializeReviewSession();
			updateCollection();
		} else if (update.getType() == UpdateType.DECK_SWAPPED) {
			initializeReviewSession();
			// cleanUp();
		}
	}

	public void showAnswer() {
		ensureReviewSessionIsValid();
		m_stopTimer.press();
		m_showAnswer = true;
		updatePanels();
	}

	/**
	 * Updates the panels
	 */
	public void updatePanels() {
		if (activeCardExists()) {
			String currentBack = m_showAnswer ? getCurrentBack() : "";
			m_reviewPanel.updatePanels(getCurrentFront(), currentBack, m_showAnswer);
		}
	}

	private void initializeReviewSession() {
		Deck currentDeck = DeckManager.getCurrentDeck();
		int maxNumReviews = currentDeck.getStudyOptions().getReviewSessionSize();
		List<Card> reviewableCards = currentDeck.getReviewableCardList();
		int totalNumberOfReviewableCards = reviewableCards.size();
		Logger.getGlobal()
		    .info("Number of reviewable cards is " + totalNumberOfReviewableCards);
		int numCardsToBeReviewed = Math.min(maxNumReviews,
		    totalNumberOfReviewableCards);
		// now, for best effect, those cards which have expired more recently should
		// be rehearsed first, as other cards probably need to be relearned anyway,
		// and we should try to contain the damage.
		reviewableCards.sort((firstCard, secondCard) -> currentDeck
		    .getTimeUntilNextReview(secondCard)
		    .compareTo(currentDeck.getTimeUntilNextReview(firstCard)));
		// get the first n for the review
		m_cardCollection = new ArrayList<>(
		    reviewableCards.subList(0, numCardsToBeReviewed));
		Collections.shuffle(m_cardCollection);

		m_counter = 0;
		startCardReview();
	}

	private void startCardReview() {
		m_showAnswer = false;
		m_startTimer.reset();
		m_stopTimer.reset();
		m_startTimer.press();
		updatePanels();
	}

	private Card getCurrentCard() {
		Utilities.require(activeCardExists(),
		    "ReviewSession.getCurrentCard() error: there is no current card.");
		return m_cardCollection.get(m_counter);
	}

	private String getCurrentBack() {
		if (activeCardExists()) {
			return getCurrentCard().getBack();
		} else {
			return "";
		}
	}

	private boolean activeCardExists() {
		return m_counter < m_cardCollection.size();
	}

	private void moveToNextReviewOrEnd() {
		if (hasNextCard()) {
			m_counter++;
			startCardReview();
		} else {
			BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
			    MainWindowState.SUMMARIZING.name()));
		}
	}

	/**
	 * Returns the index of the last card in the session.
	 * 
	 * @return the index of the last card in the session.
	 */
	private int indexOfLastCard() {
		return m_cardCollection.size() - 1;
	}

	/**
	 * Returns whether there is a next card to study
	 * 
	 * @return whether there is a next card to study.
	 */
	private boolean hasNextCard() {
		return m_counter + 1 <= indexOfLastCard();
	}

	/**
	 * Returns the number of cards that still need to be reviewed in this session
	 * 
	 * @return the number of cards that still must be reviewed in this session.
	 */
	public int cardsToGoYet() {
		ensureReviewSessionIsValid();
		return m_cardCollection.size() - m_counter;
	}

	/**
	 * If cards are added to (or, more importantly, removed from) the deck, ensure
	 * that the card also disappears from the list of cards to be reviewed
	 */
	public void updateCollection() {
		boolean deletingCurrentCard = false;
		for (int cardIndex = 0; cardIndex < m_cardCollection.size(); cardIndex++) {
			Card currentCard = m_cardCollection.get(cardIndex);
			if (!deckContainsCardWithThisFront(currentCard.getFront())) {
				m_cardCollection.remove(cardIndex);
				deletingCurrentCard = (cardIndex == m_counter);
				if (cardIndex <= m_counter) {
					m_counter--;
				}
			}
		}
		if (deletingCurrentCard) {
			moveToNextReviewOrEnd();
		} else {
			updatePanels();
		}
	}

	/**
	 * Returns whether this deck contains a card with this front. This sounds a
	 * lot like whether the deck contains a card, but since by definition each
	 * card in a deck has a unique front, just checking fronts simplifies things.
	 * 
	 * 
	 * @param front
	 *          the front which may or may not be present on a card in the deck.
	 * @return whether the deck contains a card with the given front.
	 */
	private boolean deckContainsCardWithThisFront(String front) {
		return DeckManager.getCurrentDeck().getCards().getCardWithFront(front)
		    .isPresent();
	}

	/**
	 * Allows the GUI to initialize the panel that displays the reviews
	 * 
	 * @param reviewPanel
	 *          the name of the panel in which the reviews are performed.
	 */
	public void setPanel(ReviewPanel reviewPanel) {
		m_reviewPanel = reviewPanel;
	}

}
