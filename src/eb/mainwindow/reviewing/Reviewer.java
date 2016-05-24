package eb.mainwindow.reviewing;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import eb.data.Card;
import eb.data.Deck;
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

class ReviewSession implements Listener {
	private List<Card> m_cardCollection;
	private int m_counter;
	private FirstTimer m_startTimer = new FirstTimer();
	private FirstTimer m_stopTimer = new FirstTimer();
	private List<Review> m_reviewResults = new ArrayList<>();
	private final ReviewPanel m_reviewPanel;
	private boolean m_showAnswer;

	ReviewSession(ReviewPanel reviewPanel) {
		m_reviewPanel = reviewPanel;
		m_reviewResults = new ArrayList<>();
		int maxNumReviews = Deck.getStudyOptions().getReviewSessionSize();
		List<Card> reviewableCards = Deck.getReviewableCardList();
		int totalNumberOfReviewableCards = reviewableCards.size();
		Logger.getGlobal()
		    .info("Number of reviewable cards is " + totalNumberOfReviewableCards);
		m_counter = Math.min(maxNumReviews, totalNumberOfReviewableCards);
		// now, for best effect, those cards which have expired more recently should
		// be rehearsed first, as other cards probably need to be relearned anyway,
		// and we should try to contain the damage.
		reviewableCards
		    .sort((firstCard, secondCard) -> secondCard.getTimeUntilNextReview()
		        .compareTo(firstCard.getTimeUntilNextReview()));
		// get the first n for the review
		m_cardCollection = reviewableCards.subList(0, m_counter);
		Collections.shuffle(m_cardCollection);
		BlackBoard.register(this, UpdateType.CARD_CHANGED);
		BlackBoard.register(this, UpdateType.DECK_CHANGED);
		startCardReview();
	}

	@Override
	public void respondToUpdate(Update update) {
		if (update.getType() == UpdateType.CARD_CHANGED) {
			updatePanels();
		} else if (update.getType() == UpdateType.DECK_CHANGED) {
			// It can be that the current card has been deleted, OR another card has
			// been deleted.
			updateCollection();
		}

	}

	private void startCardReview() {
		m_showAnswer = false;
		m_startTimer.press();
		updatePanels();
	}

	private Card getCurrentCard() {
		Utilities.require(m_counter > 0,
		    "ReviewSession.getCurrentCard() error: " + "there is no current card.");
		return m_cardCollection.get(m_counter - 1);
	}

	public String getCurrentFront() {
		if (activeCardExists()) {
			return getCurrentCard().getFront();
		} else {
			return "";
		}
	}

	public String getCurrentBack() {
		if (activeCardExists()) {
			return getCurrentCard().getBack();
		} else {
			return "";
		}
	}

	private boolean activeCardExists() {
		return m_counter > 0;
	}

	public void wasRemembered(boolean remembered) {

		Duration duration = Duration.between(m_startTimer.getInstant(),
		    m_stopTimer.getInstant());
		double duration_in_s = duration.getNano() / 1000_000_000.0
		    + duration.getSeconds();
		Logger.getGlobal().info(m_counter + " " + duration_in_s);
		Review review = new Review(duration, remembered);
		m_reviewResults.add(review);
		getCurrentCard().addReview(review);
		m_startTimer.reset();
		m_stopTimer.reset();

		m_counter--;
		if (m_counter <= 0) {
			// clean up
			BlackBoard.unRegister(this);
			BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
			    MainWindowState.SUMMARIZING.name()));
		} else {
			startCardReview();
		}
	}

	public boolean hasNextCard() {
		return m_counter > 1;
	}

	public List<Review> getReviewResults() {
		return m_reviewResults;
	}

	public void updatePanels() {
		String currentBack = m_showAnswer ? getCurrentBack() : "";
		m_reviewPanel.updatePanels(getCurrentFront(), currentBack);
	}

	public void showAnswer() {
		m_stopTimer.press();
		m_showAnswer = true;
		updatePanels();
	}

	/**
	 * If cards are added to (or, more importantly, removed from) the deck, ensure
	 * that the card also disappears from the list of cards to be reviewed
	 */
	public void updateCollection() {
		for (int cardIndex = 0; cardIndex < m_cardCollection.size(); cardIndex++) {
			Card currentCard = m_cardCollection.get(cardIndex);
			if (!Deck.contains(currentCard)) {
				if (cardIndex == m_counter - 1) {
					// current card must be removed
					m_stopTimer.press(); // to handle the wasRemembered well.
					wasRemembered(false); // or true. Doesn't matter if the card is
					                      // removed anyway.
				}
				if (cardIndex <= m_counter - 1) {
					m_counter--;
				}
				m_cardCollection.remove(cardIndex);
				m_reviewResults.remove(cardIndex);
			}
		}
		updatePanels();
	}
}

/**
 * Manages the review session much like Deck manages the LogicalDeck: there can
 * only be one review at a time
 * 
 * @author Eric-Wubbo Lameijer
 *
 */
public class Reviewer {

	static ReviewSession c_session;
	static ReviewPanel c_reviewPanel;

	/**
	 * To hide implicit public reviewer
	 */
	private Reviewer() {
		Utilities.require(false, "Reviewer constructor error: one should not "
		    + "try to create an instance of the static reviewer class");
	}

	public static void start(ReviewPanel reviewPanel) {
		c_reviewPanel = reviewPanel;
		c_session = new ReviewSession(reviewPanel);
	}

	public static List<Review> getReviewResults() {
		if (c_session == null) {
			return new ArrayList<>();
		} else {
			return c_session.getReviewResults();
		}
	}

	public static String getCurrentFront() {
		Utilities.require(c_session != null,
		    "Reviewer.getCurrentFront() error: there is no reviewable card.");

		return c_session.getCurrentFront();
	}

	public static void wasRemembered(boolean wasRemembered) {
		c_session.wasRemembered(wasRemembered);
	}

	public static void showAnswer() {
		c_session.showAnswer();

	}

}
