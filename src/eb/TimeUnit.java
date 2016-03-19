package eb;

import java.time.Duration;
import java.util.Optional;
import java.util.Vector;

/**
 * TimeUnit provides the time units, for use in for example combo boxes so that
 * users can select time intervals for reviewing.
 * 
 * @author Eric-Wubbo Lameijer
 */
public enum TimeUnit {
	SECOND("second(s)", Duration.ofSeconds(1)), MINUTE("minute(s)",
	    Duration.ofMinutes(1)), HOUR("hour(s)", Duration.ofHours(1)), DAY(
	        "day(s)", Duration.ofDays(1)), WEEK("week(s)",
	            Duration.ofDays(7)), MONTH("month(s)", Duration.ofMinutes(43830)), // 365.25
	                                                                               // days
	                                                                               // a
	                                                                               // year,
	                                                                               // 12
	                                                                               // months
	YEAR("year(s)", Duration.ofHours(8766)); // 365.25 days a year

	// The name of the time unit (like "hour(s)").
	private final String m_name;

	// The duration of the time unit (in terms of Java Duration objects).
	private final Duration m_duration;

	/**
	 * Constructs a time unit enum value.
	 * 
	 * @param name
	 *          the name of the time unit (like "second(s)")
	 * @param duration
	 *          the duration of the time unit (as a Java Duration)
	 */
	private TimeUnit(String name, Duration duration) {
		// preconditions: none - I trust the internal checks, especially since this
		// is a private constructor
		m_name = name;
		m_duration = duration;
		// postconditions: none
	}

	/**
	 * Returns the name for the unit in a form that is suitable for the user
	 * interface (so not MINUTE, which the .toString() would produce, but
	 * "minute(s)".
	 * 
	 * @return the name of the unit in a user-interface-friendly form ("day(s")
	 */
	public String getUserInterfaceName() {
		// preconditions: none - we can assume the enum has been initialized
		// properly
		return m_name;
		// postconditions: none - this simple getter should not change anything.
	}

	/**
	 * Returns the names of all time units (like "week(s)") as a String array.
	 * 
	 * @return the names of all time units (like "week(s)") as a String array
	 */
	public static Vector<String> getUnitNames() {
		// preconditions: none, this is an enum, so it should have correct initial
		// values.
		Vector<String> unitNames = new Vector<>();
		for (TimeUnit timeUnit : TimeUnit.values()) {
			unitNames.add(timeUnit.m_name);
		}
		// postconditions: none; this is a getter, so should not have changed
		// anything
		return unitNames;
	}

	/**
	 * C Converts the given string into the appropriate TimeUnit.
	 * 
	 * @param unitAsString
	 *          the unit as a string (like "second(s)") that needs to be converted
	 *          to the proper unit, SECOND
	 */
	public static Optional<TimeUnit> parseUnit(String unitAsString) {
		for (TimeUnit timeUnit : TimeUnit.values()) {
			if (timeUnit.m_name.equals(unitAsString)) {
				return Optional.of(timeUnit);
			}
		}
		// no correct parsing possible
		return Optional.empty();
	}
}