package eb;

import javax.swing.JPanel;

public abstract class InputPanel extends JPanel {
	protected void notifyOfChange() {
		BlackBoard.post(UpdateType.INPUTFIELD_CHANGED);
	}
	
	abstract boolean equals(InputPanel otherInputPanel);

}
