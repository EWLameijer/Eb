package eb.utilities;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class ButtonAction extends AbstractAction {
	private transient Runnable m_action;

	public ButtonAction(Runnable action) {
		m_action = action;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Thread thread = new Thread(m_action);
		thread.start();
	}
}