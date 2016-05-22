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

class ReviewSession {
	private List<Card> m_cardCollection = new ArrayList<>();
	private int m_counter = 3;
	private FirstTimer m_startTimer = new FirstTimer();
	private FirstTimer m_stopTimer = new FirstTimer();
	private List<Review> m_reviewResults = new ArrayList<>();

	private Card getCurrentCard() {
		return m_cardCollection.get(m_counter - 1);
	}

	public String getCurrentFront() {
		m_startTimer.press();
		return getCurrentCard().getFront();
	}

	public String getCurrentBack() {
		return getCurrentCard().getBack();
	}

	public void wasRemembered(boolean remembered) {
		m_stopTimer.press();
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
			BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
			    MainWindowState.SUMMARIZING.name()));
		}
	}

	public void activate() {
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

	}

	public void respondToSwappedDeck() {
		activate();
	}

	public boolean hasNextCard() {
		return m_counter > 0;
	}

	public List<Review> getReviewResults() {
		return m_reviewResults;
	}
}

/**
 * Manages the review session much like Deck manages the LogicalDeck: there can
 * only be one review at a time
 * 
 * @author Eric-Wubbo Lameijer
 *
 */
public class Reviewer implements Listener {

	ReviewSession c_session;

	/**
	 * To hide implicit public reviewer
	 */
	private Reviewer() {
		Utilities.require(false, "Reviewer constructor error: one should not "
		    + "try to create an instance of the static reviewer class");
	}

	@Override
	public void respondToUpdate(Update update) {
		// TODO Auto-generated method stub
	}

	public static void start() {

	}

}
