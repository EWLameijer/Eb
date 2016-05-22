package eb.eventhandling;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DelegatingDocumentListener implements DocumentListener {

	// the function that handles all update requests
	private final Runnable m_handler;

	public DelegatingDocumentListener(Runnable handler) {
		m_handler = handler;
	}

	private void processUpdate() {
		Thread t = new Thread(m_handler);
		t.start();
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		processUpdate();

	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		processUpdate();

	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		processUpdate();
	}

}
