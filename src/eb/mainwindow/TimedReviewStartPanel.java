package eb.mainwindow;

import javax.swing.JButton;
import javax.swing.JPanel;

import eb.eventhandling.BlackBoard;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;

@SuppressWarnings("serial")
public class TimedReviewStartPanel extends JPanel {

	TimedReviewStartPanel() {
		super();
		JButton startButton = new JButton("Start reviewing");
		JButton postponeButton = new JButton("Postpone reviewing");
		postponeButton.addActionListener(
		    e -> BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
		        MainWindowState.INFORMATIONAL.name())));
		add(startButton);
		add(postponeButton);
	}

}
