package eb;

/**
 * Interface to be implemented by objects (like GUI elements) that need to
 * change their status or display in response to a change in the current deck.
 * 
 * @author Eric.Lameijer
 *
 */
public interface DeckChangeListener {

	/**
	 * Executes the actions necessary to update the object's status or display
	 * after the deck has been changed.
	 */
	abstract void respondToChangedDeck();
}
