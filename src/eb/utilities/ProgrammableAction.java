package eb.utilities;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

@SuppressWarnings("serial")
public class ProgrammableAction extends AbstractAction {
	private transient Runnable m_action;

	public ProgrammableAction(Runnable action) {
		m_action = action;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		m_action.run();
	}
}