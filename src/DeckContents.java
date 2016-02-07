package eb;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * [CC] Contains the properties belonging to the 'pure' deck itself, like its
 * name and contents [not the mess of dealing with the GUI, which is the
 * provenance of the Deck class]
 * 
 * @author Eric-Wubbo Lameijer
 */
public class DeckContents implements Serializable {

  // Automatically generated ID for serialization. [CCCC]
  private static final long serialVersionUID = 8271837223354295531L;

  // The file extension of a deck [CCCC]
  private static final String DECKFILE_EXTENSION = ".deck";

  // The name of the deck. [CCCC]
  private String m_name = null;

  // the cards contained by this deck [CCCC]
  private List<Card> m_cards = null;

  /**
   * [CPPRCCC] Returns the File object representing a deck with name "deckName"
   * on disk.
   * 
   * @param deckName
   *          the name of the deck
   * @return the File object belonging to this deck.
   */
  protected static File getDeckFileHandle(String deckName) {
    // preconditions: m_cards is a valid identifier
    Utilities.require(Utilities.isStringValidIdentifier(deckName),
        "Deck.getDeckFileHandle error: deck name is invalid.");

    // code
    String deckFileName = deckName + DECKFILE_EXTENSION;
    File deckFile = new File(deckFileName);

    // postconditions: deckFile exists!
    Utilities.require(deckFile != null,
        "Deck.getDeckFileHandle error: problem creating file handle for deck.");

    return deckFile;
  }

  /**
   * [CPPRCCC] Returns the handle (File object) to the file in which this deck
   * is stored.
   * 
   * @return the handle (File object) to the file which stores this deck
   */
  protected File getFileHandle() {
    // preconditions: none (the deck should exist, of course, but since this
    // function can only be called if the deck already exists...

    // code
    File deckFileHandle = DeckContents.getDeckFileHandle(m_name);

    // postconditions: the fileHandle should not be null, after all, that
    // would mean an evil error has occurred
    Utilities.require(deckFileHandle != null, "Deck.getFileHandle error: "
        + "problem creating file handle for deck.");

    return deckFileHandle;
  }

  /**
   * [CPPRCCC] Returns the number of cards in this deck.
   * 
   * @return the number of cards in this deck
   */
  public int getCardCount() {
    // preconditions
    Utilities.require(m_cards != null,
        "DeckContents.getCardCount error: Cards not initialized!");

    // postconditions: none (assume standard method size works properly)
    return m_cards.size();
  }

  /**
   * [CPPRCCC] Constructs a deck with name "name". Note that by defining this
   * constructor, it is not needed to define a 'forbidden' default constructor
   * anymore.
   * 
   * @param name
   *          the name of the deck to be created
   */
  public DeckContents(String name) {
    // preconditions
    Utilities.require(Utilities.isStringValidIdentifier(name),
        "Deck(name) error: deck has a bad name.");

    // code
    m_name = name;
    m_cards = new ArrayList<Card>();

    // postconditions: none. The deck should have been constructed,
    // everything should work
  }

  /**
   * [CPPRCCC] Returns whether this new front (text) does not already occur in
   * the deck - after all, the deck is a kind of map, with a stimulus (front)
   * leading to only one response (back).
   * 
   * @param front
   *          the front of the card to be checked for uniqueness in the deck
   * @return whether the card would be a valid addition to the deck (true) or
   *         whether the front would be a duplicate of a front already present
   *         (false)
   */
  private boolean isNotYetPresentInDeck(String front) {
    // Preconditions: front is a valid identifier
    // Precondition: the deck has been initialized (so is not null)
    Utilities.require(Utilities.isStringValidIdentifier(front),
        "Deck.isNotYetPresentInDeck error: the text on the front of the "
            + "card needs to be a valid identifier, not null or a string with "
            + "only whitespace characters.");
    Utilities.require(m_cards != null, "Deck.isNotYetPresentInDeck error: "
        + "the list of cards has not been properly initialized yet.");

    for (Card card : m_cards) {
      if (card.getFront().equals(front)) {
        return false; // a card with the same front IS present in the deck.
      }
    }

    // if you get here, no card with the checked-for front has been found
    return true;

    // Postconditions: none, really. Simple return of a boolean
  }

  /**
   * [CPPRCCC] Checks if a certain card can be added to the deck. In practice,
   * this means that the front is a valid identifier that is not already present
   * in the deck, and the back is not a null pointer.
   * 
   * @param card
   *          the candidate card to be added.
   * @return whether the card can legally be added to the deck.
   */
  protected boolean canAddCard(Card card) {
    // preconditions: the card should not be null. Otherwise, all cards,
    // even invalid ones, should be able to be handled by this method.
    Utilities.require(card != null,
        "Deck.canAddCard error: " + "the card to be tested cannot be null.");

    return ((Utilities.isStringValidIdentifier(card.getFront()))
        && (isNotYetPresentInDeck(card.getFront()))
        && (card.getBack() != null));

    // postconditions: none; a boolean will be returned.
  }

  protected void addCard(Card card) {
    Utilities.require(canAddCard(card),
        "DeckContents.addCard error: the card "
            + "that is intended to be added is invalid. The 'canAddCard' "
            + "method has to be invoked first to check the possibility of the "
            + "current method.");

    boolean cardAddSuccessful = m_cards.add(card);

    // postconditions: the deck should have been grown by one.
    Utilities.require(cardAddSuccessful, "Deck.addCard error: something "
        + "has gone wrong while adding the card to the deck.");
  }
}
