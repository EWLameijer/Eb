package eb.mainwindow.reviewing;

import java.time.Instant;

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

	/**
	 * Constructor: set the firstTimer object to the blank state, awaiting the signal
	 * that indicates it should store the time at that moment.
	 */
	FirstTimer() {
		reset();
	}

	/**
	 * press: if the FirstTimer object is not storing a time (Instant) now, it will store
	 * the current time in the object.
	 */
	void press() {
		if (m_firstInstant == null) {
			m_firstInstant = Instant.now();
		} // else: instant already recorded, no nothing
	}

	/**
	 * reset: empties the FirstTimer object, so it can be reused to store a new time point (Instant)
	 */
	void reset() {
		m_firstInstant = null;
	}

	/**
	 * getInstant: returns the instant stored in this object, throws an exception if someone tries to
	 * use a FirstTimer object erroneously [I could have returned an Optional, but throwing exceptions
	 * helps find logic errors]
	 * 
	 * @return the Instant stored in this FirstTimer object.
	 */
	Instant getInstant() {
		Utilities.require(m_firstInstant != null, "FirstTimer.getInstant() "
		    + "error: attempt to use time object before any time has been registered.");
		return m_firstInstant;
	}

}