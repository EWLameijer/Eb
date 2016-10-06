package eb.data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eb.utilities.Utilities;

/**
 * The Card class represents a card, which has contents (front and back, or
 * 'stimulus' and 'response', as well as a history (number of repetitions and
 * such).
 *
 * @author Eric-Wubbo Lameijer
 */
public class Card implements Serializable {

	// Automatically generated ID for serialization.
	private static final long serialVersionUID = -2746012998758766327L;

	// The text/contents of the front of the card. Should never be null.
	private String m_textOnFront;

	// The text/contents of the back of the card. Should never be null.
	private String m_textOnBack;

	// The time/instant when this card was created.
	private Instant m_creationInstant;

	// The reviews of the cards. Should never be null.
	private List<Review> m_reviews;

	/**
	 * Creates a new card; ensures that the input is valid. Note that empty cards
	 * are allowed (after all, a card will be empty before it is filled), however,
	 * an empty card may not be added to a Deck - but that's a matter of the
	 * Deck's policy, of course.
	 *
	 * @param textOnFront
	 *          the text for the front of the card. May be a blank string, but may
	 *          not be null.
	 * @param textOnBack
	 *          the text for the back of the card. May be a blank string, but may
	 *          not be null.
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
		m_creationInstant = Instant.now();
		m_reviews = new ArrayList<>();

		// postconditions: none. Given valid input (the preconditions have been
		// met), the card will be created successfully.
	}

	/**
	 * Returns the contents of the front of the card.
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
	 * Returns the contents of the back of the card.
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
	 * Returns the most recent review.
	 * 
	 * @return the most recent review.
	 */
	public Review getLastReview() {
		// needs to be public; is required by ReviewSession

		// preconditions: a review must have taken place, one should not call this
		// on a freshly created card.
		Utilities.require(hasBeenReviewed(), "History.getLastReview() "
		    + "error: no review has taken place yet. Please only call this method "
		    + "after checking the existence of a review with 'hasBeenReviewed'.");
		int indexOfLastReview = m_reviews.size() - 1;
		return m_reviews.get(indexOfLastReview);
	}

	/**
	 * Checks whether the card has been reviewed at least once.
	 * 
	 * @return true if the card has been reviewed at least once, false if the card
	 *         has just been created.
	 */
	boolean hasBeenReviewed() {
		// preconditions: none. Object exists
		return !m_reviews.isEmpty();
		// postconditions: none. Returns simple boolean.
	}

	/**
	 * Returns the instant that this card was created.
	 * 
	 * @return the instant that this card was created
	 */
	Instant getCreationInstant() {
		return m_creationInstant;
	}

	/**
	 * Debugging function, helps check that the reviews have proceeded correctly.
	 * Reports all reviews of this card performed so far.
	 */
	private void reportReviews() {
		for (Review review : m_reviews) {
			Logger.getGlobal()
			    .info(review.getThinkingTime() + " " + review.wasSuccess());
		}
	}

	/**
	 * Adds a new review to the list of reviews.
	 * 
	 * @param review
	 *          the review to be added to this card's list of reviews.
	 */
	public void addReview(Review review) {
		Utilities.require(review != null,
		    "Card.addReview error: review cannot be null.");
		m_reviews.add(review);
		reportReviews();
	}

	/**
	 * Sets the front of the card to a new value, which must be a valid identifier
	 * (cannot be null or the empty string, or a string containing only whitespace
	 * characters).
	 * 
	 * @param front
	 *          the new front of the card.
	 */
	public void setFront(String front) {
		Utilities.require(Utilities.isStringValidIdentifier(front),
		    "Card.setFront() error: "
		        + "the given front is not a valid identifier");
		m_textOnFront = front;
	}

	/**
	 * Sets the back of the card to a new value, which must be a valid identifier
	 * (cannot be null or the empty string, or a string containing only whitespace
	 * characters).
	 * 
	 * @param back
	 *          the new back of the card.
	 */
	public void setBack(String back) {
		Utilities.require(Utilities.isStringValidIdentifier(back),
		    "Card.setBack() error: " + "the given back is not a valid identifier");
		m_textOnBack = back;
	}

	/**
	 * How long the most recent streak is (2 uninterrupted successful reviews, 0
	 * successful reviews (after a failure), and so on...)
	 * 
	 * @return the length of the current reviewing success streak (>=0)
	 */
	public int streakSize() {
		int successfulReviewCount = 0;
		int lastReviewIndex = m_reviews.size() - 1;
		int currentReviewIndex = lastReviewIndex;
		while (currentReviewIndex >= 0
		    && m_reviews.get(currentReviewIndex).wasSuccess()) {
			successfulReviewCount++;
			currentReviewIndex--;
		}
		return successfulReviewCount;
	}

}
