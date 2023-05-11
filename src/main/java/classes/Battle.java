package classes;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Battle {

    // Singleton instance of the Battle class
    private static Battle single_instance = null;

    // Users participating in the battle
    private User firstUser;
    private User secondUser;
    // Battle result in JSON format
    private String battleResult;
    // Flag to indicate if a battle is in progress
    private boolean isBusy = false;
    // Object used for synchronization between threads
    final Object LOCK_OBJECT = new Object();

    // Private constructor to implement Singleton pattern
    private Battle() {
    }

    // Method to get the singleton instance of the Battle class
    public static Battle getInstance() {
        if (single_instance == null) {
            single_instance = new Battle();
        }
        return single_instance;
    }

    /**
     * This method initiates a battle between two users.
     * @param newUser User to be registered and battle.
     * @return Result of the battle.
     */
    // Method to register a user for a battle and return the battle results
    public String registerAndBattleUser(User newUser) {
        // Register the first user and set isBusy to true
        if (firstUser == null) {
            firstUser = newUser;
            battleResult = null;
            isBusy = true;

            // Synchronize on the LOCK object to ensure exclusive access
            synchronized (LOCK_OBJECT) {
                while (isBusy) {
                    try {
                        // Wait for the battle to finish
                        LOCK_OBJECT.wait();
                    } catch (InterruptedException ie) {
                        // Ktu sigurohet trajtimi i një ndërprerjeje si kërkesë për dalje.
                        break;
                    }
                }
            }
            return battleResult;
        }
        // Register the second user and start the battle
        else if (secondUser == null) {
            secondUser = newUser;
            Handler_Card cardHandler = new Handler_Card();
            CardDeck firstUserDeck = cardHandler.getUserDeck(firstUser);
            CardDeck secondUserDeck = cardHandler.getUserDeck(secondUser);
            battleResult = battle(firstUser, secondUser, firstUserDeck, secondUserDeck);
            isBusy = false;

            // Notify all waiting threads that the battle has finished
            synchronized (LOCK_OBJECT) {
                LOCK_OBJECT.notifyAll();
            }
            // user i par esht null
            firstUser = null;
            // user i dyte esht null
            secondUser = null;
            // dhe return qe nxjerr rezultatet e ndeshjes
            return battleResult;
        }
        // dhe nfund del return i barabarte me null
        return null;
    }

    /**
     * This method conducts a battle between two users.
     * @param player1 First user in the battle.
     * @param player2 Second user in the battle.
     * @param player1Deck CardDeck of the first user.
     * @param player2Deck CardDeck of the second user.
     * @return Battle log as a JSON string.
     */
    public String battle(User player1, User player2, CardDeck player1Deck, CardDeck player2Deck) {
        // Initialize battle log to null
        String battleLog = null;

        // Validate parameters
        if (player1 != null && player2 != null && player1Deck != null && player2Deck != null) {
            // Initialize ObjectMapper for JSON processing
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode battleLogArray = objectMapper.createArrayNode();
            int roundCounter = 0;

            // Run rounds until a user runs out of cards or roundCounter reaches 100
            do {
                ObjectNode roundLog = createRoundLog(objectMapper, player1, player2, player1Deck, player2Deck, ++roundCounter);
                battleLogArray.add(roundLog);
            } while (!player1Deck.isDeckEmpty() && !player2Deck.isDeckEmpty() && roundCounter < 100);

            try {
                // Convert the battle log to a JSON string
                battleLog = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(battleLogArray);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            // Update the users' records based on the battle outcome
            updateUsersAfterBattle(player1, player2, player1Deck, player2Deck);
        }

        // Reset users and battle result
        this.firstUser = null;
        this.secondUser = null;
        this.battleResult = null;

        return battleLog;
    }


    // Method to conduct a battle between two users


// Method to create a round log entry for the battle
        private ObjectNode createRoundLog (ObjectMapper objectMapper, User player1, User player2, CardDeck
        player1Deck, CardDeck player2Deck,int roundCounter){

            // Create an ObjectNode to store round log details
            ObjectNode roundLog = objectMapper.createObjectNode();

            // Select a random card from each player's deck
            Card card1 = player1Deck.pickRandomCard();
            Card card2 = player2Deck.pickRandomCard();

            // Calculate the damage each card can do to the other
            float card1Damage = damage(card1, card2);
            float card2Damage = damage(card2, card1);

            // Log round details in the round log
            logRoundDetails(roundLog, player1, player2, player1Deck, player2Deck, card1, card2, card1Damage, card2Damage, roundCounter);
            // Process the round outcome and update the card decks accordingly
            processRoundOutcome(player1Deck, player2Deck, card1, card2, card1Damage, card2Damage, roundLog);

            // Log the remaining size of each player's deck after the round
            roundLog.put("Deck Size 1 After", player1Deck.getDeckSize());
            roundLog.put("Deck Size 2 After", player2Deck.getDeckSize());

            // Return the populated round log
            return roundLog;
        }

// Method to log round details in the round log
        private void logRoundDetails (ObjectNode roundLog, User player1, User player2, CardDeck player1Deck, CardDeck
        player2Deck, Card card1, Card card2,float card1Damage, float card2Damage, int roundCounter){
            roundLog.put("Round", roundCounter);
            roundLog.put("User 1", player1.getName());
            roundLog.put("User 2", player2.getName());
            roundLog.put("Deck Size 1", player1Deck.getDeckSize());
            roundLog.put("Deck Size 2", player2Deck.getDeckSize());
            roundLog.put("CardID 1", card1.getId());
            roundLog.put("CardID 2", card2.getId());
            roundLog.put("Card Name 1", card1.getName());
            roundLog.put("Card Name 2", card2.getName());
            roundLog.put("Card Damage 1", card1Damage);
            roundLog.put("Card Damage 2", card2Damage);
        }

// Method to process the round outcome and update the card decks
        private void processRoundOutcome (CardDeck player1Deck, CardDeck player2Deck, Card card1, Card card2,
        float card1Damage, float card2Damage, ObjectNode roundLog){
            // Determine the winner of the round and update the card decks accordingly
            if (card1Damage > card2Damage) {
                // If card1's damage is greater, player1 is the winner of the round
                player2Deck.deleteCard(card2);
                player1Deck.insertCard(card2);
                // Log the winner's name in the round log
                roundLog.put("Winner: ", firstUser.getName());
            } else if (card1Damage < card2Damage) {
                // If card2's damage is greater, player2 is the winner of the round
                player1Deck.deleteCard(card1);
                player2Deck.insertCard(card1);
                roundLog.put("Winner: ", secondUser.getName());
            } else {
                roundLog.put("Winner: ", "Draw");
            }
        }

// Method to update users' records after the battle
        private void updateUsersAfterBattle (User player1, User player2, CardDeck player1Deck, CardDeck player2Deck){
            // Compare the size of the decks after the battle
            if (player1Deck.getDeckSize() > player2Deck.getDeckSize()) {
                player1.lose();
                player2.win();
            // If player2's deck is larger, player2 wins and player1 loses
            } else if (player2Deck.getDeckSize() > player1Deck.getDeckSize()) {
                player1.win();
                player2.lose();
            } else {
                player1.draw();
                player2.draw();
            }
        }

// Method to calculate damage between two cards
        public float damage (Card attacker, Card defender){
            if (isSpecialDamage(attacker)) {
                return specialDamage(attacker);
            }
            if (isNoDamage(attacker, defender)) {
                return 0;
            }
            if (isNegativeDamage(attacker, defender)) {
                return -1;
            }
            if (defender.getMonsterCategory() == MonsterCategory.Kraken) {
                return 0;
            }
            return calculateElementalDamage(attacker, defender);
        }

// Method to check if the card has special damage
        private boolean isSpecialDamage (Card card){
            return card.getMonsterCategory() == MonsterCategory.magicdice;
        }

// Method to calculate special damage for the card
        private float specialDamage (Card card){
            Random random = new Random();
            if (random.nextInt(6) > 3) {
                return 999;
            }
            return card.getDamage();
        }

// Method to check if there is no damage between two cards
        private boolean isNoDamage (Card attacker, Card defender){
            return attacker.getMonsterCategory() != MonsterCategory.Spell &&
                    defender.getMonsterCategory() != MonsterCategory.Spell &&
                    noDamageCases(attacker.getMonsterCategory(), defender.getMonsterCategory());
        }

// Method to check no damage cases between two monster categories
        private boolean noDamageCases (MonsterCategory attackerCategory, MonsterCategory defenderCategory){
            return (attackerCategory == MonsterCategory.Dragon && defenderCategory == MonsterCategory.FireElf) ||
                    (attackerCategory == MonsterCategory.Goblin && defenderCategory == MonsterCategory.Dragon) ||
                    (attackerCategory == MonsterCategory.Ork && defenderCategory == MonsterCategory.Wizard);
        }

// Method to check if there is negative damage between two cards
        private boolean isNegativeDamage (Card attacker, Card defender){
            return attacker.getMonsterCategory() == MonsterCategory.Knight &&
                    defender.getMonsterCategory() == MonsterCategory.Spell &&
                    defender.getElementType() == Element.Water;
        }

// Method to calculate elemental damage between two cards
        private float calculateElementalDamage (Card attacker, Card defender){
            // Get the base damage of the attacker
            float damage = attacker.getDamage();

            // Get the elemental type of the attacker and defender
            Element attackerElement = attacker.getElementType();
            Element defenderElement = defender.getElementType();

            // Check for element combinations where the attacker has an advantage
            // If the attacker has the advantage, the damage is doubled
            if (attackerElement == Element.Water && defenderElement == Element.Fire ||
                    attackerElement == Element.Normal && defenderElement == Element.Water ||
                    attackerElement == Element.Fire && defenderElement == Element.Normal) {
                return damage * 2;
            }

            // Check for element combinations where the defender has an advantage
            // If the defender has the advantage, the damage is halved
            if (attackerElement == Element.Water && defenderElement == Element.Normal ||
                    attackerElement == Element.Fire && defenderElement == Element.Water ||
                    attackerElement == Element.Normal && defenderElement == Element.Fire) {
                return damage / 2;
            }
            // If neither the attacker nor the defender have an elemental advantage, return the base damage
            return damage;
        }

// Method to fetch the scoreboard data
        public String fetchScoreboard () {
            try (Connection connection = DB.getInstance().getConnection()) {
                // Execute the scoreboard query and get the results as an ArrayNode
                ArrayNode scoreboardData = executeScoreboardQuery(connection);
                // Convert the ArrayNode of scoreboard data to a JSON string and return it
                return convertArrayNodeToJsonString(scoreboardData);
            } catch (SQLException | JsonProcessingException e) {
                // If a SQLException or JsonProcessingException is thrown, print the stack trace
                e.printStackTrace();
            }
            // If an exception was thrown, return null
            return null;
        }

// Method to execute the scoreboard query and return the results
        private ArrayNode executeScoreboardQuery (Connection connection) throws SQLException {
            String query = "SELECT name, wins, games, elo FROM users WHERE name IS NOT NULL ORDER BY elo DESC;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                return createScoreboardDataArray(resultSet);
            }
        }
// Method to create an array of scoreboard data from the query result set
            private ArrayNode createScoreboardDataArray (ResultSet resultSet) throws SQLException {
                // Here is created an ObjectMapper, which is used for converting between Java objects and JSON
                ObjectMapper objectMapper = new ObjectMapper();
                // Then is created an ArrayNode that holds the scoreboard data
                ArrayNode scoreboardArray = objectMapper.createArrayNode();
                while (resultSet.next()) {
                    // For each row, create a scoreboard entry as an ObjectNode
                    ObjectNode scoreboardEntry = createScoreboardEntry(objectMapper, resultSet);
                    // Add the scoreboard entry to the ArrayNode
                    scoreboardArray.add(scoreboardEntry);
                }
                // Return the ArrayNode of scoreboard data
                return scoreboardArray;
            }

// Method to create a scoreboard entry object from the result set
            private ObjectNode createScoreboardEntry (ObjectMapper objectMapper, ResultSet resultSet) throws
            SQLException {
                ObjectNode entry = objectMapper.createObjectNode();
                entry.put("Name", resultSet.getString(1));
                entry.put("Wins", resultSet.getString(2));
                entry.put("Games", resultSet.getString(3));
                entry.put("Elo", resultSet.getString(4));
                return entry;
            }

// Method to convert an ArrayNode to a JSON string
            private String convertArrayNodeToJsonString (ArrayNode arrayNode) throws JsonProcessingException {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
            }
}