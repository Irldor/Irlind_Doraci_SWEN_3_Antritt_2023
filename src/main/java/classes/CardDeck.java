package classes;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a deck object that contains a collection of card objects.
 * The deck has the ability to add, remove, and retrieve cards.
 */
public class CardDeck {

    // A list to store the cards in the deck
    private List<Card> cardList = new ArrayList<>();

    /**
     * Constructor that initializes the deck with a given list of cards.
     * Only the first four cards are added to the deck.
     *
     * @param initialCards a list of cards to initialize the deck
     */
    public CardDeck(List<Card> initialCards) {
        if (initialCards != null) {
            for (int i = 0; initialCards.size() > i && i < 4; i++) {
                this.cardList.add(initialCards.get(i));
            }
        }
    }

    /**
     * Removes a card from the deck.
     *
     * @param card the card object to be removed
     */
    public void deleteCard(Card card) {
        if (cardList != null) {
            cardList.remove(card);
        }
    }

    /**
     * Adds a card to the deck if it is not already present.
     *
     * @param card the card object to be added
     */
    public void insertCard(Card card) {
        if (!cardList.contains(card)) {
            cardList.add(card);
        }
    }

    /**
     * Retrieves a random card from the deck.
     *
     * @return a random card object or null if the deck is empty
     */
    public Card pickRandomCard() {
        if (cardList != null && cardList.size() > 0) {
            return cardList.get((int) (Math.random() * cardList.size()));
        }
        return null;
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isDeckEmpty() {
        return cardList.isEmpty();
    }

    /**
     * Gets the size of the deck.
     *
     * @return the number of cards in the deck or 0 if the deck is empty
     */
    public int getDeckSize() {
        if (!isDeckEmpty()) {
            return cardList.size();
        }
        return 0;
    }
}



