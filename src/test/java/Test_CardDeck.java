import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import classes.Card;
import classes.CardDeck;
import classes.Element;
import classes.MonsterCategory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Test_CardDeck {

    // Declare an ArrayList of Card objects and a CardDeck
    List<Card> cards = new ArrayList<>();
    CardDeck deck;

    /**
     * This method sets up the initial conditions for each test.
     * It is run before each test case and initializes a deck of cards with various characteristics.
     */
    @BeforeEach
    void setUp() {
        // Add various cards to the ArrayList
        cards.add(new Card("0", "Purple Kraken", 140, MonsterCategory.Kraken, Element.Water));
        cards.add(new Card("1", "Fiery Ork", 98, MonsterCategory.Ork, Element.Fire));
        cards.add(new Card("2", "Yellow Dragon", 117, MonsterCategory.Dragon, Element.Normal));
        cards.add(new Card("3", "Blue Goblin", 87, MonsterCategory.Goblin, Element.Water));
        cards.add(new Card("4", "Fire Wizard", 117, MonsterCategory.Wizard, Element.Fire));
        cards.add(new Card("5", "Green FireElf", 107, MonsterCategory.FireElf, Element.Normal));
        cards.add(new Card("6", "Red Knight", 120, MonsterCategory.Knight, Element.Normal));
        cards.add(new Card("7", "Gray WaterElf", 106, MonsterCategory.FireElf, Element.Water));
        cards.add(new Card("8", "Deep Ocean Spell", 89, MonsterCategory.Spell, Element.Water));
        cards.add(new Card("9", "Black Spell", 100, MonsterCategory.Spell, Element.Normal));
        cards.add(new Card("10", "Red Fire Spell", 100, MonsterCategory.Spell, Element.Fire));

        //Initialize an ArrayList
        List<Card> testDeck = new ArrayList<>();

        //Add four random cards to the temporary Deck
        for (int i = 0; i < 4; i++) {
            Card card = cards.get((int)(Math.random() * cards.size()));
            cards.remove(card);
            testDeck.add(card);
        }

        //Create a new CardDeck object
        deck = new CardDeck(testDeck);
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This test case checks the functionality of adding cards to the deck.
     * It begins with a deck of four cards, adds two more, checks the size,
     * and then removes one card and checks the size again.
     */
    @Test
    public void addCardToDeck() {
        //Initialize ArrayList and fill it up
        List<Card> testDeck = new ArrayList<>();

        Card card1 = new Card("1", "Blue Dragon", 117, MonsterCategory.Dragon, Element.Water);
        Card card2 = new Card("2", "Old Fire Elf", 107, MonsterCategory.FireElf, Element.Fire);
        Card card3 = new Card("3", "Green Goblin", 87, MonsterCategory.Goblin, Element.Normal);
        Card card4 = new Card("4", "Heavy Knight", 120, MonsterCategory.Knight, Element.Normal);

        testDeck.add(card1);
        testDeck.add(card2);
        testDeck.add(card3);
        testDeck.add(card4);

        CardDeck deck = new CardDeck(testDeck);

        //Add two new cards to the deck
        Card card5 = new Card("5", "Red Dragon", 123, MonsterCategory.Dragon, Element.Fire);
        Card card6 = new Card("6", "Old Water Elf", 106, MonsterCategory.FireElf, Element.Water);
        deck.insertCard(card5);
        deck.insertCard(card6);

        //Assert that deck size is 6, remove one card then assert that deck size is 5
        assertEquals(6, deck.getDeckSize());
        deck.deleteCard(deck.pickRandomCard());
        assertEquals(5, deck.getDeckSize());
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This test case validates the functionality of removing a card from the deck.
     * It initializes a deck with four cards, removes one, and then checks if the deck size is reduced to three.
     */
    @Test
    public void removeCardFromDeck() {
        //Initialize ArrayList
        List<Card> testDeck = new ArrayList<>();

        //Add cards
        Card card1 = new Card("1", "Blue Dragon", 117, MonsterCategory.Dragon, Element.Water);
        Card card2 = new Card("2", "Old Fire Elf", 107, MonsterCategory.FireElf, Element.Fire);
        Card card3 = new Card("3", "Green Goblin", 87, MonsterCategory.Goblin, Element.Normal);
        Card card4 = new Card("4", "Heavy Knight", 120, MonsterCategory.Knight, Element.Normal);

        testDeck.add(card1);
        testDeck.add(card2);
        testDeck.add(card3);
        testDeck.add(card4);

        CardDeck deck = new CardDeck(testDeck);

        //Delete card and assert
        deck.deleteCard(card1);
        assertEquals(3, deck.getDeckSize());
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This test case checks the size of the deck after initialization.
     * The expected deck size is 4 after randomly selecting four cards from a list of 10.
     */
    @Test
    public void testDeckSize() {
        List<Card> deckList = new ArrayList<>();
        deckList.add(new Card("0", "Fire Dragon", 120, MonsterCategory.Dragon, Element.Fire));
        deckList.add(new Card("1", "Water Elf", 110, MonsterCategory.FireElf, Element.Water));
        deckList.add(new Card("2", "Normal Goblin", 95, MonsterCategory.Goblin, Element.Normal));
        deckList.add(new Card("3", "Strong Knight", 135, MonsterCategory.Knight, Element.Normal));
        deckList.add(new Card("4", "Black Kraken", 140, MonsterCategory.Kraken, Element.Water));
        deckList.add(new Card("5", "Gray Ork", 105, MonsterCategory.Ork, Element.Normal));
        deckList.add(new Card("6", "Red Wizard", 118, MonsterCategory.Wizard, Element.Fire));
        deckList.add(new Card("7", "Water Spell", 100, MonsterCategory.Spell, Element.Water));
        deckList.add(new Card("8", "Fire Spell", 110, MonsterCategory.Spell, Element.Fire));

        //Initialize an Array list for the temporary Deck
        List<Card> testDeckDeck = new ArrayList<>();

        //Add four random cards
        for (int i = 0; i < 4; i++) {
            Card card = deckList.get((int) (Math.random() * deckList.size()));
            deckList.remove(card);
            testDeckDeck.add(card);
        }

        //Create a new CardDeck object and assert
        CardDeck deck = new CardDeck(testDeckDeck);
        assertEquals(4, deck.getDeckSize());
    }
}
