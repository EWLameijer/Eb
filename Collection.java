package learning_software;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

public class Collection {
	private List<Deck> m_decks;
	//private Deck m_currentDeck;
	private final static String collectionOverviewFileName = 
			"collection_overview.eb";
	private String m_collectionName;
	
	// TODO: allow creation of different collections, for example for 
	// different users using the same machine
	//private static final String m_defaultCollectionFileName = "default_collection.eb";
	//private static List<String> m_deckNames; // most recently studied first
	//private static Hashtable<String,File> m_decks;
	
	private Collection(String collectionName) {
		m_collectionName = collectionName;
		m_decks = new ArrayList<Deck>();
		File collectionDirectory = new File(collectionName);
		if (!collectionDirectory.exists()) {
			collectionDirectory.mkdir();
		}
		String collectionOverviewFilePath = m_collectionName + File.separator + 
				collectionOverviewFileName;
		File collectionOverviewFile = new File(collectionOverviewFilePath);
		if (!collectionOverviewFile.exists()) {
			boolean canCreateOverviewFile = 
					Utilities.createEmptyFile(collectionOverviewFilePath);
			if (!canCreateOverviewFile) {
				// error message already produced by createEmptyFile
				// just return empty collection
				return;
			}
		}
		try( FileReader collectionOverviewReader = 
				new FileReader( collectionOverviewFilePath ) ){
			BufferedReader collectionOverviewBReader = 
					new BufferedReader( collectionOverviewReader);
			do {
				String deckName = collectionOverviewBReader.readLine();
				if (deckName==null || deckName.equals("")) {
					break;
				}
				loadDeck(deckName);
			} while(true);
		} catch (Exception e) {
			// if there is no collection yet, no problem; we'll just create
			// a new one.
		}
		sortDecks();
	}
	
	public void addDeck( String deckName, String deckFilename) {
		Utilities.createEmptyFile(deckFilename);
		Deck newDeck = new Deck(deckName,deckFilename);
		m_decks.add(newDeck);
		ChangePropagator.setCurrentDeck(newDeck);
	}
	
	public boolean noDeckActive() {
		return ChangePropagator.getCurrentDeck() == null;
	}
	
	public void save() {
		if (m_decks == null) return;
		for (Deck deck : m_decks ) {		
			deck.save();
		}
	}
	
	public void saveDeckNames(PrintWriter destination) {
		if (m_decks == null) return;
		for (Deck deck : m_decks ) {
			destination.printf("%s\t%s\t%tc",deck.getName(),deck.getFileName(),
					deck.getDateOfLastModification());
			destination.println();
		}
	}
	
	public Deck getCurrentDeck() {
		return ChangePropagator.getCurrentDeck();
	}
	
	private String deckNameToDeckFileName( String deckName ) {
		return m_collectionName + File.separator + deckName + ".deck";
	}
	
	private void loadDeck(String deckName) {
		String deckFileName = deckNameToDeckFileName( deckName );
		Deck newDeck = new Deck(deckName,deckFileName);
		m_decks.add( newDeck );	
	}
	
	private void sortDecks() {
		m_decks.sort(new Comparator<Deck> () {
			public int compare(Deck firstDeck, Deck secondDeck) {
				return firstDeck.getDateOfLastModification().compareTo(
						secondDeck.getDateOfLastModification());
			}
		});
		if (!m_decks.isEmpty()) {
			ChangePropagator.setCurrentDeck(m_decks.get(0));
		}
	}
	
	public static Collection loadCollection(String collectionName) {
		Collection newCollection = new Collection(collectionName);
		return newCollection;
	}
	
	public static void loadDefaultCollection() {
		
	}
	
	private void createDeck() {
		String newDeckName = JOptionPane.showInputDialog(this, 
				"Please enter the name of the deck to be created:");
		if (newDeckName != null ) {
			String deckFileName = newDeckName + ".deck";
			File deckFile = new File(deckFileName);
			if (deckFile.exists()) {
				JOptionPane.showMessageDialog(this, 
						"A deck with that name already exists.\n" +
						"Either choose a different name, or delete "+
						"the old deck first.",
						"Deck already exists",
						JOptionPane.ERROR_MESSAGE);
			}
			else {
				ChangePropagator.getCurrentCollection().addDeck(newDeckName,deckFileName);
				this.setTitle(newDeckName);
			}
		}
	}
}
