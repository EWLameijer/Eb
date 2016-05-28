package eb.subwindow;

import eb.utilities.Utilities;

/**
 * Needed, as enums have to start with the enum values, and you cannot use
 * identifiers which are defined only later.
 * 
 * @author Eric-Wubbo Lameijer
 *
 */
class TimedModusHelper {
	TimedModusHelper() {
		Utilities.require(false, "TimedModusHelper constructor error: "
		    + "TimedModusHelper should never be initialized. ");
	}

	public static final String NORMAL_IDENTIFIER = "normal";
	public static final String TIMED_IDENTIFIER = "timed";
}

public enum TimedModus {

	TRUE(TimedModusHelper.TIMED_IDENTIFIER), FALSE(
	    TimedModusHelper.NORMAL_IDENTIFIER);

	private String m_uiName;

	TimedModus(String name) {
		m_uiName = name;
	}

	public String getName() {
		return m_uiName;
	}

	public static TimedModus stringToTimedModus(String value) {
		Utilities.require(value != null, "TimedModus.stringToTimedModus() error: "
		    + "the incoming value may not be null.");
		if (value.equals(TimedModusHelper.TIMED_IDENTIFIER)) {
			return TimedModus.TRUE;
		} else if (value.equals(TimedModusHelper.NORMAL_IDENTIFIER)) {
			return TimedModus.FALSE;
		} else {
			Utilities.require(false, "TimedModus.stringToTimedModus() error: "
			    + "I don't recognize modus'" + value + "'");
			return null;
		}
	}

}
