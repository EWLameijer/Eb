package eb;

import java.io.Serializable;

/**
 * [CC] The Card class represents a card, which has contents (front and back,
 * 'stimulus' and 'response', as well as a history (number of repetitions and
 * such
 * 
 * @author Eric-Wubbo Lameijer
 */
public class Card implements Serializable {

	// Automatically generated ID for serialization
	private static final long serialVersionUID = -2746012998758766327L;

	// The text/contents of the front of the card
	private String m_textOnFront = null;

	// The text/contents of the back of the card
	private String m_textOnBack = null;

	/**
	 * Returns the contents of the front of the card
	 * 
	 * @return the contents of the front of the card
	 */
	public String getFront() {
		// preconditions: the front should not be null, that indicates some
		// logic error
		Utilities.require(m_textOnFront != null, "Card.getFront() error: "
		    + " the contents of the front of the card cannot be null.");

		// postconditions: same as preconditions
		return m_textOnFront;
	}

	/**
	 * Returns the contents of the back of the card
	 * 
	 * @return the contents of the back of the card
	 */
	public String getBack() {
		// preconditions: the front should not be null, that indicates some
		// logic error
		Utilities.require(m_textOnBack != null, "Card.getBack() error: "
		    + " the contents of the back of the card cannot be null.");

		// postconditions: same as preconditions
		return m_textOnBack;
	}

	/**
	 * Creates a new card; ensures that the input is valid. Note that empty cards
	 * are allowed (after all, a card will be empty before it is filled), however,
	 * an empty card may not be added to a Deck - but that's a matter of the
	 * Deck's policy, of course.
	 * 
	 * @param textOnFront
	 *          the text to go on the front of the card. May be a blank string,
	 *          but may not be null.
	 * @param textOnBack
	 *          the text to go on the back of the card. May be a blank string, but
	 *          may not be null.
	 */
	public Card(String textOnFront, String textOnBack) {
		// preconditions: textOnFront and textOnBack should not be null,
		// as that is likely due to a logic error somewhere and at least is
		// rather untidy
		Utilities.require(textOnFront != null, "Card constructor error: "
		    + "the contents of the front of the card should not be null.");
		Utilities.require(textOnBack != null, "Card constructor) error: "
		    + "the contents of the back of the card should not be null.");

		m_textOnFront = textOnFront;
		m_textOnBack = textOnBack;

		// postconditions: none. Given valid input (or the preconditions have
		// been met, the card will be created successfully.
	}
}
