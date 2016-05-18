package eb;

import javax.swing.JButton;
import javax.swing.JPanel;

public class TimedReviewStartPanel extends JPanel {
	
	TimedReviewStartPanel() {
		super();
		JButton startButton = new JButton("Start reviewing");
		JButton postponeButton = new JButton("Postpone reviewing");
		postponeButton.addActionListener(
		    e -> ProgramController.setProgramState(ProgramState.INFORMATIONAL));
		add(startButton);
		add(postponeButton);
	}

}
