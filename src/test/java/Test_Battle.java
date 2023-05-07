import classes.Battle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import classes.*;

import java.util.ArrayList;
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

    // Set up method, runs before each test case
    @BeforeEach
    void setUp() {
        // Create four identical cards for the test deck
        Card card1 = new Card ("1","Kraken_0",0, MonsterCategory.Kraken, Element.Water);
        Card card2 = new Card ("2","Kraken_0",0, MonsterCategory.Kraken, Element.Water);
        Card card3 = new Card ("3","Kraken_0",0, MonsterCategory.Kraken, Element.Water);
        Card card4 = new Card ("4","Kraken_0",0, MonsterCategory.Kraken, Element.Water);

        // Initialize an ArrayList to hold the cards
        List<Card> cards = new ArrayList<>();
        // Add cards to the list
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);

        // Create a new CardDeck object using the list of cards
        deck_0 = new CardDeck(cards);
    }

    // Test case for the draw() method
    @Test
    public void testDraw() {
        // Get the Battle instance
        Battle manager = Battle.getInstance();

        // Set up mocked User object behavior
        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");

        // Execute a battle using the mocked users and test decks
        manager.battle(userA,userB,deck_0,deck_0);

        // Verify that both users called the draw() method
        verify(userA).draw();
        verify(userB).draw();
    }

    // Test case for win() and lose() methods
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

