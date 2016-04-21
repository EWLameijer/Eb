package eb;

import java.io.Serializable;
import java.util.Objects;

/**
 * The StudyOptions class can store the learning settings that we want to use
 * for a particular deck. However, in some cases a StudyOptions object can exist
 * outside any particular deck (for example Eb's default options).
 *
 * @author Eric-Wubbo Lameijer
 */
public class StudyOptions implements Serializable {

	// The serialization ID. Automatically generated, can be ignored.
	private static final long serialVersionUID = -5967297039338080285L;

	// The interval between creation of a card and when it should first be
	// reviewed. Can be set by the user.
	private final TimeInterval m_initialInterval;

	// The default initial interval.
	private static final TimeInterval DEFAULT_INITIAL_INTERVAL = new TimeInterval(
	    10.0, TimeUnit.MINUTE);

	/**
	 * StudyOptions constructor; sets all elements to proper initial values.
	 *
	 * @param initialInterval
	 *          the interval that Eb waits after creation of a card before showing
	 *          it to the user.
	 */
	StudyOptions(TimeInterval initialInterval) {
		// preconditions: none. Is private constructor, should be fed valid values
		// internally
		m_initialInterval = new TimeInterval(initialInterval);
		// postconditions: none. Should work.
	}

	/**
	 * Returns the interval that a newly created card has to wait before it should
	 * be reviewed (after all, reviewing a card 2 seconds after making it probably
	 * won't teach you much, as it is still too fresh in your memory).
	 *
	 * @return the interval a newly created card has to wait before it can be
	 *         reviewed for the first time
	 */
	TimeInterval getInitialInterval() {
		// preconditions: none. The constructor and setters should have taken care
		// that the interval is valid.
		return m_initialInterval;
		// postconditions: none. This is a simple getter method that should not
		// change anything.
	}

	/**
	 * Returns Eb's default study option settings.
	 *
	 * @return Eb's default study options
	 */
	public static StudyOptions getDefault() {
		// preconditions: none
		return new StudyOptions(DEFAULT_INITIAL_INTERVAL);
		// postconditions: none. Should have worked.
	}

	/**
	 * Whether the contents of this StudyOptions object equal those of another
	 * (StudyOptions) object.
	 * 
	 * @param otherObject
	 *          the object to compare this StudyOptions object with
	 * 
	 * @return whether the contents of the other object equal the contents of this
	 *         particular object
	 */
	@Override
	public boolean equals(Object otherObject) {
		if (this == otherObject) {
			return true;
		} else if (otherObject == null) {
			return false;
		} else if (getClass() != otherObject.getClass()) {
			return false;
		} else {
			StudyOptions otherOptions = (StudyOptions) otherObject;
			return m_initialInterval.equals(otherOptions.m_initialInterval);
		}
	}

	public int hashCode() {
		return Objects.hash(m_initialInterval);
	}
}
