import classes.Handler_Card;
import classes.Battle;
import org.junit.jupiter.api.Test;
import classes.Card;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Test_Damage {

    @Test
    // Test the ability of FireElf against other cards
    public void testFireElfAbility() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create two cards to test the FireElf ability
        String cardName1 = "FireElf";
        float cardDamage1 = 15;
        String cardName2 = "WaterDragon";
        float cardDamage2 = 15;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1", cardName1, cardDamage1, handlerCard.determineMonsterCategory(cardName1), handlerCard.determineElementType(cardName1));
        Card card2 = new Card("2", cardName2, cardDamage2, handlerCard.determineMonsterCategory(cardName2), handlerCard.determineElementType(cardName2));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of each card against the other
        float result1 = manager.calculateDamage(card1, card2);
        float result2 = manager.calculateDamage(card2, card1);


        // Check if the expected damage is correct for each card
        assertEquals(7.5, result1);
        assertEquals(0, result2);
    }

    @Test
    // Test the ability of Knight against other cards
    public void testKnightAbility() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create two cards to test the Knight ability
        String cardName1 = "Knight";
        float cardDamage1 = 50;
        String cardName2 = "WaterSpell";
        float cardDamage2 = 100;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1", cardName1, cardDamage1, handlerCard.determineMonsterCategory(cardName1), handlerCard.determineElementType(cardName1));
        Card card2 = new Card("2", cardName2, cardDamage2, handlerCard.determineMonsterCategory(cardName2), handlerCard.determineElementType(cardName2));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of each card against the other
        float result1 = manager.calculateDamage(card1, card2);
        float result2 = manager.calculateDamage(card2, card1);

        // Check if the expected damage is correct for each card
        assertEquals(-1, result1);
        assertEquals(50, result2);
    }

    @Test
    // Test the ability of Kraken against other cards
    public void testKrakenAbility() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create two cards to test the Kraken ability
        String cardName1 = "Kraken";
        float cardDamage1 = 50;
        String cardName2 = "Spell";
        float cardDamage2 = 100;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1", cardName1, cardDamage1, handlerCard.determineMonsterCategory(cardName1), handlerCard.determineElementType(cardName1));
        Card card2 = new Card("2", cardName2, cardDamage2, handlerCard.determineMonsterCategory(cardName2), handlerCard.determineElementType(cardName2));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of each card against the other
        float result1 = manager.calculateDamage(card1, card2);
        float result2 = manager.calculateDamage(card2, card1);

        // Check if the expected damage is correct for each card
        assertEquals(50, result1);
        assertEquals(0, result2);
    }

    @Test
    // Test the ability of Ork against other cards
    public void testOrkAbility() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create two cards to test the Ork ability
        String cardName1 = "Ork";
        float cardDamage1 = 50;
        String cardName2 = "Wizard";
        float cardDamage2 = 100;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1",cardName1,cardDamage1, handlerCard.determineMonsterCategory(cardName1), handlerCard.determineElementType(cardName1));
        Card card2 = new Card("2",cardName2,cardDamage2, handlerCard.determineMonsterCategory(cardName2), handlerCard.determineElementType(cardName2));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of each card against the other
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card2,card1);

        // Check if the expected damage is correct for each card
        assertEquals(0,result1);
        assertEquals(100,result2);
    }

    @Test
    // Test the ability of WaterSpell against other cards
    public void testWaterSpellAbility() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create three cards to test the WaterSpell ability
        String cardName1 = "WaterSpell";
        String cardName2 = "FireSpell";
        String cardName3 = "NormalSpell";

        float damage = 50;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1",cardName1,damage, handlerCard.determineMonsterCategory(cardName1), handlerCard.determineElementType(cardName1));
        Card card2 = new Card("2",cardName2,damage, handlerCard.determineMonsterCategory(cardName2), handlerCard.determineElementType(cardName2));
        Card card3 = new Card("2",cardName3,damage, handlerCard.determineMonsterCategory(cardName3), handlerCard.determineElementType(cardName3));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of WaterSpell against other cards
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card1,card3);

        // Check if the expected damage is correct for WaterSpell
        assertEquals(100,result1);
        assertEquals(25,result2);
    }

    @Test
    // Test the ability of FireSpell against other cards
    public void testFireSpellAbility() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create three cards to test the FireSpell ability
        String cardName1 = "WaterSpell";
        String cardName2 = "FireSpell";
        String cardName3 = "NormalSpell";

        float damage = 50;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1",cardName1,damage, handlerCard.determineMonsterCategory(cardName1), handlerCard.determineElementType(cardName1));
        Card card2 = new Card("2",cardName2,damage, handlerCard.determineMonsterCategory(cardName2), handlerCard.determineElementType(cardName2));
        Card card3 = new Card("2",cardName3,damage, handlerCard.determineMonsterCategory(cardName3), handlerCard.determineElementType(cardName3));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of FireSpell against other cards
        float result1 = manager.calculateDamage(card2,card1);
        float result2 = manager.calculateDamage(card2,card3);

        // Check if the expected damage is correct for FireSpell
        assertEquals(25,result1);
        assertEquals(100,result2);
    }


    @Test
    // Test the attack of a NormalSpell card against other cards
    public void NormalSpellAttack() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create three cards for the test
        String CardName1 = "WaterSpell";
        String CardName2 = "FireSpell";
        String CardName3 = "NormalSpell";

        float damage = 50;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1",CardName1,damage, handlerCard.determineMonsterCategory(CardName1), handlerCard.determineElementType(CardName1));
        Card card2 = new Card("2",CardName2,damage, handlerCard.determineMonsterCategory(CardName2), handlerCard.determineElementType(CardName2));
        Card card3 = new Card("2",CardName3,damage, handlerCard.determineMonsterCategory(CardName3), handlerCard.determineElementType(CardName3));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of NormalSpell against other cards
        float result1 = manager.calculateDamage(card3,card1);
        float result2 = manager.calculateDamage(card3,card2);

        // Check if the expected damage is correct for NormalSpell
        assertEquals(100,result1);
        assertEquals(25,result2);
    }

    @Test
    // Test the attack of a NormalMonster card against other cards
    public void NormalMonsterAttack() {
        Handler_Card handlerCard = Handler_Card.getInstance();

        // Create three cards for the test
        String CardName1 = "Dragon";
        String CardName2 = "Knight";
        String CardName3 = "Wizard";

        float damage = 50;

        // Create the cards using the Handler_Card
        Card card1 = new Card("1",CardName1,damage, handlerCard.determineMonsterCategory(CardName1), handlerCard.determineElementType(CardName1));
        Card card2 = new Card("2",CardName2,damage, handlerCard.determineMonsterCategory(CardName2), handlerCard.determineElementType(CardName2));
        Card card3 = new Card("2",CardName3,damage, handlerCard.determineMonsterCategory(CardName3), handlerCard.determineElementType(CardName3));

        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of each card against the others
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card1,card3);
        float result3 = manager.calculateDamage(card2,card1);
        float result4 = manager.calculateDamage(card2,card3);
        float result5 = manager.calculateDamage(card3,card1);
        float result6 = manager.calculateDamage(card3,card2);

        // Check if the expected damage is correct for each card
        assertEquals(50,result1);
        assertEquals(50,result2);
        assertEquals(50,result3);
        assertEquals(50,result4);
        assertEquals(50,result5);
        assertEquals(50,result6);
    }

}
