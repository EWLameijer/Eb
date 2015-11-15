package learning_software;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import java.util.Observable;

class Deck implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	transient private String m_deckName;
	transient private String m_fileName;
	private List<Card> m_cards;
	transient private int m_currentCardIndex;
	private Date m_dateOfLastModification;
	
	public Deck(String deckName, String fileName) {
		m_fileName = fileName;
		m_deckName = deckName;
		m_cards = new LinkedList<Card>();
		try (FileReader deckReader = new FileReader( fileName )) {
			DateFormat dateFormat = DateFormat.getDateTimeInstance(
					DateFormat.FULL, DateFormat.FULL);
			BufferedReader deckBReader = new BufferedReader(deckReader);
			String timeLastModified = deckBReader.readLine();
			m_dateOfLastModification = Utilities.noStringDataAvailable(timeLastModified) ?
					new Date(0) : dateFormat.parse(timeLastModified);	
			deckReader.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot find the deck " + deckName + ".",
					"Cannot find deck",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"Error reading deck " + deckName + ".",
					"Cannot read deck",
					JOptionPane.ERROR_MESSAGE);	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, 
					"Error reading deck " + deckName + ".",
					"Deck file format incorrect",
					JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public String getName() {
		return m_deckName;
	}
	
	public String getFileName() {
		return m_fileName;
	}
	
	public Date getDateOfLastModification() {
		return m_dateOfLastModification;
	}
	
	public void save() {
		try (FileOutputStream fileOut = new FileOutputStream(m_fileName);
		     ObjectOutputStream out = new ObjectOutputStream(fileOut);) {
		 
			out.writeObject(this);
		    out.close();
		    fileOut.close();
		/*try (PrintWriter deckWriter = new PrintWriter(m_fileName)) {
			for (Card card : m_cards) {
				deckWriter.println(card.getFront() + " " + card.getBack());
			}*/
		} catch (Exception e) {}
	}
	
	// checks whether a card exists in the deck. Note that a card is
	// identified uniquely by its name.
	private boolean cardExists(String front) {
		for (Card currentCard: m_cards) {
			if (currentCard.hasFront(front)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean cardHasEmptyFront(String front) {
		if (front == null || front == "") {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean verifyCard(String front) {
		// note: for a card to be valid, only its front has to be checked:
		// it should not be empty, nor should it be a copy of an existing card.
		
		if (cardHasEmptyFront(front) || cardExists( front )) {
			return false;
		}
		return true;
	}
	
	private void addCard(String front, String back) {
		Card newCard = new Card(front, back);
		int positionToInsert = 
				m_cards.isEmpty() ? 0 : m_currentCardIndex+1;
		m_cards.add(positionToInsert, newCard);
		m_currentCardIndex = positionToInsert;
		System.out.println(positionToInsert);
		m_dateOfLastModification = new Date(); // now
	}

	public boolean tryToAddCard(String front, String back) {
		if (verifyCard(front)) {
			addCard(front, back);
			ChangePropagator.propagateDeckChange();
			return true;
		} else {
			if (cardHasEmptyFront(front)) {
				JOptionPane.showMessageDialog(null, 
					"The front of the card cannot be empty!",
					"Front of card cannot be empty",
					JOptionPane.ERROR_MESSAGE);
			} else if (cardExists(front)) {
				JOptionPane.showMessageDialog(null, 
						"A Card with this front already exists! Please either" +
				        " delete the original card or change this card's " +
						"front.",
						"Front of card already exists",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}
	
	public boolean hasNextCard() {
		// list of size 1 has last element 0, so cardIndex should be 1 or more
		// less than the size.
		return m_currentCardIndex < ( m_cards.size() - 1); 
	}
	
	public Card getNextCard() {
		if (!hasNextCard()) {
			return null;
		} else {
			m_currentCardIndex++;
			return getCurrentCard();
		}
	}
	
	public boolean hasPreviousCard() {
		// list of size 1 has last element 0, so cardIndex should be 1 or more
		// less than the size.
		return m_currentCardIndex > 0; 
	}
	
	public Card getPreviousCard() {
		if (!hasPreviousCard()) {
			return null;
		} else {
			m_currentCardIndex--;
			return getCurrentCard();
		}
	}
	
	public Card getCurrentCard() {
		if (m_cards == null || m_cards.size()==0) {
			return null;
		}
		else {
			return m_cards.get(m_currentCardIndex);
		}
	}



}
