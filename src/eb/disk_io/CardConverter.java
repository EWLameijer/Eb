package eb.disk_io;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

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
	private static final String SEPARATOR_REGEX = "\\t\\t";

	private static final String SEPARATOR = "\t\t";

	private CardConverter() {
		Utilities.require(false, "CardConverter constructor error: CardConverter "
		    + "is a static utility class and objects of it should not be created.");
	}

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
		String[] strings = line.split(SEPARATOR_REGEX);
		if (strings.length != 2 || strings[0].isEmpty()) {
			Utilities.require(false, "CardConverter.lineToCard() error: "
			    + "the input string is invalid.");
		}
		return new Card(strings[0], strings[1]);
	}

	/**
	 * The cardToLine method transforms the data of a card to a line, for purposes
	 * of saving it to a human-readable file (that is also easy to restore data
	 * from).
	 * 
	 * @param card
	 *          the card to be written into a line of text.
	 * @return a newline-terminated String containing the front and back of the
	 *         card, as text.
	 */
	public static String cardToLine(Card card) {
		// preconditions: the input card cannot be null
		Utilities.require(card != null, "CardConverter.cardToLine() error: "
		    + "the card to be converted cannot be null.");
		return card.getFront() + SEPARATOR + card.getBack() + Utilities.EOL;
	}

	/**
	 * Writes a single card, converted to a line, to the given writer.
	 * 
	 * @param writer
	 *          the writer to write the card to
	 * @param card
	 *          the card to write to the writer
	 */
	public static void writeLine(Writer writer, Card card) {
		try {
			writer.write(cardToLine(card));
		} catch (IOException e) {
			Logger.getGlobal().info(e + "");
		}
	}

}
