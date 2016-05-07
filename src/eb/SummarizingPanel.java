package eb;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.util.List;
import java.util.OptionalDouble;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SummarizingPanel extends JPanel {
	JLabel m_report = new JLabel();
	JButton m_backToInformationModeButton = new JButton(
	    "Back to information screen");
	JButton m_backToReactiveModeButton = new JButton(
	    "Back to information screen");
	JButton m_backToReviewing = new JButton("Go to next round of reviews");
	JPanel m_buttonPanel;
	JPanel m_reviewsCompletedBPanel;
	JPanel m_stillReviewsToDoBPanel;

	private static final String REVIEWS_COMPLETED_MODE = "reviews completed";
	private static final String STILL_REVIEWS_TODO_MODE = "still reviews to do";

	SummarizingPanel() {
		super();

		m_reviewsCompletedBPanel = new JPanel();
		m_backToReactiveModeButton.addActionListener(
		    e -> ProgramController.setProgramState(ProgramState.REACTIVE));
		m_reviewsCompletedBPanel.add(m_backToReactiveModeButton);

		m_stillReviewsToDoBPanel = new JPanel();
		m_backToInformationModeButton.addActionListener(
		    e -> ProgramController.setProgramState(ProgramState.INFORMATIONAL));
		m_stillReviewsToDoBPanel.add(m_backToInformationModeButton);
		m_backToReviewing.addActionListener(
		    e -> ProgramController.setProgramState(ProgramState.REVIEWING));
		m_stillReviewsToDoBPanel.add(m_backToReviewing);

		m_buttonPanel = new JPanel();
		m_buttonPanel.setLayout(new CardLayout());
		m_buttonPanel.add(m_reviewsCompletedBPanel, REVIEWS_COMPLETED_MODE);
		m_buttonPanel.add(m_stillReviewsToDoBPanel, STILL_REVIEWS_TODO_MODE);
		add(m_report);
		add(m_buttonPanel);

	}

	private String optionalDoubleToString(OptionalDouble d) {
		if (d.isPresent()) {
			return String.format("%.2f", d.getAsDouble());
		} else {
			return "not applicable";
		}
	}

	@Override
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
		CardLayout cardLayout = (CardLayout) m_buttonPanel.getLayout();
		if (Deck.getReviewableCardList().isEmpty()) {
			cardLayout.show(m_buttonPanel, REVIEWS_COMPLETED_MODE);
		} else {
			cardLayout.show(m_buttonPanel, STILL_REVIEWS_TODO_MODE);
		}
	}
}
