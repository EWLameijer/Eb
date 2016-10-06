package eb.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	public static File getDeckFileHandle(String deckName) {
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
	public String getName() {
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

	/**
	 * Returns the time till the next review of the given card. The time can be
	 * negative, as that information can help deprioritize 'over-ripe' cards which
	 * likely have to be learned anew anyway.
	 * 
	 * @return the time till the next planned review of this card. Can be
	 *         negative.
	 */
	public Duration getTimeUntilNextReview(Card card) {
		// case 1: the card has never been reviewed yet. So take the creation
		// instant and add the user-specified initial interval.
		if (!card.hasBeenReviewed()) {
			return Duration.between(Instant.now(), m_studyOptions.getInitialInterval()
			    .asDuration().addTo(card.getCreationInstant()));
		} else {
			// other cases: there have been previous reviews.
			Review lastReview = card.getLastReview();
			Instant lastReviewInstant = lastReview.getInstant();
			Duration waitTime;
			if (lastReview.wasSuccess()) {
				waitTime = getIntervalAfterSuccessfulReview(card);
			} else {
				waitTime = m_studyOptions.getForgottenCardInterval().asDuration();
			}
			Temporal officialReviewTime = waitTime.addTo(lastReviewInstant);
			return Duration.between(Instant.now(), officialReviewTime);
		}
	}

	/**
	 * Returns the time to wait for the next review (the previous review being a
	 * success).
	 * 
	 * @return the time to wait for the next review
	 */
	private Duration getIntervalAfterSuccessfulReview(Card card) {
		// the default wait time after a single successful review is given by the
		// study options
		Duration waitTime = m_studyOptions.getRememberedCardInterval().asDuration();

		// However, if previous reviews also have been successful, the wait time
		// should be longer (using exponential growth by default, though may want
		// to do something more sophisticated in the future).
		double lengtheningFactor = DeckManager.getLengtheningFactor();
		int streakLength = card.streakSize();
		int numberOfLengthenings = streakLength - 1; // 2 reviews = lengthen 1x.
		for (int lengtheningIndex = 0; lengtheningIndex < numberOfLengthenings; lengtheningIndex++) {
			waitTime = Utilities.multiplyDurationBy(waitTime, lengtheningFactor);
		}
		return waitTime;
	}

	/**
	 * Returns a list of all the cards which should be reviewed at the current
	 * moment and study settings.
	 * 
	 * @return a list of all the cards which should be reviewed, given the current
	 *         card collection and study settings.
	 */
	public List<Card> getReviewableCardList() {
		List<Card> reviewableCards = new ArrayList<>();
		Iterator<Card> cardIterator = m_cardCollection.getIterator();
		while (cardIterator.hasNext()) {
			Card currentCard = cardIterator.next();
			if (getTimeUntilNextReview(currentCard).isNegative()) {
				reviewableCards.add(currentCard);
			}
		}
		return reviewableCards;
	}

	/**
	 * Returns the time that the user has to wait to the next review.
	 * 
	 * @return how long it will be until the next review.
	 */
	public Duration getTimeUntilNextReview() {
		Utilities.require(m_cardCollection.getSize() > 0,
		    "LogicalDeck.getTimeUntilNextReview()) error: the time till next "
		        + "review is undefined for an empty deck.");
		Iterator<Card> cardIterator = m_cardCollection.getIterator();

		Card firstCard = cardIterator.next();
		Duration minimumTimeUntilNextReview = getTimeUntilNextReview(firstCard);
		while (cardIterator.hasNext()) {
			Card card = cardIterator.next();
			Duration timeUntilThisCardIsReviewed = getTimeUntilNextReview(card);

			if (timeUntilThisCardIsReviewed
			    .compareTo(minimumTimeUntilNextReview) < 0) {
				minimumTimeUntilNextReview = timeUntilThisCardIsReviewed;
			}
		}
		return minimumTimeUntilNextReview;
	}

}
