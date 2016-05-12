package eb;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
class ButtonAction extends AbstractAction {
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

/**
 * The panel used to review cards (shows front, on clicking "Show Answer" the
 * answer is shown, after which the user can confirm or deny that he/she knew
 * the correct answer).
 * 
 * @author Eric-Wubbo Lameijer
 */
@SuppressWarnings("serial")
public class ReviewPanel extends JPanel {

	private CardPanel m_frontOfCardPanel;
	private CardPanel m_backOfCardPanel;
	private JPanel m_buttonPanel;

	private static final String HIDDEN_ANSWER = "HIDDEN_ANSWER";
	private static final String SHOWN_ANSWER = "SHOWN_ANSWER";

	ReviewPanel() {
		super();
		setLayout(new GridBagLayout());
		GridBagConstraints frontOfCardConstraints = new GridBagConstraints();

		frontOfCardConstraints.gridx = 0;
		frontOfCardConstraints.gridy = 0;
		frontOfCardConstraints.gridwidth = 4;
		frontOfCardConstraints.gridheight = 2;
		frontOfCardConstraints.weightx = 4.0;
		frontOfCardConstraints.weighty = 2.0;
		frontOfCardConstraints.fill = GridBagConstraints.BOTH;
		m_frontOfCardPanel = new CardPanel();
		m_frontOfCardPanel.setBackground(Color.PINK);

		add(m_frontOfCardPanel, frontOfCardConstraints);
		GridBagConstraints backOfCardConstraints = new GridBagConstraints();
		backOfCardConstraints.gridx = 0;
		backOfCardConstraints.gridy = 2;
		backOfCardConstraints.gridwidth = 4;
		backOfCardConstraints.gridheight = 2;
		backOfCardConstraints.weightx = 4.0;
		backOfCardConstraints.weighty = 2.0;
		backOfCardConstraints.fill = GridBagConstraints.BOTH;
		m_backOfCardPanel = new CardPanel();
		m_backOfCardPanel.setBackground(Color.YELLOW);
		add(m_backOfCardPanel, backOfCardConstraints);

		JButton editButton = new JButton("Edit card");
		editButton.addActionListener(e -> {
			Card currentCard = Deck.getCardWithFront(Reviewer.getCurrentFront())
		      .get();
			CardEditingManager editingManager = new CardEditingManager(currentCard);
			editingManager.activateCardEditingWindow(currentCard);
		});

		JButton editButton2 = new JButton("Edit card");
		editButton2.addActionListener(e -> {
			Card currentCard = Deck.getCardWithFront(Reviewer.getCurrentFront())
		      .get();
			CardEditingManager editingManager = new CardEditingManager(currentCard);
			editingManager.activateCardEditingWindow(currentCard);
		});

		JPanel buttonPanelForHiddenBack = new JPanel();
		buttonPanelForHiddenBack.setLayout(new FlowLayout());
		JButton showAnswerButton = new JButton("Show Answer");
		showAnswerButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('s'), "show answer");
		showAnswerButton.getActionMap().put("show answer",
		    new ButtonAction(() -> showAnswer()));
		showAnswerButton.addActionListener(e -> showAnswer());
		buttonPanelForHiddenBack.add(showAnswerButton);
		buttonPanelForHiddenBack.add(editButton2);

		JPanel buttonPanelForShownBack = new JPanel();
		JButton rememberedButton = new JButton("Remembered");
		rememberedButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('r'), "remembered");
		rememberedButton.getActionMap().put("remembered",
		    new ButtonAction(() -> remembered(true)));
		rememberedButton.addActionListener(e -> remembered(true));
		buttonPanelForShownBack.add(rememberedButton);
		JButton forgottenButton = new JButton("Forgotten");
		forgottenButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('f'), "forgotten");
		forgottenButton.getActionMap().put("forgotten",
		    new ButtonAction(() -> remembered(false)));

		forgottenButton.addActionListener(e -> remembered(false));
		buttonPanelForShownBack.add(forgottenButton);
		buttonPanelForShownBack.add(editButton);

		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
		buttonPanelConstraints.gridx = 0;
		buttonPanelConstraints.gridy = 4;
		buttonPanelConstraints.gridwidth = 4;
		buttonPanelConstraints.gridheight = 1;
		buttonPanelConstraints.weightx = 4.0;
		buttonPanelConstraints.weighty = 1.0;
		buttonPanelConstraints.fill = GridBagConstraints.BOTH;
		m_buttonPanel = new JPanel();
		m_buttonPanel.setLayout(new CardLayout());
		m_buttonPanel.add(buttonPanelForHiddenBack, HIDDEN_ANSWER);
		m_buttonPanel.add(buttonPanelForShownBack, SHOWN_ANSWER);
		m_buttonPanel.setBackground(Color.GREEN);
		add(m_buttonPanel, buttonPanelConstraints);
		GridBagConstraints sidePanelConstraints = new GridBagConstraints();
		sidePanelConstraints.gridx = 4;
		sidePanelConstraints.gridy = 0;
		sidePanelConstraints.gridwidth = 1;
		sidePanelConstraints.gridheight = 5;
		sidePanelConstraints.fill = GridBagConstraints.BOTH;
		sidePanelConstraints.weightx = 1.0;
		sidePanelConstraints.weighty = 5.0;
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.RED);
		add(sidePanel, sidePanelConstraints);
	}

	private void remembered(boolean wasRemembered) {
		CardLayout cardLayout = (CardLayout) (m_buttonPanel.getLayout());
		cardLayout.show(m_buttonPanel, HIDDEN_ANSWER);
		Reviewer.wasRemembered(wasRemembered);
		m_backOfCardPanel.setText("");
		if (Reviewer.hasNextCard()) {
			m_frontOfCardPanel.setText(Reviewer.getCurrentFront());
			repaint();
		}
	}

	private void showAnswer() {
		CardLayout cardLayout = (CardLayout) (m_buttonPanel.getLayout());
		cardLayout.show(m_buttonPanel, SHOWN_ANSWER);
		m_backOfCardPanel.setText(Reviewer.getCurrentBack());
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		m_frontOfCardPanel.setText(Reviewer.getCurrentFront());
	}

	public void refresh() {
		repaint();

	}

}
