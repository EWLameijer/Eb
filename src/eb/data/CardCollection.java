package eb.data;

import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import eb.disk_io.CardConverter;

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
	 * Creates a new CardCollection
	 */
	public CardCollection() {
		m_cards = new LinkedList<>();
	}

	/**
	 * Returns the size of the CardCollection (=the number of cards in the
	 * CardCollection)
	 * 
	 * @return the number of cards in the CardCollection
	 */
	public int getSize() {
		return m_cards.size();
	}

	/**
	 * Write the cards of this collection, in alphabetical order (well, the order
	 * imposed by the default character encoding) to a given writer.
	 * 
	 * @param writer
	 *          the writer to which the cards have to be written.
	 */
	public void writeCards(Writer writer) {
		m_cards.stream()
		    .sorted(
		        (first, second) -> first.getFront().compareTo(second.getFront()))
		    .forEach(e -> CardConverter.writeLine(writer, e));

	}

}