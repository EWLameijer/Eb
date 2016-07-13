package eb.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.logging.Logger;

import eb.Eb;
import eb.subwindow.ArchivingSettings;
import eb.subwindow.StudyOptions;
import eb.utilities.Utilities;

/**
 * Contains the properties belonging to the 'pure' deck itself, like its name
 * and contents [not the mess of dealing with the GUI, which is the
 * responsibility of the Deck class]
 *
 * @author Eric-Wubbo Lameijer
 */
public class Deck implements Serializable {

	// Automatically generated ID for serialization.
	private static final long serialVersionUID = 8271837223354295531L;

	// The file extension of a deck.
	private static final String DECKFILE_EXTENSION = ".deck";

	// when writing the deck to a text file.
	private static final String HEADER_BODY_SEPARATOR = "\t\t";

	// The name of the deck (like "Spanish"). Does not include the ".deck"
	// extension.
	private final String m_name;

	// The location where the archive file will be stored
	private ArchivingSettings m_archivingSettings;

	// Note that while intuitively a deck is just a collection of cards, in Eb a
	// deck also has settings, which are per convenience also part of the deck (or
	// deck file)
	private CardCollection m_cardCollection;

	// The study options of this deck (interval increase between reviews etc.)
	private StudyOptions m_studyOptions;

	/**
	 * Constructs a deck with name "name". Note that by defining this constructor,
	 * it is not needed to define a 'forbidden' default constructor anymore.
	 *
	 * @param name
	 *          the name of the deck to be created
	 */
	public Deck(String name) {
		// preconditions
		Utilities.require(Utilities.isStringValidIdentifier(name),
		    "LogicalDeck constructor error: deck has a bad name.");

		// code
		m_name = name;
		m_cardCollection = new CardCollection();
		m_studyOptions = StudyOptions.getDefault();
		m_archivingSettings = ArchivingSettings.getDefault();

		// postconditions: none. The deck should have been constructed,
		// everything should work
	}

	/**
	 * Saves the deck to a text file (helpful for recovery), though it deletes all
	 * repetition data...
	 */
	public void saveDeckToTextfile() {
		// Phase 1: get proper filename for deck
		LocalDateTime now = LocalDateTime.now();
		String nameOfArchivingDirectory = m_archivingSettings.getDirectoryName();
		String textFileDirectory = nameOfArchivingDirectory.isEmpty() ? ""
		    : nameOfArchivingDirectory + File.separator;
		String twoDigitFormat = "%02d"; // format numbers as 01, 02...99

		String textFileName = textFileDirectory + getName() + "_"
		    + String.format(twoDigitFormat, now.get(ChronoField.DAY_OF_MONTH))
		    + String.format(twoDigitFormat, now.get(ChronoField.MONTH_OF_YEAR))
		    + String.format(twoDigitFormat, now.get(ChronoField.YEAR) % 100) + "_"
		    + String.format(twoDigitFormat, now.get(ChronoField.HOUR_OF_DAY))
		    + String.format(twoDigitFormat, now.get(ChronoField.MINUTE_OF_HOUR))
		    + ".txt";

		// Phase 2: write the deck itself
		try (BufferedWriter outputFile = new BufferedWriter(
		    new OutputStreamWriter(new FileOutputStream(textFileName), "UTF-8"));) {
			// Phase 2a: write the header.
			outputFile.write("Eb version " + Eb.VERSION_STRING + Utilities.EOL);
			outputFile.write(
			    "Number of cards is: " + m_cardCollection.getSize() + Utilities.EOL);
			outputFile.write(HEADER_BODY_SEPARATOR + Utilities.EOL);

			// Phase 2b: write the card data
			m_cardCollection.writeCards(outputFile);

		} catch (Exception e) {
			Logger.getGlobal().info(e + "");
			Utilities.require(false,
			    "Deck.saveDeckToTextfile() error: cannot save text "
			        + "copy of deck.");
		}
	}

	/**
	 * Returns the File object representing a deck with name "deckName".
	 *
	 * @param deckName
	 *          the name of the deck
	 * @return the File object belonging to this deck.
	 */
	static File getDeckFileHandle(String deckName) { // package private access
		// preconditions: m_cards is a valid identifier
		Utilities.require(Utilities.isStringValidIdentifier(deckName),
		    "LogicalDeck.getDeckFileHandle() error: deck name is invalid.");

		// code
		final String deckFileName = deckName + DECKFILE_EXTENSION;
		final File deckFile = new File(deckFileName);

		// postconditions: deckFile exists!
		Utilities.require(deckFile != null, "LogicalDeck.getDeckFileHandle() "
		    + "error: problem creating file handle for deck.");

		return deckFile;
	}

	/**
	 * Returns the handle (File object) to the file in which this deck is stored.
	 *
	 * @return the handle (File object) to the file which stores this deck
	 */
	File getFileHandle() { // package private access
		// preconditions: none (the deck should exist, of course, but since this
		// function can only be called if the deck already exists...

		// code
		final File deckFileHandle = Deck.getDeckFileHandle(m_name);

		// postconditions: the fileHandle should not be null, after all, that
		// would mean an evil error has occurred
		Utilities.require(deckFileHandle != null, "LogicalDeck.getFileHandle() "
		    + " error: problem creating file handle for deck.");

		return deckFileHandle;
	}

	/**
	 * Returns the study settings of this deck.
	 *
	 * @return the study settings of this deck.
	 */
	public StudyOptions getStudyOptions() {
		// preconditions and postconditions: none. After all, m_studyOptions has
		// already been initialized in LogicalDeck's constructor.
		return m_studyOptions;
	}

	/**
	 * Sets the study options to a new value.
	 * 
	 * @param studyOptions
	 *          the new study options
	 */
	public void setStudyOptions(StudyOptions studyOptions) {
		m_studyOptions = studyOptions;
	}

	/**
	 * Returns the name of the deck (like "Chinese")
	 * 
	 * @return the name of the deck, for example "Chinese"
	 */
	String getName() { // package private method
		return m_name;
	}

	/**
	 * Helps avoid problems when deserializing after a code update.
	 */
	void fixNewFields() { // package-private (used by DeckManager)
		if (m_archivingSettings == null) {
			m_archivingSettings = ArchivingSettings.getDefault();
		}
	}

	/**
	 * Returns the archiving settings of this deck.
	 * 
	 * @return the archiving settings of this deck
	 */
	ArchivingSettings getArchivingSettings() { // package private access.
		if (m_archivingSettings == null) {
			m_archivingSettings = new ArchivingSettings();
		}
		return m_archivingSettings;
	}

	/**
	 * Returns the collection of cards that this deck possesses.
	 * 
	 * @return the CardCollection associated with this deck.
	 */
	public CardCollection getCards() {
		return m_cardCollection;
	}

}
