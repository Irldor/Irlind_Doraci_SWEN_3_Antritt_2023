import classes.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Test_Damage {
    @Test
    /**
     * Test the ability of the Knight card against other cards.
     * This test case is focused on ensuring that the calculation
     * of damages when a Knight card battles with a WaterSpell card is correct.
     */
// Method to create a card using the Handler_Card
    private Card createCard(String id, String name, float damage) {
        Handler_Card handlerCard = Handler_Card.getInstance();
        MonsterCategory category = handlerCard.determineMonsterCategory(name);
        Element element = handlerCard.determineElementType(name);

        return new Card(id, name, damage, category, element);
    }

    // Method to calculate and test the damage between two cards
    private void testDamage(Card card1, Card card2, float expectedDamage1, float expectedDamage2) {
        // Get the instance of the Battle manager
        Battle manager = Battle.getInstance();

        // Calculate the damage of each card against the other
        float result1 = manager.damage(card1, card2);
        float result2 = manager.damage(card2, card1);

        // Check if the expected damage is correct for each card
        assertEquals(expectedDamage1, result1);
        assertEquals(expectedDamage2, result2);
    }

    @Test
    public void testKnight() {
        // Arrange: Create two cards to test the Knight ability
        Card knightCard = createCard("1", "Knight", 50);
        Card waterSpellCard = createCard("2", "WaterSpell", 100);

        // Act & Assert: Test the damage between the two cards
        testDamage(knightCard, waterSpellCard, -1, 50);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    /**
     * Test the ability of the FireElf card against other cards.
     * Specifically, this test verifies the calculation of damages
     * when a FireElf card battles with a WaterDragon card.
     */
    public void testFireElf() {
        // Arrange: Create two cards to test the FireElf ability
        Card fireElfCard = createCard("1", "FireElf", 15);
        Card waterDragonCard = createCard("2", "WaterDragon", 15);

        // Act & Assert: Test the damage between the two cards
        testDamage(fireElfCard, waterDragonCard, 7.5f, 0);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    /**
     * Test the ability of the Kraken card against other cards.
     * This test verifies the correctness of damage calculation
     * when a Kraken card battles with a Spell card.
     */
    public void testKraken() {
        // Arrange: Create two cards to test the Kraken ability
        Card krakenCard = createCard("1", "Kraken", 50);
        Card spellCard = createCard("2", "Spell", 100);

        // Act & Assert: Test the damage between the two cards
        testDamage(krakenCard, spellCard, 50f, 0);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    /**
     * This test checks the WaterSpell card's ability against other cards.
     * It ensures that the damage calculation is accurate when a WaterSpell card
     * battles with FireSpell and NormalSpell cards.
     */
    public void testWaterSpell() {
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
        float result1 = manager.damage(card1,card2);
        float result2 = manager.damage(card1,card3);

        // Check if the expected damage is correct for WaterSpell
        assertEquals(100,result1);
        assertEquals(25,result2);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    /**
     * This test verifies the FireSpell card's ability against other cards.
     * It tests the correctness of the damage calculation when a FireSpell card
     * battles with WaterSpell and NormalSpell cards.
     */
    public void testFireSpell() {
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
        float result1 = manager.damage(card2,card1);
        float result2 = manager.damage(card2,card3);

        // Check if the expected damage is correct for FireSpell
        assertEquals(25,result1);
        assertEquals(100,result2);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This test case validates the NormalSpell card's attack ability against other cards.
     * It checks whether the damage calculation is accurate when a NormalSpell card
     * combats with WaterSpell and FireSpell cards.
     */
    @Test
    public void TestNormalSpell() {
        // Arrange: Create the cards for the test
        Card waterSpellCard = createCard("1", "WaterSpell", 50);
        Card fireSpellCard = createCard("2", "FireSpell", 50);
        Card normalSpellCard = createCard("3", "NormalSpell", 50);

        // Act & Assert: Test the damage of NormalSpell against other cards
        testDamage(normalSpellCard, waterSpellCard, 100, 25);
        testDamage(normalSpellCard, fireSpellCard, 25, 100);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * This test verifies the attack ability of a NormalMonster card against other cards.
     * It ensures that the damage calculation is correct when a NormalMonster card (represented
     * by a Dragon, Knight, or Wizard) battles against other monster cards.
     */
    @Test
    public void TestNormalMonster() {
        // Arrange: Create the cards for the test
        Card dragonCard = createCard("1", "Dragon", 50);
        Card knightCard = createCard("2", "Knight", 50);
        Card wizardCard = createCard("3", "Wizard", 50);

        // Act & Assert: Test the damage of each card against the others
        testDamage(dragonCard, knightCard, 50, 50);
        testDamage(dragonCard, wizardCard, 50, 50);
        testDamage(knightCard, dragonCard, 50, 50);
        testDamage(knightCard, wizardCard, 50, 50);
        testDamage(wizardCard, dragonCard, 50, 50);
        testDamage(wizardCard, knightCard, 50, 50);
    }
}
