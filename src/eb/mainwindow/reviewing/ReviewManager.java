package eb.mainwindow.reviewing;

import java.time.Duration;
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
	private List<Card> m_cardsToBeReviewed;
	
	// m_counter stores the index of the card in the m_cardsToBeReviewed list that should be reviewed next.
	private int m_counter;
	
	// m_startTimer is activated when the card is shown
	private FirstTimer m_startTimer = new FirstTimer();
	
	// m_stopTimer is activated when the user presses the button to show the answer.
	private FirstTimer m_stopTimer = new FirstTimer();
	
	// Should the answer (back of the card) be shown to the user? 'No'/false when the user is trying to recall the answer, 
	// 'Yes'/true when the user needs to check the answer.
	private boolean m_showAnswer;

	/**
	 * To disable implicit public constructor
	 */
	private ReviewManager() {
	}

	/**
	 * getInstance is the method to call the lone object stored in the ReviewManager Singleton - 
	 * see the documentation on design patterns.
	 * 
	 * @return the only instance of the ReviewManager class that this program should have.
	 */
	public static ReviewManager getInstance() {
		if (m_instance == null) {
			m_instance = new ReviewManager();
		}
		return m_instance;
	}

	/**
	 * start starts the reviewing process,
	 * 
	 * @param reviewPanel
	 */
	// TODO:  
	public void start(ReviewPanel reviewPanel) {
		if (reviewPanel != null) {
			m_reviewPanel = reviewPanel;
			m_currentDeck = DeckManager.getContents();
		}
		initializeReviewSession();
	}

	private void ensureReviewSessionIsValid() {
		if (m_currentDeck != DeckManager.getContents()
		    || m_cardsToBeReviewed == null) {
			m_currentDeck = DeckManager.getContents();
			initializeReviewSession();
		}
	}

	public List<Review> getReviewResults() {
		ensureReviewSessionIsValid();
		List<Review> listOfReviews = new ArrayList<>();
		for (Card card : m_cardsToBeReviewed) {
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
		m_cardsToBeReviewed = new ArrayList<>(
		    reviewableCards.subList(0, numCardsToBeReviewed));
		Collections.shuffle(m_cardsToBeReviewed);

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
		return m_cardsToBeReviewed.get(m_counter);
	}

	private String getCurrentBack() {
		if (activeCardExists()) {
			return getCurrentCard().getBack();
		} else {
			return "";
		}
	}

	private boolean activeCardExists() {
		return m_counter < m_cardsToBeReviewed.size();
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
		return m_cardsToBeReviewed.size() - 1;
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
		return m_cardsToBeReviewed.size() - m_counter;
	}

	/**
	 * If cards are added to (or, more importantly, removed from) the deck, ensure
	 * that the card also disappears from the list of cards to be reviewed
	 */
	public void updateCollection() {
		boolean deletingCurrentCard = false;
		for (int cardIndex = 0; cardIndex < m_cardsToBeReviewed.size(); cardIndex++) {
			Card currentCard = m_cardsToBeReviewed.get(cardIndex);
			if (!deckContainsCardWithThisFront(currentCard.getFront())) {
				m_cardsToBeReviewed.remove(cardIndex);
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
