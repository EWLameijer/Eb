package eb;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager<T extends Listener> {
	private final List<T> m_listeners = new ArrayList<>();
	private final String m_eventId;

	ListenerManager(String eventId) {
		m_eventId = eventId;
	}

	public void addListener(T listener, String id) {
		Utilities.require(listener != null, "ListenerManager.addListener "
		    + "error: listener object should not be null");
		Utilities.require(id != null, "ListenerManager.addListener "
		    + "error: identifier string should not be null");
		Utilities.require(m_eventId.equals(id), "ListenerManager.addListener "
		    + "error: identifier string " + id + " is not recognized.");
		Utilities.require(!m_listeners.contains(listener),
		    "ListenerManager.addListener error: one should not add the same "
		        + "listener twice.");
		// if everything is correct, just do the work.
		m_listeners.add(listener);

	}

	public void notifyListeners() {
		for (T listener : m_listeners) {
			listener.respondToEventType(m_eventId);
		}
	}

}
