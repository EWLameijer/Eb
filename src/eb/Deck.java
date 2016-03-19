package eb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

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
public class Deck {

	// The "singleton" pointer to the logical deck managed by the Deck.
	@Nullable
	private static LogicalDeck m_contents;

	// The name of the default deck
	private static final String DEFAULT_DECKNAME = "default";

	// The objects which should be notified of any change in the deck
	private static Set<DeckChangeListener> m_deckChangeListeners = new HashSet<>();

	/**
	 * Private constructor: should not be called as this is more of a static
	 * utility class, a wrapper around the LogicalDeck.
	 */
	private Deck() {
	}

	/**
	 * Returns the number of cards in this deck.
	 * 
	 * @return the number of cards in the currently active deck
	 */
	public static int getCardCount() {
		// preconditions: the deck should have been initialized
		ensureDeckExists();

		// postconditions: none (I assume getCardCount works properly)
		return m_contents.getCardCount();
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
		return m_contents != null;
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
			boolean deckLoadedSuccessfully = loadDeck(DEFAULT_DECKNAME);

			// If loading the deck failed, try to create it.
			// Note that createDeckWithName cannot return null; it will exit
			// with an error message instead.
			if (!deckLoadedSuccessfully) {
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
		assert m_contents != null : "@AssumeAssertion(nullness)";
	}

	/**
	 * Loads a deck from file.
	 * 
	 * @param name
	 *          the name of the deck.
	 * @return a boolean indicating whether the requested deck was successfully
	 *         loaded
	 */
	public static boolean loadDeck(String name) {

		// checking preconditions
		Utilities.require(Utilities.isStringValidIdentifier(name),
		    "Deck.loadDeck: name must be a valid identifier, "
		        + "meaning that it exists and contains non-whitespace "
		        + "characters.");

		File deckFile = LogicalDeck.getDeckFileHandle(name);

		// case A: the file does not exist
		if (!deckFile.isFile()) {
			return false;
		}

		// so the file must exist. But does it contain a valid deck?
		// anyway, first save the old deck to be safe.
		save();
		try (ObjectInputStream objInStream = new ObjectInputStream(
		    new FileInputStream(deckFile))) {
			m_contents = (LogicalDeck) objInStream.readObject();
			notifyOfDeckChange();
			return m_contents != null;
		} catch (Exception e) {
			// something goes wrong with deserializing the deck; so
			// you also can't read the file
			System.out
			    .println("Deck.loadDeckWithName: could not load deck from file");
			e.printStackTrace();
			return false;
		}

		// postconditions: none: boolean returned
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
		m_contents = new LogicalDeck(name);
		save();

		// postconditions: the deck should exist (deck.save handles any errors
		// occurring during saving the deck).
		Utilities.require(deckHasBeenLoaded(), "Deck.createDeckWithName() error: "
		    + "problem creating and/or writing the new deck.");

		// The deck has been changed. So ensure depending GUI-elements know that.
		notifyOfDeckChange();
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
		try (ObjectOutputStream objOutStream = new ObjectOutputStream(
		    new FileOutputStream(m_contents.getFileHandle()))) {
			objOutStream.writeObject(m_contents);
		} catch (Exception e) {
			// Something goes wrong with serializing the deck; so
			// you cannot create the file.
			e.printStackTrace();
			Utilities.require(false,
			    "Deck.save() error: cannot write the new deck to disk.");
		}

		// postconditions: the save has to be a success! Which it is if no
		// exception occurred - in other words, if you get here.
	}

	/**
	 * Notifies all listeners that the deck has changed.
	 */
	private static void notifyOfDeckChange() {
		// preconditions: none (I assume the deck has really changed)
		for (DeckChangeListener deckChangeListener : m_deckChangeListeners) {
			deckChangeListener.respondToChangedDeck();
		}
		// postconditions: none
	}

	/**
	 * Adds a card to the deck. Should only be called after the card-adding
	 * validity is ensured using 'canAddCard' - if that is erroneously forgotten,
	 * this function will crash hard to prevent worse programming problems to crop
	 * up later.
	 * 
	 * @param card
	 *          the card to be added to the deck
	 */
	public static void addCard(Card card) {
		// preconditions: a deck should have been loaded/created
		ensureDeckExists();
		// for the rest, delegate everything (preconditions, postconditions and
		// error handling) to the logical deck itself (the contents)
		m_contents.addCard(card);
		notifyOfDeckChange();
	}

	/**
	 * Checks if a certain card can be added to the deck. In practice, this means
	 * that the front is a valid identifier that is not already present in the
	 * deck, and the back is not a null pointer. Note: this method delegates the
	 * call to the logical deck.
	 * 
	 * @param card
	 *          the candidate card to be added.
	 * @return whether the card can legally be added to the deck.
	 */
	public static boolean canAddCard(Card card) {
		// preconditions: a deck should have been loaded/created
		ensureDeckExists();
		// for the rest, delegate everything (preconditions, postconditions and
		// error handling) to the logical deck itself (the contents)
		return m_contents.canAddCard(card);
	}

	/**
	 * Adds a DeckChangeListener to the deck, to be notified of updates in the
	 * status of the deck.
	 * 
	 * @param deckChangeListener
	 *          the object that should be added to the set of objects to be
	 *          notified when the deck changes.
	 */
	public static void addDeckChangeListener(
	    DeckChangeListener deckChangeListener) {
		// preconditions: deckChangeListener should not be null. It should also
		// not already be present in the set; that would be bad programming.
		Utilities.require(deckChangeListener != null,
		    "Deck.addDeckChangeListener() error: the candidate listener cannot "
		        + "be null.");
		Utilities.require(!m_deckChangeListeners.contains(deckChangeListener),
		    "Deck.addDeckChangeListener() error: attempt to register the same "
		        + "DeckChangeListener object twice.");

		m_deckChangeListeners.add(deckChangeListener);
		// postconditions: none (I trust add to work, and out-of-memory and such
		// to be unlikely)
	}

	/**
	 * Returns the interval that Eb waits after the user adds a card before
	 * letting the user review the card.
	 * 
	 * @return the interval that Eb waits after the user adds a card before
	 *         letting the user review the card.
	 */
	public static TimeInterval getInitialInterval() {
		// preconditions and postconditions: handled by delegating the call to
		// LogicalDeck.getInitialInterval()
		ensureDeckExists();
		return m_contents.getInitialInterval();
	}

	/**
	 * Sets the interval for Eb to wait after the user adds a card before
	 * presenting the card for review.
	 * 
	 */
	/*
	 * public static void setInitialInterval(TimeInterval newInterval) { //
	 * precondition and postconditions: handled by the embedded LogicalDeck
	 * ensureDeckExists(); if (m_contents.canSetInitialInterval(newInterval) {
	 * m_contents.setInitialInterval(newInterval); } else {
	 * 
	 * }
	 */
	// }
}
