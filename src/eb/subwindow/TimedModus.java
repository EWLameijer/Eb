package eb.subwindow;

import eb.utilities.Utilities;

class TimedModusHelper {
	public final static String normalIdentifier = "normal";
	public final static String timedIdentifier = "timed";
}

public enum TimedModus {

	TRUE(TimedModusHelper.timedIdentifier), FALSE(
	    TimedModusHelper.normalIdentifier);

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
		if (value.equals(TimedModusHelper.timedIdentifier)) {
			return TimedModus.TRUE;
		} else if (value.equals(TimedModusHelper.normalIdentifier)) {
			return TimedModus.FALSE;
		} else {
			Utilities.require(false, "TimedModus.stringToTimedModus() error: "
			    + "I don't recognize modus'" + value + "'");
			return null;
		}
	}

}
