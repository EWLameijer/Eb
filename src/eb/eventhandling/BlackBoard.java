package eb.eventhandling;

import java.util.HashMap;
import java.util.HashSet;
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

	static Map<UpdateType, HashSet<Listener>> c_listeners = new HashMap<UpdateType, HashSet<Listener>>();

	public static void post(Update update) {
		HashSet<Listener> listeners = c_listeners.get(update.getType());
		if (listeners != null) {
			listeners.stream().forEach(e -> e.respondToUpdate(update));
		}
	}

	public static void register(Listener listener, UpdateType updateType) {
		Utilities.require(listener != null,
		    "BlackBoard.register() error: listener object should not be null");
		if (!c_listeners.containsKey(updateType)) {
			c_listeners.put(updateType, new HashSet<Listener>());
		}
		c_listeners.get(updateType).add(listener);
	}

	public static void unRegister(Listener listener, UpdateType updateType) {
		c_listeners.get(updateType).remove(listener);
	}
}
