package eb.disk_io;

import eb.data.Card;
import eb.utilities.Utilities;

/**
 * The CardConverter class wraps converting a card to a line for output (or disk
 * IO) and converting a line to the appropriate card.
 * 
 * @author Eric-Wubbo Lameijer
 *
 */
public class CardConverter {

	// note that the best separator in this case consists of characters that
	// cannot be part of the 'regular' text of a card; tab characters are
	// perfect for that, for when the user presses TAB, instead of a tab character
	// being added, the cursor just jumps to the other side of the card .
	private final static String separatorRegex = "\\t\\t";

	/**
	 * Uses a line to create a new card (or rather, a new Card object).
	 * 
	 * @param line
	 *          the line used to create the Card object. Needs to contain a
	 *          (non-empty) string to fill the front of the card, followed by the
	 *          separator string, followed by the string that makes up the back of
	 *          the card.
	 * @return the card
	 */
	public static Card lineToCard(String line) {

		// preconditions: the input line cannot be null
		Utilities.require(line != null, "CardConverter.lineToCard() error: "
		    + "the string to be converted cannot be null");
		String[] strings = line.split(separatorRegex);
		if (strings.length != 2 || strings[0].isEmpty()) {
			Utilities.require(false, "CardConverter.lineToCard() error: "
			    + "the input string is invalid.");
		}
		return new Card(strings[0], strings[1]);
	}

}
