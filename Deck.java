package eb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** [CC]
 * A Deck is a collection of cards (usually all belonging to a specific subject,
 * like Spanish) that can be studied by the user.
 * As there is only one deck active in Eb, and it needs to be shared with all 
 * UI windows, a singleton class may be better than having a Deck object that
 * is passed everywhere.
 * Note: one could regard the situation as if "Deck" is the interface that the
 * rest of the program deals with (Deck.getCardCount(), Deck.addCard(card) etc.)
 * However, the contents of the Deck is a different story. Intuitively, there
 * could be a "Chinese" deck, a "Java" deck etc. Those all are 'bare' data,
 * which should not know anything about the UI, and should be changed without
 * resetting all UI links to the new deck. So one should probably split the Deck
 * as the global instance of the one and only deck in Eb from the 'deck', being
 * the contents of a normal SRS deck.
 * 
 * @author Eric-Wubbo Lameijer
 */
public class Deck {
  
  // The "singleton" pointer to the contents managed by this deck [CCC
  private static DeckContents m_contents = null;

  // The name of the default deck [CCC 
  private static final String DEFAULT_DECKNAME = "default";
			
  /** [CPPRCCC]
   * Returns the number of cards in this deck.
   * @return the number of cards in the currently active deck
   */
  public static int getCardCount() {
	// preconditions: the deck should have been initialized
	ensureDeckExists();
	
	// postconditions: none (I assume getCardCount works properly)
	return m_contents.getCardCount();
  }
  
  /** [CPPRCCC]
   * Returns whether a deck / the contents of a deck have been loaded.
   * @return whether a deck has been loaded into this "deck-container"
   */
  private static boolean deckHasBeenLoaded() {
	  // preconditions: none - this method is checking a condition
	  // postconditions: none: a normal boolean is returned.
	  return (m_contents != null);
  }
  
  /** [CPPRCCC]
   * Returns whether the deck has been initialized, even if it is only with
   * the default deck. 
   * @return whether the deck has been initialized, meaning it can be used
   * for things like counting the number of cards in it.
   */
  private static void ensureDeckExists() {
	// preconditions: none. After all, this is kind of a precondition-checking
	// method.
	  
	if (!deckHasBeenLoaded() ) {    
	  // No deck has been loaded yet - try to load the default deck, 
	  // or else create it.
	  boolean deckLoadedSuccessfully = loadDeck(DEFAULT_DECKNAME);
		      
	  // If loading the deck failed, try to create it.
	  // Note that createDeckWithName cannot return null; it will exit
	  // with an error message instead.
	  if (!deckLoadedSuccessfully ) {
		createDeckWithName(DEFAULT_DECKNAME);
	  }
	}
		    
	// postconditions: m_currentDeck cannot be null, but that is ensured
	// by the Deck.createDeckWithName call, which exits with an error if
	// a deck cannot be created.
	Utilities.require( deckHasBeenLoaded(), "Deck.deckExists error: " + 
	    "there is no valid deck.");
  }
	
  /** [CPPRCCC]
   * Loads a deck from file.
   * @param name the name of the deck.
   * @return a boolean indicating whether the requested deck was successfully
   * loaded
   */
  public static boolean loadDeck(String name) {	

	// checking preconditions
	Utilities.require(Utilities.isStringValidIdentifier(name),
					"Deck.loadDeck: name must be a valid identifier, "+
					"meaning that it exists and contains non-whitespace " + 
					"characters.");
		
	File deckFile = DeckContents.getDeckFileHandle(name);
		
	// case A: the file does not exist
	if (!deckFile.isFile()) {
	  return false;
	}
		
	// so the file must exist. But does it contain a valid deck?
	// anyway, first save the old deck to be safe.
	save();
	try (ObjectInputStream objInStream = 
	    new ObjectInputStream(new FileInputStream(deckFile))) {		 
	  m_contents = (DeckContents)objInStream.readObject();
	  return (m_contents != null);
	}
	catch (Exception e) {
	  // something goes wrong with deserializing the deck; so
	  // you also can't read the file
	  System.out.println( 
	      "Deck.loadDeckWithName: could not load deck from file");
	  e.printStackTrace();
	  return false;
	}
		
	// postconditions: none: boolean returned
  }
	
  /** [CPPRCCC]
   * Creates a deck with name "name". 
   * 
   * @param name the name of the deck to be created
   * @return an optional containing the newly created deck, or null if
   * deck creation was not possible
   */
  public static void createDeckWithName(String name) 
      throws IllegalArgumentException{
		
	// checking preconditions
	Utilities.require( Utilities.isStringValidIdentifier(name),
		  "Deck.createDeckWithName error: name cannot be null or empty.");

	// code
	// if there is already a deck loaded/constructed previously, save it to disk
	// before creating the new deck
	if (deckHasBeenLoaded()) {
	  save();
	}
	m_contents = new DeckContents(name);
	save();
		
	// postconditions: the deck should exist (deck.save handles any errors
	// occurring during saving the deck).
	Utilities.require( deckHasBeenLoaded(), 
		"Deck.createDeckWithName error: " + 
		"problem creating and/or writing the new deck.");
  }
	
  /** [CPPRCCC]
   * Saves the deck to disk.
   */
  public static void save() {
	// preconditions: none (well, the deck should exist, but that's not a 
	// problem otherwise this function cannot be called anyway...
		
	// code
	  
	// first: check if there is a deck to be saved in the first place.
	if (!deckHasBeenLoaded()) {
	  // If there is no deck, there is no necessity to save it...
	  return;
	}
	try (ObjectOutputStream objOutStream = 
		new ObjectOutputStream(new FileOutputStream(m_contents.getFileHandle()))) {		 
			objOutStream.writeObject(m_contents);
		} catch (Exception e) {
			// something goes wrong with serializing the deck; so
			// you also can't create the file
			e.printStackTrace();
			Utilities.require( false,
					"Deck.save error: cannot write the new deck to disk.");
		}
		
		// postconditions: the save has to be a success! Which it is if no
		// exception occurred - in other words, if you get here.
	}
	
  /** [CPPRCCC]
	* Adds a card to the deck. Should only be called after the card-adding
	* validity is ensured using 'canAddCard' - if that is erroneously forgotten,
	* this function will crash hard to prevent worse programming problems to 
	* crop up later.
	* @param card the card to be added to the deck
	*/
  public static void addCard(Card card) {
	// preconditions: a deck should have been loaded/created
	ensureDeckExists();  
	// for the rest, delegate everything (preconditions, postconditions and 
	// error handling) to the logical deck itself (the contents)
	m_contents.addCard(card);
  }
  
	/** [CPPRCCC]
	 * Checks if a certain card can be added to the deck. In practice, this 
	 * means that the front is a valid identifier that is not already present 
	 * in the deck, and the back is not a null pointer.
	 * Note: delegates call to DeckContents.
	 * @param card the candidate card to be added.
	 * @return whether the card can legally be added to the deck.
	 */
  public static boolean canAddCard(Card card) {
	// preconditions: a deck should have been loaded/created
	ensureDeckExists();  
	// for the rest, delegate everything (preconditions, postconditions and 
	// error handling) to the logical deck itself (the contents)
	return m_contents.canAddCard(card);	
  }
}
