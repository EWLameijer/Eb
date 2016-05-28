package eb.mainwindow.reviewing;

import java.time.Instant;
import java.util.List;

import eb.data.Deck;
import eb.data.LogicalDeck;
import eb.data.Review;
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
public class Reviewer {

	static ReviewSession c_session;
	static ReviewPanel c_reviewPanel;
	static LogicalDeck c_currentDeck;

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
		c_currentDeck = Deck.getContents();
	}

	private static void ensureReviewSessionIsValid() {
		if (c_currentDeck != Deck.getContents() || c_session == null) {
			c_currentDeck = Deck.getContents();
			c_session = new ReviewSession(c_reviewPanel);
		}
	}

	public static List<Review> getReviewResults() {
		ensureReviewSessionIsValid();
		return c_session.getReviewResults();
	}

	public static String getCurrentFront() {
		ensureReviewSessionIsValid();
		return c_session.getCurrentFront();
	}

	public static void wasRemembered(boolean wasRemembered) {
		ensureReviewSessionIsValid();
		c_session.wasRemembered(wasRemembered);
	}

	public static void showAnswer() {
		ensureReviewSessionIsValid();
		c_session.showAnswer();
	}
}
