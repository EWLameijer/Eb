package eb.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.logging.Logger;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

import eb.disk_io.CardConverter;
import eb.mainwindow.reviewing.Reviewer;
import eb.subwindow.StudyOptions;
import eb.utilities.Utilities;

/**
 * Logically, a Deck is a collection of cards (usually all belonging to a
 * specific subject, like Spanish) that can be studied by the user. Practically,
 * as there are all kinds of ugly housekeeping aspects of any application that
 * have nothing to do with the contents of a deck, the "Deck" class in Eb is
 * actually more of a DeckManager or DeckHandler, which encapsulates the pure,
 * data-oriented deck (the LogicalDeck) into an envelope that can successfully
 * interact with the GUI, keeping its capabilities even when a deck is swapped.
 * Basically, one could regard the situation as if "Deck" is the interface that
 * the rest of the program deals with (Deck.getCardCount(), Deck.addCard(card)
 * etc.) However, the contents of the Deck is a different story. Intuitively,
 * there could be a "Chinese" deck, a "Java" deck etc. Those all are 'bare'
 * data, which should not know anything about the UI, and should be changed
 * without resetting all UI links to the new deck. So one should probably split
 * the Deck as the global instance of the one and only deck in Eb from the
 * 'deck'(='LogicalDeck'), that manages the contents of a normal SRS deck.
 *
 * @author Eric-Wubbo Lameijer
 */
public class DeckManager {

	// The "singleton" pointer to the logical deck managed by the Deck.
	@Nullable
	private static Deck m_deck;

	// The name of the default deck
	private static final String DEFAULT_DECKNAME = "default";

	private static String c_nameOfLastReviewedDeck = "";

	/**
	 * Private constructor: should not be called as this is more of a static
	 * utility class, a wrapper around the LogicalDeck.
	 */
	private DeckManager() {
	}

	/**
	 * Returns whether a deck / the contents of a deck have been loaded.
	 *
	 * @return whether a deck has been loaded into this "deck-container"
	 */
	@EnsuresNonNullIf(expression = { "m_contents" }, result = true)
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

	@EnsuresNonNull({ "m_contents" })
	private static void ensureDeckExists() {
		// preconditions: none. After all, this is itself a kind of
		// precondition-checking method.

		if (!deckHasBeenLoaded()) {
			// No deck has been loaded yet - try to load the default deck,
			// or else create it.
			if (canLoadDeck(getLastDeck())) {
				loadDeck(getLastDeck());
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

		// next line is necessary to satisfy the nullness checker; after all,
		// if m_contents is really null, we would have exited the program by now.
		assert m_deck != null : "@AssumeAssertion(nullness)";
	}

	private static String getLastDeck() {
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

		final File deckFile = Deck.getDeckFileHandle(name);
		save();
		try (ObjectInputStream objInStream = new ObjectInputStream(
		    new FileInputStream(deckFile))) {
			Deck loadedDeck = (Deck) objInStream.readObject();
			if (loadedDeck != null) {
				m_deck = loadedDeck;
				m_deck.fixNewFields();
				swapDeck();
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
		// anyway, first save the old deck to be safe.
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

		// code
		// if there is already a deck loaded/constructed previously, save it to disk
		// before creating the new deck
		if (deckHasBeenLoaded()) {
			save();
		}
		m_deck = new Deck(name);
		save();

		// postconditions: the deck should exist (deck.save handles any errors
		// occurring during saving the deck).
		Utilities.require(deckHasBeenLoaded(), "Deck.createDeckWithName() error: "
		    + "problem creating and/or writing the new deck.");

		// The deck has been changed. So ensure depending GUI-elements know that.
		swapDeck();

	}

	/**
	 * To do after the deck is swapped.
	 */
	private static void swapDeck() {
		// phase 1: update the data structure/model.
		Reviewer.start(null);

		// phase 2: update the view(s)
		// BlackBoard.post(new Update(UpdateType.DECK_SWAPPED));
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
	 * Returns the StudyOptions object of the current deck.
	 * 
	 * @return
	 */
	public static StudyOptions getStudyOptions() {
		// preconditions: outside ensuring that there is a deck, preconditions
		// should be handled by the relevant method in the logical deck
		ensureDeckExists();
		return m_deck.getStudyOptions();
		// postconditions: handled by callee.
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

	public static Duration getInitialInterval() {
		ensureDeckExists();
		return m_deck.getInitialInterval();
	}

	public static boolean exists(String deckName) {
		File deckFile = Deck.getDeckFileHandle(deckName);
		return deckFile.exists();
	}

	public static String getName() {
		ensureDeckExists();
		return m_deck.getName();
	}

	public static Duration getForgottenCardInterval() {
		ensureDeckExists();
		return m_deck.getStudyOptions().getForgottenCardInterval().asDuration();
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
		m_deck.setArchivingDirectory(directory);
	}

	public static String getArchivingDirectoryName() {
		ensureDeckExists();
		return m_deck.getArchivingDirectoryName();
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
