import java.io.*;
import java.util.*;

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

        int currentWinnerIndex = 0;

        for (int trick = 1; trick <= 5; trick++) {
            System.out.println("Trick " + trick + ":");
            List<Card> trickCards = new ArrayList<>();
            List<Card> currentHand;
            boolean deckExhausted = false;

            for (int i = 0; i < numPlayers; i++) {
                currentHand = playersHands.get(i);
                if (currentHand.isEmpty()) {
                    System.out.println("Player " + (i + 1) + " has no cards remaining.");
                    continue;
                }

                Card playedCard;
                if (i == currentWinnerIndex) {
                    playedCard = firstCard;
                } else {
                    playedCard = drawValidCard(currentHand, remainingCards, firstCard);
                }

                trickCards.add(playedCard);
                currentHand.remove(playedCard);

                System.out.println("Player " + (i + 1) + " plays: " + playedCard);
            }

            currentWinnerIndex = getTrickWinnerIndex(trickCards, firstCard.getSuit());
            System.out.println("Trick " + trick + " winner: Player " + (currentWinnerIndex + 1));
        }

        game.saveGame("saved_game.dat");
    }

    private static Card drawValidCard(List<Card> hand, List<Card> deck, Card leadCard) {
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.getSuit().equals(leadCard.getSuit()) || card.getRank().equals(leadCard.getRank())) {
                return hand.remove(i);
            }
        }
        // If no valid card is found, draw from the deck
        if (!deck.isEmpty()) {
            Card drawnCard = deck.remove(0);
            hand.add(drawnCard);
            System.out.println("Player drew a card: " + drawnCard);
            return drawnCard;
        } else {
            System.out.println("Deck is exhausted, cannot draw a card.");
            return null; // or return a placeholder card indicating inability to play
        }
    }

    private static boolean hasCardWithSuit(List<Card> hand, String suit) {
        for (Card card : hand) {
            if (card.getSuit().equals(suit)) {
                return true;
            }
        }
        return false;
    }

    private static int getTrickWinnerIndex(List<Card> trickCards, String suit) {
        int currentWinnerIndex = 0;
        Card currentHighestCard = trickCards.get(0);

        for (int i = 1; i < trickCards.size(); i++) {
            Card card = trickCards.get(i);
            if (card.getSuit().equals(suit)) {
                if (card.getRank().compareTo(currentHighestCard.getRank()) > 0) {
                    currentHighestCard = card;
                    currentWinnerIndex = i;
                }
            }
        }

        return currentWinnerIndex;
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
