package eb.data;

import java.util.LinkedList;
import java.util.List;

/**
 * CardCollection contains a collection of cards, which forms the content of the
 * deck (so what is stored, what should be learned, not how it should be learned
 * or the file system logistics).
 * 
 * @author Eric-Wubbo Lameijer
 */
public class CardCollection {
	private List<Card> m_cards;

	/**
	 * 
	 */
	public CardCollection() {
		m_cards = new LinkedList<>();
	}
}