package learning_software;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

public class NewCardFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	JTextPane frontOfNewCard;
	JTextPane backOfNewCard;
	JButton cancelButton;
	JButton okButton;
	
	private void close() {
		this.dispose();
	}
	
	class EnterKeyListener implements KeyListener {
		@Override
	    public void keyPressed(KeyEvent e){
	        if(e.getKeyCode() == KeyEvent.VK_ENTER){
	        	e.consume();
	        	okButton.doClick();
	        }
	    }

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub			
		}
	}
	
	// private constructor: always need to pass Deck
	@SuppressWarnings("unused")
	private NewCardFrame() {
	}
	
	NewCardFrame(Deck currentDeck){
		setLayout(new GridBagLayout());
		
		//upperPanel.setLayout(new BorderLayout());
		frontOfNewCard = new JTextPane();
		TransferFocus.patchWithEnter(frontOfNewCard);
		//frontOfNewCard.addKeyListener(new EnterKeyListener());
		//upperPanel.setTopComponent(frontOfNewCard);
		backOfNewCard = new JTextPane();
		TransferFocus.patch(backOfNewCard);
		//upperPanel.setBottomComponent(backOfNewCard);
		backOfNewCard.addKeyListener(new EnterKeyListener());
		JSplitPane upperPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				frontOfNewCard,backOfNewCard);
		upperPanel.setResizeWeight(0.5);
		GridBagConstraints frontConstraints = new GridBagConstraints();
		frontConstraints.gridx = 0;
		frontConstraints.gridy = 0;
		frontConstraints.weightx = 1;
		frontConstraints.weighty = 1;
		frontConstraints.insets = new Insets(0,0,5,0);
		frontConstraints.fill = GridBagConstraints.BOTH;
		add(upperPanel,frontConstraints);
		cancelButton = new JButton("Cancel");
		cancelButton.setFocusable(false);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				close();
			}
		});
		okButton = new JButton("Ok");
		okButton.setFocusable(false);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				frontOfNewCard.requestFocusInWindow();
				System.out.printf("Front: %s, back %s\n", 
						frontOfNewCard.getText(), backOfNewCard.getText());
				boolean cardAddSuccessful = currentDeck.tryToAddCard(
						frontOfNewCard.getText(),
						backOfNewCard.getText()
						);
				if (cardAddSuccessful) {
					frontOfNewCard.setText("");
					backOfNewCard.setText("");			
				}
			}
		});
		JPanel buttonPane = new JPanel();
		buttonPane.add(cancelButton);
		buttonPane.add(okButton);
		GridBagConstraints buttonPaneConstraints = new GridBagConstraints();
		buttonPaneConstraints.gridx = 0;
		buttonPaneConstraints.gridy = 1;
		buttonPaneConstraints.weightx = 0;
		buttonPaneConstraints.weighty = 0;
		buttonPaneConstraints.insets = new Insets(10,10,10,10);
		add(buttonPane,buttonPaneConstraints);
		

		setSize(400,400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}

/*
 * 	NewCardFrame(){
		setLayout(new GridBagLayout());
		
		
		frontOfNewCard = new JTextPane();
		GridBagConstraints frontConstraints = new GridBagConstraints();
		frontConstraints.gridx = 0;
		frontConstraints.gridy = 0;
		frontConstraints.weightx = 1;
		frontConstraints.weighty = 1;
		frontConstraints.insets = new Insets(0,0,5,0);
		frontConstraints.fill = GridBagConstraints.BOTH;
		add(frontOfNewCard, frontConstraints);
		backOfNewCard = new JTextPane();
		GridBagConstraints backConstraints = new GridBagConstraints();
		backConstraints.gridx = 0;
		backConstraints.gridy = 1;
		backConstraints.weightx = 1;
		backConstraints.weighty = 1;
		backConstraints.insets = new Insets(5,0,0,0);
		backConstraints.fill = GridBagConstraints.BOTH;
		add(backOfNewCard,backConstraints);
		
		
		cancelButton = new JButton("Cancel");
		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.printf("Front: %s, back %s\n", 
						frontOfNewCard.getText(), backOfNewCard.getText());
				Collection.getCurrentDeck().tryToAddCard(
						frontOfNewCard.getText(),
						backOfNewCard.getText()
						);
			}
		});
		JPanel buttonPane = new JPanel();
		buttonPane.add(cancelButton);
		buttonPane.add(okButton);
		GridBagConstraints buttonPaneConstraints = new GridBagConstraints();
		buttonPaneConstraints.gridx = 0;
		buttonPaneConstraints.gridy = 2;
		buttonPaneConstraints.weightx = 0;
		buttonPaneConstraints.weighty = 0;
		buttonPaneConstraints.insets = new Insets(10,10,10,10);
		add(buttonPane,buttonPaneConstraints);
		
		setSize(400,400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}*/
