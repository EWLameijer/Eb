package eb.mainwindow;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.event.ComponentListener;
import java.beans.EventHandler;
import java.util.List;
import java.util.OptionalDouble;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

import eb.data.DeckManager;
import eb.data.Review;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.mainwindow.reviewing.Reviewer;
import eb.utilities.ProgrammableAction;

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

	private void makeKeystrokeActivateRunnable(JButton button,
	    KeyStroke keyStroke, String actionName, Runnable runnable) {
		button.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionName);
		button.getActionMap().put(actionName, new ProgrammableAction(runnable));
	}

	private void makeButtonAndKeystrokeActivateRunnable(JButton button,
	    KeyStroke keyStroke, String actionName, Runnable runnable) {
		button.addActionListener(e -> runnable.run());
		makeKeystrokeActivateRunnable(button, keyStroke, actionName, runnable);
	}

	private void backToInformationMode() {
		BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
		    MainWindowState.INFORMATIONAL.name()));
	}

	private void backToReviewingMode() {
		// note that REACTIVE is used instead of reviewing, as REACTIVE ensures tha
		// a new review session is created.
		BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
		    MainWindowState.REACTIVE.name()));
	}

	SummarizingPanel() {
		super();
		this.addComponentListener(EventHandler.create(ComponentListener.class, this,
		    "requestFocusInWindow", null, "componentShown"));
		m_reviewsCompletedBPanel = new JPanel();
		m_reviewsCompletedBPanel
		    .addComponentListener(EventHandler.create(ComponentListener.class, this,
		        "requestFocusInWindow", null, "componentShown"));
		// m_backToReactiveModeButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		// .put(KeyStroke.getKeyStroke("pressed ENTER"), "back to reactive mode");
		// m_backToReactiveModeButton.getActionMap().put("back to reactive mode",
		// new ProgrammableAction(() -> toReactiveMode()));
		// m_backToReactiveModeButton.addActionListener(e -> toReactiveMode());
		makeButtonAndKeystrokeActivateRunnable(m_backToReactiveModeButton,
		    KeyStroke.getKeyStroke("pressed ENTER"), "back to reactive mode",
		    () -> toReactiveMode());
		m_reviewsCompletedBPanel.add(m_backToReactiveModeButton);

		m_stillReviewsToDoBPanel = new JPanel();
		m_stillReviewsToDoBPanel
		    .addComponentListener(EventHandler.create(ComponentListener.class, this,
		        "requestFocusInWindow", null, "componentShown"));

		m_backToReviewing.setMnemonic(KeyEvent.VK_G);
		// m_backToReviewing.addActionListener(e -> backToReviewingMode());
		makeButtonAndKeystrokeActivateRunnable(m_backToReviewing,
		    KeyStroke.getKeyStroke('G'), "back to reviewing",
		    () -> backToReviewingMode());
		m_stillReviewsToDoBPanel.add(m_backToReviewing);

		m_backToInformationModeButton.setMnemonic(KeyEvent.VK_B);
		// m_backToInformationModeButton
		// .addActionListener(e -> backToInformationMode());
		makeButtonAndKeystrokeActivateRunnable(m_backToInformationModeButton,
		    KeyStroke.getKeyStroke('B'), "back to information mode",
		    () -> backToInformationMode());
		m_stillReviewsToDoBPanel.add(m_backToInformationModeButton);

		m_buttonPanel = new JPanel();
		m_buttonPanel.setLayout(new CardLayout());
		m_buttonPanel.add(m_reviewsCompletedBPanel, REVIEWS_COMPLETED_MODE);
		m_buttonPanel.add(m_stillReviewsToDoBPanel, STILL_REVIEWS_TODO_MODE);
		add(m_report);
		add(m_buttonPanel);

	}

	private void toReactiveMode() {
		BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
		    MainWindowState.REACTIVE.name()));
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
		if (DeckManager.getCurrentDeck().getCards().getReviewableCardList()
		    .isEmpty()) {
			cardLayout.show(m_buttonPanel, REVIEWS_COMPLETED_MODE);
		} else {
			cardLayout.show(m_buttonPanel, STILL_REVIEWS_TODO_MODE);
		}
	}
}
