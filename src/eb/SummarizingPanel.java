package eb;

import java.awt.Graphics;
import java.util.List;
import java.util.OptionalDouble;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SummarizingPanel extends JPanel {
	JLabel m_report = new JLabel();
	JButton m_backToInformationModeButton = new JButton(
	    "Back to information screen");

	SummarizingPanel() {
		super();
		add(m_report);
		m_backToInformationModeButton.addActionListener(e -> toInformationMode());
		add(m_backToInformationModeButton);
	}

	private void toInformationMode() {
		ProgramController.setProgramState(ProgramState.INFORMATIONAL);
	}

	private String optionalDoubleToString(OptionalDouble d) {
		if (d.isPresent()) {
			return String.format("%.2f", d.getAsDouble());
		} else {
			return "not applicable";
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		List<Review> results = Reviewer.getReviewResults();
		StringBuilder text = new StringBuilder();
		text.append("<html>");
		text.append("<b>Summary</b><br><br>");
		text.append("Cards reviewed<br>");
		long totalNumberOfReviews = results.size();
		text.append("total: " + totalNumberOfReviews + " <br>");
		long correctReviews = results.stream().filter(r -> r.wasSuccess()).count();
		text.append("correctly answered: " + correctReviews + "<br>");
		long incorrectReviews = totalNumberOfReviews - correctReviews;
		text.append("incorrectly answered: " + incorrectReviews + "<br>");
		text.append("<br><br>");
		text.append("time needed for answering<br>");
		OptionalDouble averageTime = results.stream()
		    .mapToDouble(r -> r.getThinkingTime()).average();
		text.append(
		    "average time: " + optionalDoubleToString(averageTime) + "<br>");
		OptionalDouble averageCorrectTime = results.stream()
		    .filter(r -> r.wasSuccess()).mapToDouble(r -> r.getThinkingTime())
		    .average();
		text.append("average time per correct card: "
		    + optionalDoubleToString(averageCorrectTime) + "<br>");
		OptionalDouble averageIncorrectTime = results.stream()
		    .filter(r -> !r.wasSuccess()).mapToDouble(r -> r.getThinkingTime())
		    .average();
		text.append("average time per incorrect card: "
		    + optionalDoubleToString(averageIncorrectTime) + "<br>");
		text.append("</html>");
		m_report.setText(text.toString());
	}
}
