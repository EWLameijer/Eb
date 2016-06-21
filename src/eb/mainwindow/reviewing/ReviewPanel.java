package eb.mainwindow.reviewing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import eb.data.Card;
import eb.data.DeckManager;
import eb.subwindow.CardEditingManager;
import eb.utilities.ProgrammableAction;

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
	private JPanel m_situationalButtonPanel;
	private JPanel m_fixedButtonPanel;

	private static final String HIDDEN_ANSWER = "HIDDEN_ANSWER";
	private static final String SHOWN_ANSWER = "SHOWN_ANSWER";

	public ReviewPanel() {
		super();
		this.setFocusable(true);
		addComponentListener(EventHandler.create(ComponentListener.class, this,
		    "requestFocusInWindow", null, "componentShown"));

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

		JPanel buttonPanelForHiddenBack = new JPanel();
		buttonPanelForHiddenBack.setLayout(new FlowLayout());
		JButton showAnswerButton = new JButton("Show Answer");
		showAnswerButton.setMnemonic(KeyEvent.VK_S);
		showAnswerButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('s'), "show answer");
		showAnswerButton.getActionMap().put("show answer",
		    new ProgrammableAction(() -> showAnswer()));
		showAnswerButton.addActionListener(e -> showAnswer());
		buttonPanelForHiddenBack.add(showAnswerButton);

		JPanel buttonPanelForShownBack = new JPanel();
		JButton rememberedButton = new JButton("Remembered");
		rememberedButton.setMnemonic(KeyEvent.VK_R);
		rememberedButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('r'), "remembered");
		rememberedButton.getActionMap().put("remembered",
		    new ProgrammableAction(() -> remembered(true)));
		rememberedButton.addActionListener(e -> remembered(true));
		buttonPanelForShownBack.add(rememberedButton);
		JButton forgottenButton = new JButton("Forgotten");
		forgottenButton.setMnemonic(KeyEvent.VK_F);
		forgottenButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('f'), "forgotten");
		forgottenButton.getActionMap().put("forgotten",
		    new ProgrammableAction(() -> remembered(false)));

		forgottenButton.addActionListener(e -> remembered(false));
		buttonPanelForShownBack.add(forgottenButton);

		// for buttons that depend on the situation, like when the back of the card
		// is shown or when it is not yet shown.
		GridBagConstraints situationalButtonPanelConstraints = new GridBagConstraints();
		situationalButtonPanelConstraints.gridx = 0;
		situationalButtonPanelConstraints.gridy = 4;
		situationalButtonPanelConstraints.gridwidth = 3;
		situationalButtonPanelConstraints.gridheight = 1;
		situationalButtonPanelConstraints.weightx = 3.0;
		situationalButtonPanelConstraints.weighty = 1.0;
		situationalButtonPanelConstraints.fill = GridBagConstraints.BOTH;
		m_situationalButtonPanel = new JPanel();
		m_situationalButtonPanel.setLayout(new CardLayout());
		m_situationalButtonPanel.add(buttonPanelForHiddenBack, HIDDEN_ANSWER);
		m_situationalButtonPanel.add(buttonPanelForShownBack, SHOWN_ANSWER);
		m_situationalButtonPanel.setBackground(Color.GREEN);
		add(m_situationalButtonPanel, situationalButtonPanelConstraints);

		JButton editButton = new JButton("Edit card");
		editButton.setMnemonic(KeyEvent.VK_E);
		editButton.getInputMap(WHEN_IN_FOCUSED_WINDOW)
		    .put(KeyStroke.getKeyStroke('e'), "edit");
		editButton.getActionMap().put("edit",
		    new ProgrammableAction(() -> editCard()));
		editButton.addActionListener(e -> editCard());

		// the fixed button panel contains buttons that need to be visible always
		GridBagConstraints fixedButtonPanelConstraints = new GridBagConstraints();
		fixedButtonPanelConstraints.gridx = 3;
		fixedButtonPanelConstraints.gridy = 4;
		fixedButtonPanelConstraints.gridwidth = 1;
		fixedButtonPanelConstraints.gridheight = 1;
		fixedButtonPanelConstraints.weightx = 1.0;
		fixedButtonPanelConstraints.weighty = 1.0;
		fixedButtonPanelConstraints.fill = GridBagConstraints.BOTH;
		m_fixedButtonPanel = new JPanel();
		m_fixedButtonPanel.add(editButton);
		add(m_fixedButtonPanel, fixedButtonPanelConstraints);

		// panel, to be used in future to show successful/unsuccessful cards.
		// for now hidden?
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

	private void editCard() {
		Card currentCard = DeckManager.getCardWithFront(Reviewer.getCurrentFront()).get();
		new CardEditingManager(currentCard);
	}

	private void remembered(boolean wasRemembered) {
		showPanel(HIDDEN_ANSWER);
		Reviewer.wasRemembered(wasRemembered);
		repaint();
	}

	private void showAnswer() {
		showPanel(SHOWN_ANSWER);
		Reviewer.showAnswer();
		repaint();
	}

	private void showPanel(String panelName) {
		CardLayout cardLayout = (CardLayout) (m_situationalButtonPanel.getLayout());
		cardLayout.show(m_situationalButtonPanel, panelName);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		m_frontOfCardPanel.setText(Reviewer.getCurrentFront());
	}

	public void refresh() {
		repaint();
	}

	public void updatePanels(String frontText, String backText,
	    boolean showAnswer) {
		m_frontOfCardPanel.setText(frontText);
		m_backOfCardPanel.setText(backText);
		showPanel(showAnswer ? SHOWN_ANSWER : HIDDEN_ANSWER);

	}

}
