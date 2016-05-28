package eb.eventhandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eb.utilities.Utilities;

/**
 * The BlackBoard class serves as a kind of blackboard for central
 * communication. Events are posted to the blackboards with a message string,
 * and the blackboard arranges that the listeners interested in that particular
 * kind of update are notified.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class BlackBoard {

	// note that the more logical Map<UpdateType, HashSet<Listener> gives problems
	// if you iterate over the set, as calling respondToUpdate may modify the set
	static Map<UpdateType, ArrayList<Listener>> c_listeners = new HashMap<>();

	// Hide implicit public constructor
	private BlackBoard() {
		Utilities.require(false,
		    "BlackBoard constructor error: BlackBoard is a static utility class "
		        + "and therefore no objects of it should be constructed.");
	}

	public static void post(Update update) {
		List<Listener> listeners = c_listeners.get(update.getType());
		if (listeners != null) {
			for (int index = 0; index < listeners.size(); index++) {
				listeners.get(index).respondToUpdate(update);
			}
		}
	}

	public static void register(Listener listener, UpdateType updateType) {
		Utilities.require(listener != null,
		    "BlackBoard.register() error: listener object should not be null");
		if (!c_listeners.containsKey(updateType)) {
			c_listeners.put(updateType, new ArrayList<Listener>());
		}
		c_listeners.get(updateType).add(listener);
	}

	public static void unRegister(Listener listener, UpdateType updateType) {
		c_listeners.get(updateType).remove(listener);
	}

	public static void unRegister(Listener listener) {
		for (UpdateType key : c_listeners.keySet()) {
			c_listeners.get(key).remove(listener);
		}

	}
}
