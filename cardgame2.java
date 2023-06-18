import java.io.*;
import java.util.*;
import javax.swing.*;

public class cardgame2 implements Serializable {
    private List<Card> deck;

    public cardgame2() {
        deck = new ArrayList<>();
    }

    public void generateDeck() {
        String[] suits = {"c", "d", "h", "s"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "J", "Q", "K"};

        for (String suit : suits) {
            for (String rank : ranks) {
                Card card = new Card(rank, suit);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public List<Card> getDeck() {
        return deck;
    }

    public List<List<Card>> distributeCards(List<Card> deck, int numPlayers, int numCards) {
        List<List<Card>> playersHands = new ArrayList<>();

        for (int i = 0; i < numPlayers; i++) {
            List<Card> hand = new ArrayList<>();
            for (int j = 0; j < numCards; j++) {
                hand.add(deck.remove(0));
            }
            playersHands.add(hand);
        }

        return playersHands;
    }

    public void saveGame(String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public static cardgame2 loadGame(String fileName) {
        cardgame2 game = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            game = (cardgame2) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Game loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
        return game;
    }

    public static void main(String[] args) {
        cardgame2 game;

        // Check if a saved game file exists
        File savedGameFile = new File("saved_game.dat");
        if (savedGameFile.exists()) {
            // Load the saved game
            game = cardgame2.loadGame("saved_game.dat");
        } else {
            // Create a new game
            game = new cardgame2();
            game.generateDeck();
            game.shuffleDeck();
        }

        List<Card> gameDeck = game.getDeck();

        int numPlayers = 4;
        int numCardsPerPlayer = 7;

        List<List<Card>> playersHands;
        if (savedGameFile.exists()) {
            // Continue from saved game
            playersHands = game.distributeCards(gameDeck, numPlayers, numCardsPerPlayer);
        } else {
            // Start a new game
            playersHands = game.distributeCards(gameDeck, numPlayers, numCardsPerPlayer);
        }

        for (int i = 0; i < playersHands.size(); i++) {
            System.out.println("Player " + (i + 1) + "'s Hand: " + playersHands.get(i));
        }

        Card firstCard = gameDeck.get(0);

        System.out.println("Center: " + firstCard.toString());

        List<Card> remainingCards = gameDeck.subList(1, gameDeck.size());

        System.out.println("Deck: " + remainingCards);

        String firstPlayer = "";
        switch (firstCard.getRank()) {
            case "A":
            case "5":
            case "9":
            case "K":
                firstPlayer = "Player 1";
                break;
            case "2":
            case "6":
                firstPlayer = "Player 2";
                break;
            case "3":
            case "7":
            case "J":
                firstPlayer = "Player 3";
                break;
            case "4":
            case "8":
            case "Q":
                firstPlayer = "Player 4";
                break;
        }
        System.out.println("Turn: " + firstPlayer);

        int currentPlayerIndex = 0;
        Card highestCard = firstCard;
        String trickWinner = firstPlayer;

        while (true) {
            System.out.println("\nNext Player Turn");

            List<Card> currentPlayerHand = playersHands.get(currentPlayerIndex);

            Card playableCard = null;
            for (Card card : currentPlayerHand) {
                if (isPlayableCard(card, highestCard)) {
                    if (playableCard == null || getCardValue(card.getRank()) > getCardValue(playableCard.getRank())) {
                        playableCard = card;
                    }
                }
            }

            if (playableCard != null) {
                currentPlayerHand.remove(playableCard);
                highestCard = playableCard;
                trickWinner = "Player " + (currentPlayerIndex + 1);
                System.out.println("Player " + (currentPlayerIndex + 1) + " plays: " + playableCard.toString());
            } else {
                System.out.println("Player " + (currentPlayerIndex + 1) + " draws a card and skips the turn");

                currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;

                if (currentPlayerIndex == 0) {
                    // Save the game
                    game.saveGame("saved_game.dat");
                    break;
                }

                continue;
            }

            if (currentPlayerIndex == 3) {
                trickWinner = "Player " + (currentPlayerIndex + 1);
                highestCard = playableCard;
                break;
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
        }

        System.out.println("\nTrick ended!");
        System.out.println("Trick winner: " + trickWinner + " with the highest card: " + highestCard.toString());

        System.out.println("\nStarting a next trick with " + trickWinner + " as the first player");

        int winnerIndex = Integer.parseInt(trickWinner.split(" ")[1]) - 1;

        Collections.rotate(playersHands, -winnerIndex);

        highestCard = gameDeck.get(0);

        currentPlayerIndex = 0;

        while (true) {
            System.out.println("\nNext Player Turn");

            List<Card> currentPlayerHand = playersHands.get(currentPlayerIndex);

            // Check if player has a valid card to play
            Card playableCard = null;
            for (Card card : currentPlayerHand) {
                if (isPlayableCard(card, highestCard)) {
                    if (playableCard == null || getCardValue(card.getRank()) > getCardValue(playableCard.getRank())) {
                        playableCard = card;
                    }
                }
            }

            if (playableCard != null) {
                currentPlayerHand.remove(playableCard);
                highestCard = playableCard;
                trickWinner = "Player " + (currentPlayerIndex + 1);
                System.out.println("Player " + (currentPlayerIndex + 1) + " plays: " + playableCard.toString());
            } else {
                System.out.println("Player " + (currentPlayerIndex + 1) + " draws a card and skips the turn");

                currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;

                if (currentPlayerIndex == 0) {
                    // Save the game
                    game.saveGame("saved_game.dat");
                    break;
                }

                continue;
            }

            if (currentPlayerIndex == 3) {
                trickWinner = "Player " + (currentPlayerIndex + 1);
                highestCard = playableCard;
                break;
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
        }

        System.out.println("\nTrick ended!");
        System.out.println("Trick winner: " + trickWinner + " with the highest card: " + highestCard.toString());
    }

    public static boolean isPlayableCard(Card card, Card centerCard) {
        String cardRank = card.getRank();
        String centerRank = centerCard.getRank();

        if (cardRank.equals("2") || cardRank.equals("J")) {
            return true;
        }

        if (cardRank.equals("A") && (centerRank.equals("A") || centerRank.equals("K"))) {
            return true;
        }

        if (cardRank.equals("K") && centerRank.equals("A")) {
            return true;
        }

        if (cardRank.equals("5") && (centerRank.equals("5") || centerRank.equals("9"))) {
            return true;
        }

        if (cardRank.equals("9") && centerRank.equals("5")) {
            return true;
        }

        if (cardRank.equals("Q") && centerRank.equals("8")) {
            return true;
        }

        return false;
    }

    public static int getCardValue(String rank) {
        switch (rank) {
            case "A":
                return 14;
            case "K":
                return 13;
            case "Q":
                return 12;
            case "J":
                return 11;
            case "9":
                return 9;
            case "8":
                return 8;
            case "7":
                return 7;
            case "6":
                return 6;
            case "5":
                return 5;
            case "4":
                return 4;
            case "3":
                return 3;
            case "2":
                return 2;
            default:
                return 0;
        }
    }
}

class Card implements Serializable {
    private String rank;
    private String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + suit;
    }
}
