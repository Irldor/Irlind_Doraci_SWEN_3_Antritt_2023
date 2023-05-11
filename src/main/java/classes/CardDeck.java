package classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        if (initialCards == null) return;
        this.cardList.addAll(initialCards.stream()
                .limit(4)
                .collect(Collectors.toList()));
    }

    /**
     * Adds a card to the deck if it is not already present.
     *
     * @param card the card object to be added
     */
    public void insertCard(Card card) {
        cardList.stream()
                .filter(cardItem -> cardItem.equals(card))
                .findFirst()
                .ifPresentOrElse(
                        existingCard -> {},
                        () -> cardList.add(card)
                );
    }



    public void deleteCard(Card card) {
        //për të trajtuar mundësinë e cardList që të jetë null. Nëse cardList nuk është null,
        // ifPresent() ekzekuton funksionin lambda, që heq kartën nga lista.
        Optional.ofNullable(cardList).ifPresent(list -> list.remove(card));
    }



    /**
     * Retrieves a random card from the deck.
     *
     * @return a random card object or null if the deck is empty
     */

    public Card pickRandomCard() {
        return (cardList == null || cardList.isEmpty()) ? null :
                cardList.get(ThreadLocalRandom.current().nextInt(cardList.size()));
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
        return (cardList == null || cardList.isEmpty()) ? 0 : cardList.size();
    }

}



