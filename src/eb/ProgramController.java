package eb;

import java.util.ArrayList;
import java.util.List;

public class ProgramController {

	private static ProgramState c_programState = ProgramState.REACTIVE;
	private static List<ProgramStateChangeListener> c_programStateChangeListeners = new ArrayList<>();

	private ProgramController() {
		Utilities.require(false, "ProgramController constructor error: "
		    + " one should not initialize a static class.");
	}

	public static ProgramState getProgramState() {
		return c_programState;
	}

	public static void setProgramState(ProgramState newProgramState) {
		c_programState = newProgramState;
		notifyListeners();
	}

	private static void notifyListeners() {
		for (ProgramStateChangeListener programStateChangeListener : c_programStateChangeListeners) {
			programStateChangeListener.respondToProgramStateChange();
		}
	}

	public static void addProgramStateChangeListener(
	    ProgramStateChangeListener programStateChangeListener) {

		Utilities.require(programStateChangeListener != null,
		    "ProgramController.addProgramStateListener() error: the listener "
		        + "object to be added cannot be null.");
		Utilities.require(
		    !c_programStateChangeListeners.contains(programStateChangeListener),
		    "ProgramController.addProgramStateListener() error: attempt to "
		        + "register the same ProgramStateChangeListener object twice.");

		c_programStateChangeListeners.add(programStateChangeListener);
	}

	public static void respondToSwappedDeck() {
		setProgramState(ProgramState.REACTIVE);
	}

}
