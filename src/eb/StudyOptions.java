package eb;

import java.io.Serializable;

/**
 * The StudyOptions class can store the learning settings that we want to use
 * for a particular deck, but in some cases can exist outside any particular
 * deck (for example Eb's default options).
 * 
 * @author Eric-Wubbo Lameijer
 */
public class StudyOptions implements Serializable {

	// The serialization ID. Automatically generated, can be ignored.
	private static final long serialVersionUID = -5967297039338080285L;

	// The interval between creation of a card and when it should first be
	// reviewed. Can be set by the user.
	private TimeInterval m_initialInterval;

	// The default initial interval.
	private final static TimeInterval DEFAULT_INITIAL_INTERVAL = new TimeInterval(
	    10.0, TimeUnit.MINUTE);

	/**
	 * Returns the interval that a newly created card has to wait before it should
	 * be reviewed (after all, reviewing a card 2 seconds after making it probably
	 * won't teach you much, as it is still too fresh in memory.
	 * 
	 * @return the interval a newly created card has to wait before it can be
	 *         reviewed for the first time.
	 */
	TimeInterval getInitialInterval() {
		// preconditions: none; the constructor and setters should have taken care
		// that the interval is correct.
		return m_initialInterval;
		// postconditions: none. This is a simple getter method that should not
		// change anything.
	}

	// timed? bool and timedinterval

	/**
	 * StudyOptions constructor; sets all elements to proper initial values.
	 */
	private StudyOptions(TimeInterval initialInterval) {
		// preconditions: none. Is private constructor, should be fed valid values
		// internally
		m_initialInterval = new TimeInterval(initialInterval);
		// postconditions: none. Should work.
	}

	/**
	 * Returns the study option settings used by Eb by default.
	 * 
	 * @return Eb's default study options
	 */
	public static StudyOptions getDefault() {
		// preconditions: none
		return new StudyOptions(DEFAULT_INITIAL_INTERVAL);
		// postconditions: none. Should have worked.
	}
}
