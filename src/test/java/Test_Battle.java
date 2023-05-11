import classes.Battle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import classes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Test_Battle {
    // Declare mocked User objects to simulate real users in tests
    @Mock
    private User userA;
    @Mock
    private User userB;

    // Declare a CardDeck object to be used in test cases
    private CardDeck deck_0;

    // Helper method to create a card
    private Card createCard(String id, String name, int damage, MonsterCategory category, Element element) {
        return new Card(id, name, damage, category, element);
    }

    // Helper method to create a deck of cards
    private CardDeck createDeck(Card... cards) {
        return new CardDeck(Arrays.asList(cards));
    }

    /**
     * The setup method that runs before each test case.
     * It creates a deck of four identical cards for testing.
     */
    @BeforeEach
    void setUp() {
        // Use the helper method to create four identical cards for the test deck
        Card card1 = createCard("1","Kraken_0",0, MonsterCategory.Kraken, Element.Water);
        Card card2 = createCard("2","Kraken_0",0, MonsterCategory.Kraken, Element.Water);
        Card card3 = createCard("3","Kraken_0",0, MonsterCategory.Kraken, Element.Water);
        Card card4 = createCard("4","Kraken_0",0, MonsterCategory.Kraken, Element.Water);

        // Use the helper method to create a deck with the four cards
        deck_0 = createDeck(card1, card2, card3, card4);
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Method for setting up a battle
    private void setupBattle(User userA, User userB, CardDeck deckA, CardDeck deckB) {
        // Define the behavior of the mocked User objects when the getName method is called
        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");

        // Get the singleton instance of the Battle class and execute a battle
        Battle.getInstance().battle(userA, userB, deckA, deckB);
    }

    @Test
    public void testDraw() {
        // Arrange: Set up the battle
        setupBattle(userA, userB, deck_0, deck_0);

        // Assert: Verify that the expected results have occurred
        verify(userA).draw();
        verify(userB).draw();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This test case checks the win() and lose() methods in the Battle class.
     * It verifies that the user with the stronger deck calls win() method and the user with the weaker deck calls lose() method.
     */
    @Test
    public void testWin() {
        // Get the Battle instance
        Battle manager = Battle.getInstance();

        // Create a winning card and a list of winning cards for the winning deck
        Card winningCard = new Card("1", "Strong_50", 50, MonsterCategory.Kraken, Element.Water);
        List<Card> winningCards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            winningCards.add(new Card(winningCard.getId(), winningCard.getName(), winningCard.getDamage(), winningCard.getMonsterCategory(), winningCard.getElementType()));
        }
        CardDeck deck_winner = new CardDeck(winningCards);

        // Create a losing card and a list of losing cards for the losing deck
        Card losingCard = new Card("2", "Weak_20", 20, MonsterCategory.Kraken, Element.Water);
        List<Card> losingCards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            losingCards.add(new Card(losingCard.getId(), losingCard.getName(), losingCard.getDamage(), losingCard.getMonsterCategory(), losingCard.getElementType()));
        }
        CardDeck deck_loser = new CardDeck(losingCards);

        // Set up mocked User object behavior
        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");

        // Execute a battle using the mocked users and winning/losing decks
        manager.battle(userA, userB, deck_winner, deck_loser);

        // Verify that userA called win() method and userB called lose() method
        verify(userA).win();
        verify(userB).lose();
    }
}

