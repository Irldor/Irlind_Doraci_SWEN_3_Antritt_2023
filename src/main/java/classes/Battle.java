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
                        // Treat an interrupt as an exit request
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
            firstUser = null;
            secondUser = null;
            return battleResult;
        }
        return null;
    }

    // Method to conduct a battle between two users
    public String battle(User player1, User player2, CardDeck player1Deck, CardDeck player2Deck) {
        // Check if the parameters are valid
        if (player1 == null || player2 == null || player1Deck == null || player2Deck == null) {
            this.firstUser = null;
            this.secondUser = null;
            this.battleResult = null;
            return null;
        }

        int roundCounter = 0;
        try {
            // Initialize ObjectMapper for JSON processing
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode battleLogArray = objectMapper.createArrayNode();

            // Run rounds until a user runs out of cards or roundCounter reaches 100
            while (!player1Deck.isDeckEmpty() && !player2Deck.isDeckEmpty() && ++roundCounter <= 100) {
                ObjectNode roundLog = createRoundLog(objectMapper, player1, player2, player1Deck, player2Deck, roundCounter);
                battleLogArray.add(roundLog);
            }

            // Convert the battle log to a JSON string
            String battleLog = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(battleLogArray);
            // Update the users' records based on the battle outcome
            updateUsersAfterBattle(player1, player2, player1Deck, player2Deck);
            return battleLog;

        } catch (JsonProcessingException e) {
            {
                e.printStackTrace();
            }
            return null;
        }
    }

// Method to create a round log entry for the battle
        private ObjectNode createRoundLog (ObjectMapper objectMapper, User player1, User player2, CardDeck
        player1Deck, CardDeck player2Deck,int roundCounter){
            ObjectNode roundLog = objectMapper.createObjectNode();
            Card card1 = player1Deck.pickRandomCard();
            Card card2 = player2Deck.pickRandomCard();
            float card1Damage = calculateDamage(card1, card2);
            float card2Damage = calculateDamage(card2, card1);

            // Log round details in the round log
            logRoundDetails(roundLog, player1, player2, player1Deck, player2Deck, card1, card2, card1Damage, card2Damage, roundCounter);
            // Process the round outcome and update the card decks accordingly
            processRoundOutcome(player1Deck, player2Deck, card1, card2, card1Damage, card2Damage, roundLog);

            roundLog.put("Deck Size 1 After", player1Deck.getDeckSize());
            roundLog.put("Deck Size 2 After", player2Deck.getDeckSize());

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
                player2Deck.deleteCard(card2);
                player1Deck.insertCard(card2);
                roundLog.put("Winner: ", firstUser.getName());
            } else if (card1Damage < card2Damage) {
                player1Deck.deleteCard(card1);
                player2Deck.insertCard(card1);
                roundLog.put("Winner: ", secondUser.getName());
            } else {
                roundLog.put("Winner: ", "Draw");
            }
        }

// Method to update users' records after the battle
        private void updateUsersAfterBattle (User player1, User player2, CardDeck player1Deck, CardDeck player2Deck){
            if (player1Deck.getDeckSize() > player2Deck.getDeckSize()) {
                player1.lose();
                player2.win();
            } else if (player2Deck.getDeckSize() > player1Deck.getDeckSize()) {
                player1.win();
                player2.lose();
            } else {
                player1.draw();
                player2.draw();
            }
        }

// Method to calculate damage between two cards
        public float calculateDamage (Card attacker, Card defender){
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
            float damage = attacker.getDamage();
            Element attackerElement = attacker.getElementType();
            Element defenderElement = defender.getElementType();

            if (attackerElement == Element.Water && defenderElement == Element.Fire ||
                    attackerElement == Element.Normal && defenderElement == Element.Water ||
                    attackerElement == Element.Fire && defenderElement == Element.Normal) {
                return damage * 2;
            }
            if (attackerElement == Element.Water && defenderElement == Element.Normal ||
                    attackerElement == Element.Fire && defenderElement == Element.Water ||
                    attackerElement == Element.Normal && defenderElement == Element.Fire) {
                return damage / 2;
            }
            return damage;
        }

// Method to fetch the scoreboard data
        public String fetchScoreboard () {
            try (Connection connection = DB.getInstance().getConnection()) {
                ArrayNode scoreboardData = executeScoreboardQuery(connection);
                return convertArrayNodeToJsonString(scoreboardData);
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            }
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
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayNode scoreboardArray = objectMapper.createArrayNode();
                while (resultSet.next()) {
                    ObjectNode scoreboardEntry = createScoreboardEntry(objectMapper, resultSet);
                    scoreboardArray.add(scoreboardEntry);
                }
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