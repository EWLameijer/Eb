package eb.mainwindow.reviewing;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import eb.data.Card;
import eb.data.DeckManager;
import eb.data.Review;
import eb.eventhandling.BlackBoard;
import eb.eventhandling.Listener;
import eb.eventhandling.Update;
import eb.eventhandling.UpdateType;
import eb.mainwindow.MainWindowState;
import eb.utilities.Utilities;

class ReviewSession implements Listener {
	private List<Card> m_cardCollection;
	private int m_counter;
	private FirstTimer m_startTimer = new FirstTimer();
	private FirstTimer m_stopTimer = new FirstTimer();
	private final ReviewPanel m_reviewPanel;
	private boolean m_showAnswer;

	ReviewSession(ReviewPanel reviewPanel) {
		m_reviewPanel = reviewPanel;
		int maxNumReviews = DeckManager.getStudyOptions().getReviewSessionSize();
		List<Card> reviewableCards = DeckManager.getReviewableCardList();
		int totalNumberOfReviewableCards = reviewableCards.size();
		Logger.getGlobal()
		    .info("Number of reviewable cards is " + totalNumberOfReviewableCards);
		int numCardsToBeReviewed = Math.min(maxNumReviews,
		    totalNumberOfReviewableCards);
		// now, for best effect, those cards which have expired more recently should
		// be rehearsed first, as other cards probably need to be relearned anyway,
		// and we should try to contain the damage.
		reviewableCards
		    .sort((firstCard, secondCard) -> secondCard.getTimeUntilNextReview()
		        .compareTo(firstCard.getTimeUntilNextReview()));
		// get the first n for the review
		m_cardCollection = new ArrayList<>(
		    reviewableCards.subList(0, numCardsToBeReviewed));
		Collections.shuffle(m_cardCollection);
		BlackBoard.register(this, UpdateType.CARD_CHANGED);
		BlackBoard.register(this, UpdateType.DECK_CHANGED);
		BlackBoard.register(this, UpdateType.DECK_SWAPPED);
		m_counter = 0;
		startCardReview();
	}

	@Override
	public void respondToUpdate(Update update) {
		if (update.getType() == UpdateType.CARD_CHANGED) {
			updatePanels();
		} else if (update.getType() == UpdateType.DECK_CHANGED) {
			// It can be that the current card has been deleted, OR another card has
			// been deleted.
			updateCollection();
		} else if (update.getType() == UpdateType.DECK_SWAPPED) {
			cleanUp();
		}

	}

	private void startCardReview() {
		m_showAnswer = false;
		m_startTimer.reset();
		m_stopTimer.reset();
		m_startTimer.press();
		updatePanels();
	}

	private Card getCurrentCard() {
		Utilities.require(activeCardExists(),
		    "ReviewSession.getCurrentCard() error: there is no current card.");
		return m_cardCollection.get(m_counter);
	}

	public String getCurrentFront() {
		if (activeCardExists()) {
			return getCurrentCard().getFront();
		} else {
			return "";
		}
	}

	public String getCurrentBack() {
		if (activeCardExists()) {
			return getCurrentCard().getBack();
		} else {
			return "";
		}
	}

	private boolean activeCardExists() {
		return m_counter < m_cardCollection.size();
	}

	/**
	 * Wipes the review session.
	 */
	private void cleanUp() {
		BlackBoard.unRegister(this);
	}

	public void wasRemembered(boolean remembered) {
		Duration duration = Duration.between(m_startTimer.getInstant(),
		    m_stopTimer.getInstant());
		double duration_in_s = duration.getNano() / 1000_000_000.0
		    + duration.getSeconds();
		Logger.getGlobal().info(m_counter + " " + duration_in_s);
		Review review = new Review(duration, remembered);
		getCurrentCard().addReview(review);
		moveToNextReviewOrEnd();
	}

	private void moveToNextReviewOrEnd() {

		m_counter++;
		if (hasNextCard()) {
			m_counter++;
			startCardReview();
		} else {
			cleanUp();
			BlackBoard.post(new Update(UpdateType.PROGRAMSTATE_CHANGED,
			    MainWindowState.SUMMARIZING.name()));
		}
	}

	public boolean hasNextCard() {
		return m_counter + 1 < m_cardCollection.size();
	}

	public List<Review> getReviewResults() {
		List<Review> listOfReviews = new ArrayList<>();
		for (Card card : m_cardCollection) {
			listOfReviews.add(card.getLastReview());
		}
		return listOfReviews;
	}

	public void updatePanels() {
		String currentBack = m_showAnswer ? getCurrentBack() : "";
		m_reviewPanel.updatePanels(getCurrentFront(), currentBack, m_showAnswer);
	}

	public void showAnswer() {
		m_stopTimer.press();
		m_showAnswer = true;
		updatePanels();
	}

	/**
	 * If cards are added to (or, more importantly, removed from) the deck, ensure
	 * that the card also disappears from the list of cards to be reviewed
	 */
	public void updateCollection() {
		for (int cardIndex = 0; cardIndex < m_cardCollection.size(); cardIndex++) {
			Card currentCard = m_cardCollection.get(cardIndex);
			if (!DeckManager.getCurrentDeck().getCards().contains(currentCard)) {
				m_cardCollection.remove(cardIndex);
				boolean deletingCurrentCard = (cardIndex == m_counter);
				if (cardIndex <= m_counter) {
					m_counter--;
				}
				if (deletingCurrentCard) {
					moveToNextReviewOrEnd();
				}
			}
		}
		updatePanels();
	}
}