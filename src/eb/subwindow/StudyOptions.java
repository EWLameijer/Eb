package eb.subwindow;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import eb.utilities.TimeInterval;
import eb.utilities.TimeUnit;
import eb.utilities.Utilities;

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

	private final TimeInterval m_rememberedCardInterval;
	private static final TimeInterval DEFAULT_REMEMBERED_INTERVAL = new TimeInterval(
	    1.0, TimeUnit.DAY);
	private final TimeInterval m_forgottenCardInterval;
	private static final TimeInterval DEFAULT_FORGOTTEN_INTERVAL = new TimeInterval(
	    1.0, TimeUnit.HOUR);

	// the default number of cards to be reviewed in a single reviewing session
	private static final int DEFAULT_REVIEW_SESSION_SIZE = 20;

	// the number of cards to be reviewed in a single reviewing session (like 20)
	private int m_reviewSessionSize;

	private static final double DEFAULT_LENGTHENING_FACTOR = 5.0;
	private double m_lengtheningFactor;

	private static final TimedModus DEFAULT_IS_TIMED = TimedModus.FALSE;
	private TimedModus m_isTimed;

	private static final TimeInterval DEFAULT_TIMER_INTERVAL = new TimeInterval(
	    5.0, TimeUnit.SECOND);
	private TimeInterval m_timerInterval;

	/**
	 * StudyOptions constructor; sets all elements to proper initial values.
	 *
	 * @param initialInterval
	 *          the interval that Eb waits after creation of a card before showing
	 *          it to the user.
	 */
	StudyOptions(TimeInterval initialInterval,
	    Optional<Integer> reviewSessionSize, TimeInterval rememberedInterval,
	    TimeInterval forgottenInterval, Optional<Double> lengtheningFactor,
	    TimedModus isTimed, TimeInterval timerInterval) {
		// preconditions: none. Is private constructor, should be fed valid values
		// internally
		m_initialInterval = new TimeInterval(initialInterval);
		m_reviewSessionSize = reviewSessionSize.orElse(DEFAULT_REVIEW_SESSION_SIZE);
		m_rememberedCardInterval = new TimeInterval(rememberedInterval);
		m_lengtheningFactor = lengtheningFactor.orElse(DEFAULT_LENGTHENING_FACTOR);
		m_forgottenCardInterval = new TimeInterval(forgottenInterval);
		m_isTimed = isTimed;
		m_timerInterval = timerInterval;
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
	public TimeInterval getInitialInterval() {
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
		return new StudyOptions(DEFAULT_INITIAL_INTERVAL,
		    Optional.of(DEFAULT_REVIEW_SESSION_SIZE), DEFAULT_REMEMBERED_INTERVAL,
		    DEFAULT_FORGOTTEN_INTERVAL, Optional.of(DEFAULT_LENGTHENING_FACTOR),
		    DEFAULT_IS_TIMED, DEFAULT_TIMER_INTERVAL);
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
			return m_initialInterval.equals(otherOptions.m_initialInterval)
			    && m_reviewSessionSize == otherOptions.m_reviewSessionSize
			    && m_rememberedCardInterval
			        .equals(otherOptions.m_rememberedCardInterval)
			    && Utilities.doublesEqualWithinThousands(m_lengtheningFactor,
			        otherOptions.m_lengtheningFactor)
			    && m_forgottenCardInterval
			        .equals(otherOptions.m_forgottenCardInterval)
			    && timerSettingsSameAs(otherOptions);
		}
	}

	private boolean timerSettingsSameAs(StudyOptions otherOptions) {
		if (m_isTimed != otherOptions.m_isTimed)
			return false;
		if (m_isTimed == TimedModus.TRUE) {
			return m_timerInterval.equals(otherOptions.m_timerInterval);
		} else {
			// timer settings off - then length of timer interval does not matter
			return true;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_initialInterval, m_reviewSessionSize,
		    m_rememberedCardInterval, m_forgottenCardInterval, m_isTimed,
		    m_timerInterval);
	}

	public int getReviewSessionSize() {
		return m_reviewSessionSize;
	}

	public TimeInterval getRememberedCardInterval() {
		return m_rememberedCardInterval;
	}

	public TimeInterval getForgottenCardInterval() {
		return m_forgottenCardInterval;
	}

	public double getLengtheningFactor() {
		return m_lengtheningFactor;
	}

	public TimeInterval getTimerInterval() {
		if (m_timerInterval == null) {
			m_timerInterval = DEFAULT_TIMER_INTERVAL;
		}
		return m_timerInterval;
	}

	public boolean isTimed() {
		return m_isTimed == TimedModus.TRUE;
	}

	public TimedModus getTimedModus() {
		return m_isTimed;
	}
}
