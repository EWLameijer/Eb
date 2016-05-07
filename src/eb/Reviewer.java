package eb;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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

public class Reviewer {

	private static List<Card> c_cardCollection = new ArrayList<>();
	private static int c_counter = 3;
	private static FirstTimer c_startTimer = new FirstTimer();
	private static FirstTimer c_stopTimer = new FirstTimer();
	private static List<Review> c_reviewResults = new ArrayList<>();

	/**
	 * To hide implicit public reviewer
	 */
	private Reviewer() {
		Utilities.require(false, "Reviewer constructor error: one should not "
		    + "try to create an instance of the static reviewer class");
	}

	private static Card getCurrentCard() {
		return c_cardCollection.get(c_counter - 1);
	}

	public static String getCurrentFront() {
		c_startTimer.press();
		return getCurrentCard().getFront();
	}

	public static String getCurrentBack() {
		c_stopTimer.press();
		return getCurrentCard().getBack();
	}

	public static void wasRemembered(boolean remembered) {

		Duration duration = Duration.between(c_startTimer.getInstant(),
		    c_stopTimer.getInstant());
		double duration_in_s = duration.getNano() / 1000_000_000.0
		    + duration.getSeconds();
		Logger.getGlobal().info(c_counter + " " + duration_in_s);
		Review review = new Review(duration, remembered);
		c_reviewResults.add(review);
		getCurrentCard().addReview(review);
		c_startTimer.reset();
		c_stopTimer.reset();
		c_counter--;
		if (c_counter <= 0) {
			ProgramController.setProgramState(ProgramState.SUMMARIZING);
		}
	}

	public static void activate() {
		c_reviewResults = new ArrayList<>();
		int maxNumReviews = Deck.getStudyOptions().getReviewSessionSize();
		List<Card> reviewableCards = Deck.getReviewableCardList();
		int totalNumberOfReviewableCards = reviewableCards.size();
		Logger.getGlobal()
		    .info("Number of reviewable cards is " + totalNumberOfReviewableCards);
		c_counter = Math.min(maxNumReviews, totalNumberOfReviewableCards);
		// now, for best effect, those cards which have expired more recently should
		// be rehearsed first, as other cards probably need to be relearned anyway,
		// and we should try to contain the damage.
		reviewableCards
		    .sort((firstCard, secondCard) -> secondCard.getTimeUntilNextReview()
		        .compareTo(firstCard.getTimeUntilNextReview()));
		// get the first n for the review
		c_cardCollection = reviewableCards.subList(0, c_counter);
		Collections.shuffle(c_cardCollection);

	}

	public static void respondToSwappedDeck() {
		activate();
	}

	public static boolean hasNextCard() {
		return c_counter > 0;
	}

	public static List<Review> getReviewResults() {
		return c_reviewResults;
	}

}
