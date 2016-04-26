package eb;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	public static String getCurrentFront() {
		c_startTimer.press();
		return c_cardCollection.get(c_counter - 1).getFront();
	}

	public static String getCurrentBack() {
		c_stopTimer.press();
		return c_cardCollection.get(c_counter - 1).getBack();
	}

	public static void wasRemembered(boolean remembered) {
		c_counter--;
		Duration duration = Duration.between(c_startTimer.getInstant(),
		    c_stopTimer.getInstant());
		double duration_in_s = duration.getNano() / 1000_000_000.0
		    + duration.getSeconds();
		System.out.println(c_counter + " " + duration_in_s);
		c_reviewResults.add(new Review(duration, remembered));
		c_startTimer.reset();
		c_stopTimer.reset();
		if (c_counter <= 0) {
			ProgramController.setProgramState(ProgramState.SUMMARIZING);
		}
	}

	public static void activate() {
		c_reviewResults = new ArrayList<>();
		int maxNumReviews = Deck.getStudyOptions().getReviewSessionSize();
		List<Card> reviewableCards = Deck.getReviewableCardList();
		int totalNumberOfReviewableCards = reviewableCards.size();
		System.out.println(
		    "Number of reviewable cards is " + totalNumberOfReviewableCards);
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

	public static boolean hasNextCard() {
		return c_counter > 0;
	}

	public static List<Review> getReviewResults() {
		return c_reviewResults;
	}

}
