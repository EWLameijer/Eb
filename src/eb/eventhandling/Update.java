package eb.eventhandling;

import eb.utilities.Utilities;

public class Update {
	UpdateType m_updateType;
	String m_contents;

	public Update(UpdateType updateType) {
		Utilities.require(updateType != UpdateType.PROGRAMSTATE_CHANGED,
		    "Update constructor error: must give second parameter when "
		        + "the program state changes.");
		init(updateType, "");

	}

	private void init(UpdateType updateType, String string) {
		m_updateType = updateType;
		m_contents = string;
	}

	public UpdateType getType() {
		return m_updateType;
	}

	public String getContents() {
		return m_contents;
	}

	/**
	 * Produces an update; the exact value of the update is given with the String
	 * "contents". Note that contents can (so far) only be provided for program
	 * state (Main window state) updates.
	 * 
	 * @param updateType
	 * @param contents
	 */
	public Update(UpdateType updateType, String contents) {
		Utilities.require(updateType == UpdateType.PROGRAMSTATE_CHANGED,
		    "Update constructor error: can only give second parameter when "
		        + "the program state changes.");
		init(updateType, contents);

	}

}
