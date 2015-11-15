package learning_software;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

public class BrowseTab extends JPanel implements DeckChangeListener {
	private static final long serialVersionUID = 1L;
	private JTextPane frontOfCurrentCard;
	private JTextPane backOfCurrentCard;
	private JButton m_previousCardButton;
	private JButton m_nextCardButton;
	
	
	private void showCard(Card card) {
		frontOfCurrentCard.setText(card.getFront());
		backOfCurrentCard.setText(card.getBack());
	}
	
	private void updateButtonAvailability() {
		//System.out.println(m_currentDeck);
		if (ChangePropagator.getCurrentDeck() == null ) {
			m_nextCardButton.setEnabled(false);
			m_previousCardButton.setEnabled(false);
		} else {
			m_nextCardButton.setEnabled( ChangePropagator.getCurrentDeck().hasNextCard());
			m_previousCardButton.setEnabled(ChangePropagator.getCurrentDeck().hasPreviousCard());
		}
	}
	
	private void updateCard() {
		Card currentCard = ChangePropagator.getCurrentDeck().getCurrentCard();
		frontOfCurrentCard.setText( 
				currentCard==null ? "Front" : currentCard.getFront());
		backOfCurrentCard.setText(currentCard==null ? 
				"Back" : currentCard.getBack());
	}
	
	
	
	BrowseTab() {
		super();
		this.setName("Browse");
		this.setLayout(new BorderLayout());
		m_nextCardButton= new JButton(">");
		m_previousCardButton = new JButton("<");
		updateButtonAvailability();
		m_nextCardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
				showCard( ChangePropagator.getCurrentDeck().getNextCard());
				updateButtonAvailability();
            }
		});
		m_previousCardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
				showCard( ChangePropagator.getCurrentDeck().getPreviousCard());
				updateButtonAvailability();
                System.out.println("You clicked the button");
            }	
		});
		frontOfCurrentCard = new JTextPane();
		
		frontOfCurrentCard.setBorder( 
				BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		backOfCurrentCard = new JTextPane();

		frontOfCurrentCard.setBorder( 
				BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		Container innerBox = Box.createVerticalBox();
		innerBox.add(frontOfCurrentCard);
		innerBox.add(Box.createHorizontalStrut(10));
		innerBox.add(backOfCurrentCard);
		/*
		Container outerBox = Box.createHorizontalBox();
		outerBox.add(previousCard);
		outerBox.add(innerBox);
		outerBox.add(nextCard);
		add(outerBox);*/
		add(m_previousCardButton,BorderLayout.WEST);
		add(m_nextCardButton,BorderLayout.EAST);
		add(innerBox,BorderLayout.CENTER);
		
	}

	public void respondToChangedDeck() {
		// TODO Auto-generated method stub
		updateButtonAvailability();
		updateCard();
	}
	
}
