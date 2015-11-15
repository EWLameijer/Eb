package learning_software;

import java.util.ArrayList;
import java.util.List;

interface CardChangeListener {
	public void respondToChangedCard();
}

interface DeckChangeListener {
	public void respondToChangedDeck();
}

interface DeckSwapListener {
	public void respondToSwappedDeck();
}


class ChangePropagator {
	// card changes (change the contents of a card)
	private static List<CardChangeListener> m_cardChangeListeners = 
			new ArrayList<>();
	// deck changes (insert cards, delete cards)
	private static List<DeckChangeListener> m_deckChangeListeners = 
			new ArrayList<>();
	// deck swaps
	private static List<DeckSwapListener> m_deckSwapListeners =
			new ArrayList<>();
	// collection swaps
	
	
	private static Deck m_currentDeck;
	private static Collection m_currentCollection;
	
	public static Deck getCurrentDeck() {
		return m_currentDeck;
	}
	
	public static void setCurrentDeck(Deck newDeck) {
		m_currentDeck = newDeck;
	}
	
	public static Collection getCurrentCollection() {
		return m_currentCollection;
	}
	
	public static void setCurrentCollection(Collection newCollection) {
		m_currentCollection = newCollection;
	}	
	
	public static void registerDeckChangeListener(
			DeckChangeListener deckChangeListener) {
		m_deckChangeListeners.add(deckChangeListener);
	}
	
	public static void propagateCardChange() {
		for (CardChangeListener cardChangeListener: m_cardChangeListeners) {
			cardChangeListener.respondToChangedCard();
		}
	}
	
	public static void propagateDeckChange() {
		for (DeckChangeListener deckChangeListener: m_deckChangeListeners) {
			deckChangeListener.respondToChangedDeck();
		}
	}
}
