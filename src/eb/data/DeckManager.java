package eb.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.logging.Logger;

import eb.disk_io.CardConverter;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.subwindow.StudyOptions;
import eb.utilities.Utilities;

/**
 * The DeckManager class concerns itself with all the housekeeping (such as
 * interacting with the GUI) that the deck itself (which only concerns itself
 * with the logical content) should not need to bother about.
 *
 * @author Eric-Wubbo Lameijer
 */
public class DeckManager {

	// The deck managed by the DeckManager.
	private static Deck m_deck;

	// the name of the deck that has been reviewed previously
	// TODO: basically, is only important when starting up Eb; why does this need
	// to be a field?
	private static String c_nameOfLastReviewedDeck = "";

	// The name of the default deck
	private static final String DEFAULT_DECKNAME = "default";

	/**
	 * Private constructor: should not be called as DeckManager is basically a
	 * static utility class, a wrapper around the Deck itself.
	 */
	private DeckManager() {
		// TODO: in principle, DeckManager could be initialized to contain the name
		// of the lastReviewedDeck
		Utilities.require(false, "DeckManager constructor error: one should "
		    + "not try to construct an instance of a static utility class.");
	}

	/**
	 * Returns whether a deck / the contents of a deck have been loaded.
	 *
	 * @return whether a deck has been loaded into this "deck-container"
	 */
	private static boolean deckHasBeenLoaded() {
		// preconditions: none - this method is checking a condition
		// postconditions: none: a normal boolean is returned.
		return m_deck != null;
	}

	/**
	 * Returns whether the deck has been initialized, even if it is only with the
	 * default deck.
	 *
	 * @return whether the deck has been initialized, meaning it can be used for
	 *         things like counting the number of cards in it.
	 */
	private static void ensureDeckExists() {
		// preconditions: none. After all, this is itself a kind of
		// precondition-checking method.

		if (!deckHasBeenLoaded()) {
			// No deck has been loaded yet - try to load the default deck,
			// or else create it.
			if (canLoadDeck(getNameOfLastDeck())) {
				loadDeck(getNameOfLastDeck());
			} else {
				// If loading the deck failed, try to create it.
				// Note that createDeckWithName cannot return null; it will exit
				// with an error message instead.
				createDeckWithName(DEFAULT_DECKNAME);
			}
		}

		// postconditions: m_currentDeck cannot be null, but that is ensured
		// by the Deck.createDeckWithName call, which exits with an error if
		// a deck cannot be created.
		Utilities.require(deckHasBeenLoaded(),
		    "Deck.ensureDeckExists() error: there is no valid deck.");
	}

	/**
	 * Returns the name of the deck studied previously (ideal when starting a new
	 * session of Eb).
	 * 
	 * @return the name of the last deck studied
	 */
	private static String getNameOfLastDeck() {
		if (c_nameOfLastReviewedDeck.isEmpty()) {
			return DEFAULT_DECKNAME;
		} else {
			return c_nameOfLastReviewedDeck;
		}
	}

	/**
	 * Loads a deck from file.
	 *
	 * @param name
	 *          the name of the deck.
	 * @return a boolean indicating whether the requested deck was successfully
	 *         loaded
	 */
	public static void loadDeck(String name) {

		// checking preconditions
		Utilities.require(canLoadDeck(name),
		    "Deck.loadDeck() error: deck cannot be loaded. "
		        + "Was canLoadDeck called?");

		save();
		final File deckFile = Deck.getDeckFileHandle(name);
		try (ObjectInputStream objInStream = new ObjectInputStream(
		    new FileInputStream(deckFile))) {
			Deck loadedDeck = (Deck) objInStream.readObject();
			if (loadedDeck != null) {
				m_deck = loadedDeck;
				m_deck.fixNewFields();
				reportDeckChangeEvent();
			} else {
				Utilities.require(false,
				    "Deck.loadDeck() error: the requested deck " + "cannot be loaded.");
			}
		} catch (final Exception e) {
			// something goes wrong with deserializing the deck; so
			// you also can't read the file
			Logger.getGlobal()
			    .info(e + "Deck.loadDeck() error: could not load deck from file");
		}
		// postconditions: none
	}

	/**
	 * Returns whether a deck with this name can be loaded (it exists and is of
	 * the proper file format)
	 * 
	 * @param deckName
	 *          the name of the deck can be loaded.
	 * @return true if the deck can be loaded from disk, false if it cannot.
	 */
	public static boolean canLoadDeck(String deckName) {
		// checking preconditions
		if (!Utilities.isStringValidIdentifier(deckName)) {
			return false;
		}

		final File deckFile = Deck.getDeckFileHandle(deckName);

		// case A: the file does not exist
		if (!deckFile.isFile()) {
			return false;
		}

		// so the file must exist. But does it contain a valid deck?
		try (ObjectInputStream objInStream = new ObjectInputStream(
		    new FileInputStream(deckFile))) {
			Deck loadedDeck = (Deck) objInStream.readObject();
			return loadedDeck != null;
		} catch (final Exception e) {
			// something goes wrong with deserializing the deck; so
			// you also can't read the file
			Logger.getGlobal()
			    .info(e + "Deck.loadDeck() error: could not load deck from file");
			return false;
		}
		// postconditions: none
	}

	/**
	 * Creates a deck with name "name".
	 *
	 * @param name
	 *          the name of the deck to be created
	 */
	public static void createDeckWithName(String name) {

		// checking preconditions
		Utilities.require(Utilities.isStringValidIdentifier(name),
		    "Deck.createDeckWithName() error: name cannot be null, and has to "
		        + "contain non-whitespace characters.");

		// Save the current deck to disk before creating the new deck
		save();

		m_deck = new Deck(name);

		// postconditions: the deck should exist (deck.save handles any errors
		// occurring during saving the deck).
		Utilities.require(deckHasBeenLoaded(), "Deck.createDeckWithName() error: "
		    + "problem creating and/or writing the new deck.");

		// The deck has been changed. So ensure depending GUI-elements know that.
		reportDeckChangeEvent();

	}

	/**
	 * After the deck has been swapped, ensure anything not handled by the
	 * GUI-element activating the deck swap itself is performed.
	 */
	private static void reportDeckChangeEvent() {
		// A new review session is needed.
		BlackBoard.post(new Update(UpdateType.DECK_CHANGED));
	}

	/**
	 * Saves the deck to disk.
	 */
	public static void save() {
		// Preconditions: none (well, if the deck does not exist, you don't have to
		// do anything).

		// First: check if there is a deck to be saved in the first place.
		if (!deckHasBeenLoaded()) {
			// If there is no deck, there is no necessity to save it...
			return;
		}
		ensureDeckExists();
		try (ObjectOutputStream objOutStream = new ObjectOutputStream(
		    new FileOutputStream(m_deck.getFileHandle()))) {
			objOutStream.writeObject(m_deck);
			m_deck.saveDeckToTextfile();
		} catch (final Exception e) {
			// Something goes wrong with serializing the deck; so
			// you cannot create the file.
			Logger.getGlobal().info(e + "");
			Utilities.require(false,
			    "Deck.save() error: cannot write the new deck to disk.");
		}

		// postconditions: the save has to be a success! Which it is if no
		// exception occurred - in other words, if you get here.
	}

	/**
	 * Sets the study options of the current deck to a new value.
	 * 
	 * @param studyOptions
	 *          the new study options
	 */
	public static void setStudyOptions(StudyOptions studyOptions) {
		// preconditions: outside ensuring that there is a deck, preconditions
		// should be handled by the relevant method in the logical deck
		ensureDeckExists();
		m_deck.setStudyOptions(studyOptions);
		// postconditions: handled by callee.
	}

	public static Duration getRememberedCardInterval() {
		ensureDeckExists();
		return m_deck.getStudyOptions().getRememberedCardInterval().asDuration();
	}

	public static double getLengtheningFactor() {
		ensureDeckExists();
		return m_deck.getStudyOptions().getLengtheningFactor();
	}

	public static void setNameOfLastReviewedDeck(String nameOfLastReviewedDeck) {
		c_nameOfLastReviewedDeck = nameOfLastReviewedDeck;
	}

	public static Deck getContents() {
		ensureDeckExists();
		return m_deck;
	}

	public static void setArchivingDirectory(File directory) {
		ensureDeckExists();
		m_deck.getArchivingSettings().setDirectory(directory);
	}

	public static String getArchivingDirectoryName() {
		ensureDeckExists();
		return m_deck.getArchivingSettings().getDirectoryName();
	}

	/**
	 * Creates a deck based on an archive file.
	 * 
	 * @param selectedFile
	 *          the archive file (text file) to base the new deck on.
	 */
	public static void createDeckFromArchive(File selectedFile) {
		String fileName = selectedFile.getName();
		int sizeOfFileName = fileName.length();
		int sizeOfEnd = "_DDMMYY_HHMM.txt".length();
		String deckName = fileName.substring(0, sizeOfFileName - sizeOfEnd);
		createDeckWithName(deckName);
		ensureDeckExists();

		CardConverter.extractCardsFromArchiveFile(selectedFile);
	}

	/**
	 * Returns the current deck (loads the default deck or creates a deck if none
	 * exists yet)
	 * 
	 * @return the current deck.
	 */
	public static Deck getCurrentDeck() {
		ensureDeckExists();
		return m_deck;
	}

}
