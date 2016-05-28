package eb.utilities.ui_elements;

import javax.swing.JPanel;

import eb.eventhandling.BlackBoard;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;

@SuppressWarnings("serial")
public abstract class InputPanel extends JPanel {
	protected void notifyOfChange() {
		BlackBoard.post(new Update(UpdateType.INPUTFIELD_CHANGED));
	}

	abstract boolean equals(InputPanel otherInputPanel);

}
