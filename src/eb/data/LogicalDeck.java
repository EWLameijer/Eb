package eb.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import eb.subwindow.StudyOptions;
import eb.utilities.TimeInterval;
import eb.utilities.Utilities;

/**
 * Contains the properties belonging to the 'pure' deck itself, like its name
 * and contents [not the mess of dealing with the GUI, which is the
 * responsibility of the Deck class]
 *
 * @author Eric-Wubbo Lameijer
 */
public class LogicalDeck implements Serializable {

	// Automatically generated ID for serialization.
	private static final long serialVersionUID = 8271837223354295531L;

	// The file extension of a deck.
	private static final String DECKFILE_EXTENSION = ".deck";

	// The name of the deck (like "Spanish"). Does not include the ".deck"
	// extension.
	private final String m_name;

	// The cards contained by this deck.
	private final List<Card> m_cards;

	// The study options of this deck (interval increase between reviews etc.)
	private StudyOptions m_studyOptions;

	/**
	 * Constructs a deck with name "name". Note that by defining this constructor,
	 * it is not needed to define a 'forbidden' default constructor anymore.
	 *
	 * @param name
	 *          the name of the deck to be created
	 */
	public LogicalDeck(String name) {
		// preconditions
		Utilities.require(Utilities.isStringValidIdentifier(name),
		    "LogicalDeck constructor error: deck has a bad name.");

		// code
		m_name = name;
		m_cards = new ArrayList<>();
		m_studyOptions = StudyOptions.getDefault();

		// postconditions: none. The deck should have been constructed,
		// everything should work
	}
	
	private String formatToTwoDigits(int input) {
		if (input < 10) {
			return "0" + input;
		} else {
			return "" + input;
		}
	}
	
	public void saveDeckToTextfile() {
		// TODO Auto-generated method stub
		// Phase 1: get proper filename for deck
		LocalDateTime now = LocalDateTime.now();
		String textFileName = getName() 
				+ "_"
				+ formatToTwoDigits(now.get(ChronoField.DAY_OF_MONTH)) 
				+ formatToTwoDigits(now.get(ChronoField.MONTH_OF_YEAR))
				+ formatToTwoDigits(now.get(ChronoField.YEAR) % 100 )
				+ "_"
				+ formatToTwoDigits(now.get(ChronoField.HOUR_OF_DAY))
				+ formatToTwoDigits(now.get(ChronoField.MINUTE_OF_HOUR))
				+ ".txt";
		try ( FileWriter outputFile = new FileWriter(textFileName)) {
			outputFile.write("Number of cards is: " + m_cards.size() + Utilities.EOL);
			m_cards.stream().sorted( (first,second) -> first.getFront().compareTo(second.getFront())).
				forEach(e -> 
					{ 
						try {
							outputFile.write(e.getFront() + "\t\t" + e.getBack() + Utilities.EOL);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				);
			
		} catch (Exception e) {
			Utilities.require(false, "Deck.saveDeckToTextfile() error: cannot save text "
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
	static File getDeckFileHandle(String deckName) {
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
	File getFileHandle() {
		// preconditions: none (the deck should exist, of course, but since this
		// function can only be called if the deck already exists...

		// code
		final File deckFileHandle = LogicalDeck.getDeckFileHandle(m_name);

		// postconditions: the fileHandle should not be null, after all, that
		// would mean an evil error has occurred
		Utilities.require(deckFileHandle != null, "LogicalDeck.getFileHandle() "
		    + " error: problem creating file handle for deck.");

		return deckFileHandle;
	}

	/**
	 * Returns the number of cards in this deck.
	 *
	 * @return the number of cards in this deck
	 */
	public int getCardCount() {
		// preconditions: none, as we initialize the list of cards at construction.

		// postconditions: none (I assume that the standard size() method works
		// properly)
		return m_cards.size();
	}

	/**
	 * Returns whether this new front (text) does not already occur in the deck -
	 * after all, the deck is a kind of map, with a stimulus (front) leading to
	 * only one response (back).
	 *
	 * @param front
	 *          the front of the card to be checked for uniqueness in the deck
	 *
	 * @return whether the card would be a valid addition to the deck (true) or
	 *         whether the front would be a duplicate of a front already present
	 *         (false)
	 */
	private boolean isNotYetPresentInDeck(String front) {
		return !getCardWithFront(front).isPresent();
	}

	/**
	 * Checks if a certain card can be added to the deck. In practice, this means
	 * that the front is a valid identifier that is not already present in the
	 * deck, and the back is not a null pointer.
	 *
	 * @param card
	 *          the candidate card to be added.
	 *
	 * @return whether the card can legally be added to the deck.
	 */
	protected boolean canAddCard(Card card) {
		// preconditions: the card should not be null. Otherwise, all cards,
		// even invalid ones, should be able to be handled by this method.
		Utilities.require(card != null, "LogicalDeck.canAddCard() error: "
		    + "the card to be tested cannot be null.");

		return Utilities.isStringValidIdentifier(card.getFront())
		    && (isNotYetPresentInDeck(card.getFront())) && (card.getBack() != null);

		// postconditions: none; a boolean will be returned.
	}

	/**
	 * Adds a card to the deck. Note that one has to call canAddCard() beforehand.
	 *
	 * @param card
	 *          the card to add to the deck.
	 */
	protected void addCard(Card card) {
		// preconditions: card must be 'addable'
		Utilities.require(canAddCard(card),
		    "LogicalDeck.addCard() error: the card "
		        + "that is intended to be added is invalid. The 'canAddCard' "
		        + "method has to be invoked first to check the possibility of the "
		        + "current method.");

		final boolean cardAddSuccessful = m_cards.add(card);

		// postconditions: the deck should have been grown by one.
		Utilities.require(cardAddSuccessful, "LogicalDeck.addCard() error: "
		    + " something has gone wrong while adding the card to the deck.");
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
	 * Returns the time that the user has to wait to the next review.
	 * 
	 * @return how long it will be until the next review.
	 */
	public Duration getTimeUntilNextReview() {
		Utilities.require(!m_cards.isEmpty(),
		    "LogicalDeck.getTimeUntilNextReview()) error: the time till next "
		        + "review is undefined for an empty deck.");
		Duration minimumTimeUntilNextReview = m_cards.get(0)
		    .getTimeUntilNextReview();
		for (Card card : m_cards) {
			if (card.getTimeUntilNextReview()
			    .compareTo(minimumTimeUntilNextReview) < 0) {
				minimumTimeUntilNextReview = card.getTimeUntilNextReview();
			}
		}
		return minimumTimeUntilNextReview;
	}

	public Duration getInitialInterval() {
		TimeInterval initialInterval = m_studyOptions.getInitialInterval();
		return initialInterval.asDuration();
	}

	public List<Card> getReviewableCardList() {
		List<Card> reviewableCards = new ArrayList<>();
		for (Card card : m_cards) {
			if (card.getTimeUntilNextReview().isNegative()) {
				reviewableCards.add(card);
			}
		}
		return reviewableCards;
	}

	public String getName() {
		return m_name;
	}

	public Optional<Card> getCardWithFront(String frontText) {
		// Preconditions: front is a valid identifier.
		Utilities.require(Utilities.isStringValidIdentifier(frontText),
		    "LogicalDeck.isNotYetPresentInDeck() error: the text on the front of the "
		        + "card needs to be a valid identifier, not null or a string with "
		        + "only whitespace characters.");

		for (final Card card : m_cards) {
			if (card.getFront().equals(frontText)) {
				return Optional.of(card); // a card with the same front IS present in
				                          // the deck.
			}
		}

		// if you get here, no card with the checked-for front has been found
		return Optional.empty();

		// Postconditions: none, really. Simple return of a boolean.
	}

	public void removeCard(Card card) {
		m_cards.remove(card);

	}
}
