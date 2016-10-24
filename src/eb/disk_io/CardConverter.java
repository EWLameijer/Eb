package eb.disk_io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

import eb.data.Card;
import eb.data.DeckManager;
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
	 * The reviewHistoryToLine method transforms the review data of a card to a
	 * line, for purposes of saving it to a format that is easier to restore from
	 * than Java's standard 'blobs'. (Note that I could also use GoogleProto here,
	 * but my Java skills are not yet sufficient to handle that level of added
	 * complexity)
	 * 
	 * @param card
	 *          the card of which the review history must be saved.
	 * @return a newline-terminated String containing the front and review history
	 *         of the card.
	 */
	public static String reviewHistoryToLine(Card card) {
		Utilities.require(card != null,
		    "CardConverter.reviewHistoryToLine() error: "
		        + "the card to be converted cannot be null.");
		return card.getFront() + SEPARATOR + card.getHistory() + Utilities.EOL;
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

	public static void extractCardsFromArchiveFile(File selectedFile) {
		try {
			List<String> lines = Files.readAllLines(selectedFile.toPath(),
			    Charset.forName("UTF-8"));

			// find out which line contains the first card (skip version data and
			// such for now)
			int currentLine = 0;
			while ((currentLine < lines.size())
			    && !lines.get(currentLine).equals(SEPARATOR)) {
				currentLine++;
			}
			// now skip the separator line
			currentLine++;
			// now read in the cards
			for (; currentLine < lines.size(); currentLine++) {
				Card newCard = CardConverter.lineToCard(lines.get(currentLine));
				DeckManager.getCurrentDeck().getCards().addCard(newCard);
			}
		} catch (IOException e) {
			Logger.getGlobal().info(e + "");
			e.printStackTrace();
		}
	}

}
