package eb.data;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

import eb.utilities.Utilities;

/**
 * A Review object stores relevant data about a review, like when it occurred,
 * how long it took, and of course the result. In future, it may also store data
 * like 'review type'.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class Review implements Serializable {
	private static final long serialVersionUID = -3475131013697503513L;
	private final Instant m_instant;
	private final Duration m_thinkingTime;
	private final boolean m_success;

	/**
	 * Constructor for Review objects.
	 * 
	 * @param thinkingTime
	 *          the time the user needed to come up with his or her answer
	 * 
	 * @param wasSuccess
	 *          whether the user knew the answer (true) or didn't (false)
	 */
	public Review(Duration thinkingTime, boolean wasSuccess) {
		m_instant = Instant.now();
		m_thinkingTime = thinkingTime;
		m_success = wasSuccess;
	}

	/**
	 * Returns whether the review was a success.
	 * 
	 * @return true if the review was a success, false if it wasn't.
	 */
	public boolean wasSuccess() {
		// preconditions: none. Review exists
		return m_success;
		// postconditions: none. Simple return of boolean
	}

	/**
	 * Returns the instant of the review
	 * 
	 * @return the instant (the point in time) that the review was performed.
	 */
	public Instant getInstant() {
		// preconditions: none. Instant exists if review exists
		return m_instant;
		// postconditions: none. Simple return of Instant
	}

	public double getThinkingTime() {
		return Utilities.durationToSeconds(m_thinkingTime);
	}

	public String toString() {
		return "";
	}
}
