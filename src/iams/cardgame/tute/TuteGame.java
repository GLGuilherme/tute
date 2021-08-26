package iams.cardgame.tute;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.CardModel.Suit;

public class TuteGame
{
    static final public int NUM_CARDS_PER_PLAYER = 8;
    
    final private Comparator<Card> cardValueComparator = new Comparator<Card>()
    {
        @Override
        public int compare(Card o1, Card o2)
        {
            int n = Integer.compare(
                    TuteGame.this.pinta != null && o1.suit == TuteGame.this.pinta.suit ? 10 : o1.suit.ordinal(), 
                    TuteGame.this.pinta != null && o2.suit == TuteGame.this.pinta.suit ? 10 : o2.suit.ordinal());
            
            if (n != 0)
                return n;
            
            return Integer.compare(o1.rank.relativeValue, o2.rank.relativeValue);
        }
    };
    
    enum Declaration
    {
        Player1,
        Player2;
    }
    
    final private TreeMap<Suit,Declaration> declarations = new TreeMap<>();
    
    final private ArrayList<Card> cardRasters  = new ArrayList<>();
    final private ArrayList<Card> deck         = new ArrayList<>();
    final private TreeSet<Card>   player1Cards = new TreeSet<>(this.cardValueComparator);
    final private TreeSet<Card>   player2Cards = new TreeSet<>(this.cardValueComparator);
    final private ArrayList<Card> player1Baza  = new ArrayList<>();
    final private ArrayList<Card> player2Baza  = new ArrayList<>();
    
    final private Collection<Card> unmodifiableCardRasters = Collections.unmodifiableList(this.cardRasters);
    final private Collection<Card> unmodifiableDeck = Collections.unmodifiableList(this.deck);
    final private Collection<Card> unmodifiablePlayer1Cards = Collections.unmodifiableSet(this.player1Cards);
    final private Collection<Card> unmodifiablePlayer2Cards = Collections.unmodifiableSet(this.player2Cards);
    final private Collection<Card> unmodifiablePlayer1Baza = Collections.unmodifiableList(this.player1Baza);
    final private Collection<Card> unmodifiablePlayer2Baza = Collections.unmodifiableList(this.player2Baza);

    private Card pinta = null;
    
    private boolean skipCardCount = false;
    private int player1Games = 0;
    private int player2Games = 0;
    private boolean player1Turn = false;
    private boolean player1Mano = true;
    
    private PrintStream out = null;

    public Collection<Card> getCardRasters()
    {
        return this.unmodifiableCardRasters;
    }

    public boolean isDeckEmpty()
    {
        return this.deck.isEmpty();
    }
    
    public Collection<Card> getDeck()
    {
        return this.unmodifiableDeck;
    }

    public Card getPinta()
    {
        return this.pinta;
    }

    public Collection<Card> getPlayer1Cards()
    {
        return this.unmodifiablePlayer1Cards;
    }

    public Collection<Card> getPlayer2Cards()
    {
        return this.unmodifiablePlayer2Cards;
    }

    public Collection<Card> getPlayer1Baza()
    {
        return this.unmodifiablePlayer1Baza;
    }

    public Collection<Card> getPlayer2Baza()
    {
        return this.unmodifiablePlayer2Baza;
    }

    public Card hasCard(Rank rank, Suit suit, Collection<Card> playerCards)
    {
        for (Card c : playerCards)
        {
            if (c.rank == rank && c.suit == suit)
                return c;
        }
        
        return null;
    }
    
    public int getPlayer1Games()
    {
        return this.player1Games;
    }

    public int getPlayer2Games()
    {
        return this.player2Games;
    }

    public boolean isPlayer1Turn()
    {
        return this.player1Turn;
    }

    public boolean isPlayer1Mano()
    {
        return this.player1Mano;
    }
    
    public Card getPlayer1CardChangeableByPinta()
    {
        return this.getCardChangeableByPinta(this.player1Cards);
    }

    public Card getPlayer2CardChangeableByPinta()
    {
        return this.getCardChangeableByPinta(this.player2Cards);
    }

    private Card getCardChangeableByPinta(Collection<Card> playerCards)
    {
        if (this.pinta.rank != CardModel.Rank.V2)
        {
            for (Card card : playerCards)
            {
                if (card.suit == this.pinta.suit)
                {
                    if (card.rank == CardModel.Rank.V7 &&
                       (this.pinta.rank == CardModel.Rank.V8 ||
                        this.pinta.rank == CardModel.Rank.V9 ||
                        this.pinta.rank == CardModel.Rank.Knave ||
                        this.pinta.rank == CardModel.Rank.Knight ||
                        this.pinta.rank == CardModel.Rank.King ||
                        this.pinta.rank == CardModel.Rank.V3 ||
                        this.pinta.rank == CardModel.Rank.Ace))
                    {
                        return card;
                    }
                        
                    if (card.rank == CardModel.Rank.V2 &&
                        (this.pinta.rank == CardModel.Rank.V4 ||
                         this.pinta.rank == CardModel.Rank.V5 ||
                         this.pinta.rank == CardModel.Rank.V6 ||
                         this.pinta.rank == CardModel.Rank.V7))
                    {
                        return card;
                    }
                }
            }
        }
        
        return null;
    }

    private boolean calculateIfFirstCardWins(Card card1, Card card2)
    {
        if ((card1.suit == this.pinta.suit) && (card2.suit == this.pinta.suit))
            return card1.rank.relativeValue > card2.rank.relativeValue;
        
        if ((card1.suit == this.pinta.suit) != (card2.suit == this.pinta.suit))
            return card1.suit == this.pinta.suit;
        
        if (card1.suit == card2.suit)
            return card1.rank.relativeValue > card2.rank.relativeValue;
        
        return true;
    }

    public boolean arePlayerCardsEmpty()
    {
        return this.player1Cards.isEmpty() && this.player2Cards.isEmpty();
    }

    public Declaration getDeclaration(Suit suit)
    {
        return this.declarations.get(suit);
    }

    public TuteGame()
    {
        ArrayList<CardModel> cardModels = CardModel.createDeck(true);
        
        for (CardModel cardModel : cardModels)
        {
            Card card = new Card(cardModel, Main.BOARD_WIDTH / 2, Main.BOARD_HEIGHT / 2, 0);
            
            this.cardRasters.add(card); 
                    
            this.deck.add(card);
        }
        
        this.shuffleDeck();
    }

    private void shuffleDeck()
    {
        if (this.deck.size() != this.cardRasters.size())
            throw new AssertionError();
        
        Collections.shuffle(this.deck, new java.util.Random());

        for (Card card : this.getDeck())
            card.moveToFront();

        this.pinta = this.deck.get(this.deck.size() - 1);

        this.pinta.moveToBack();

        if (this.out != null)
            this.out.println("SHUFFLE PINTA " + this.pinta); 
        
        if (this.out != null)
            this.out.println("TURN " + (this.player1Turn ? 1 : 2)); 
    }

    private Card changePintaPlayer(Card cardChangeableByPinta, TreeSet<Card> playerCards)
    {
        if (this.out != null)
            this.out.println("PLAYER " + (playerCards == this.player1Cards ? "1" : "2") + " CHANGES PINTA " + this.pinta + " " + cardChangeableByPinta); 
        
        if (!playerCards.remove(cardChangeableByPinta))
            throw new AssertionError();
        
        if (!playerCards.add(this.pinta))
            throw new AssertionError();
        
        if (!this.deck.remove(this.pinta))
            throw new AssertionError();
        
        if (!this.deck.add(cardChangeableByPinta))
            throw new AssertionError();
        
        Card previousPinta = this.pinta;
        
        this.pinta = cardChangeableByPinta;
        
        return previousPinta;
    }
    
    public Card changePintaPlayer1()
    {
        return changePintaPlayer(this.getCardChangeableByPinta(this.player1Cards), this.player1Cards);
    }

    public Card changePintaPlayer2()
    {
        return changePintaPlayer(this.getCardChangeableByPinta(this.player2Cards), this.player2Cards);
    }
    
    private Card dealCardToPlayer(TreeSet<Card> playerCards)
    {
        Card c1 = this.deck.remove(0);
        
        if (c1 == null)
            throw new AssertionError();
        
        if (this.out != null)
            this.out.println("DISPATCH " + (playerCards == this.player1Cards ? "1" : "2") + " " + c1); 
        
        playerCards.add(c1);
        
        return c1;
    }

    public Card dealCardToPlayer1()
    {
        return this.dealCardToPlayer(this.player1Cards);
    }

    public Card dealCardToPlayer2()
    {
        return this.dealCardToPlayer(this.player2Cards);
    }

    public void declare(Suit suit)
    {
        if (this.canMakeDeclarations())
        {
            if (this.player1Turn && 
                this.hasCard(Rank.King,   suit, this.player1Cards) != null && 
                this.hasCard(Rank.Knight, suit, this.player1Cards) != null)
            {
                this.declarations.put(suit, Declaration.Player1);
                return;
            }
    
            if (!this.player1Turn && 
                this.hasCard(Rank.King,   suit, this.player2Cards) != null && 
                this.hasCard(Rank.Knight, suit, this.player2Cards) != null)
            {
                this.declarations.put(suit, Declaration.Player2);
                return;
            }
        }
        
        throw new AssertionError();
    }
    
    public TreeSet<Card> calculateAllowedCardsToAvoidRenuncio(Card firstCard, Collection<Card> playerCards)
    {
        if (this.isDeckEmpty())
        {
            TreeSet<Card> alternatives = new TreeSet<>();
            
            for (Card c : playerCards)
            {
                if (firstCard.suit == c.suit 
                        && c.rank.relativeValue > firstCard.rank.relativeValue)
                {
                    alternatives.add(c);
                }
            }
    
            if (alternatives.isEmpty())
            {
                for (Card c : playerCards)
                {
                    if (firstCard.suit == c.suit)
                    {
                        alternatives.add(c);
                    }
                }
            }
            
            if (firstCard.suit != this.pinta.suit)
            {
                for (Card c : playerCards)
                {
                    if (this.pinta.suit == c.suit)
                    {
                        alternatives.add(c);
                    }
                }
            }
            
            if (!alternatives.isEmpty())
                return alternatives;
        }
        
        return new TreeSet<>(playerCards);
    }
    
    public boolean calculateIfRenuncio(Card firstCard, Card secondCard, Collection<Card> playerCards)
    {
        TreeSet<Card> allowedCards = 
                this.calculateAllowedCardsToAvoidRenuncio(firstCard, playerCards);
        
        return !allowedCards.contains(secondCard);
    }
    
    public boolean playCards(Card firstCard, Card secondCard)
    {
        if (this.out != null)
            this.out.println("THROW " + firstCard + " " + secondCard); 
        
        boolean firstCardWins = this.calculateIfFirstCardWins(firstCard, secondCard);
        
        boolean player1Wins = false;
        
        if (this.player1Turn)
        {
            if (this.calculateIfRenuncio(firstCard, secondCard, this.player2Cards))
                throw new AssertionError();
            
            if (!this.player1Cards.remove(firstCard))
                throw new AssertionError();
            
            if (!this.player2Cards.remove(secondCard))
                throw new AssertionError();
            
            player1Wins = firstCardWins;
        }
        else
        {
            if (this.calculateIfRenuncio(firstCard, secondCard, this.player1Cards))
                throw new AssertionError();

            if (!this.player2Cards.remove(firstCard))
                throw new AssertionError();
            
            if (!this.player1Cards.remove(secondCard))
                throw new AssertionError();
            
            player1Wins = !firstCardWins;
        }
        
        if (player1Wins)
        {
            this.player1Baza.add(firstCard);
            this.player1Baza.add(secondCard);
        }
        else
        {
            this.player2Baza.add(firstCard);
            this.player2Baza.add(secondCard);
        }
        
        this.player1Turn = player1Wins;

        if (this.out != null)
            this.out.println("TURN " + (this.player1Turn ? 1 : 2)); 

        return player1Wins;
    }

    public void countPointsAndRestart(boolean player1WinnedLastTrick)
    {
        if (!this.skipCardCount)
        {
            int player1Points = 0, player2Points = 0;
            
            for (Card card : this.getPlayer1Baza())
                player1Points += card.rank.countValue;
                
            for (Card card : this.getPlayer2Baza())
                player2Points += card.rank.countValue;
            
            if (player1WinnedLastTrick)
                player1Points += 10;
            else
                player2Points += 10;
            
            for (Map.Entry<Suit,Declaration> entry : this.declarations.entrySet())
            {
                int points = entry.getKey() == this.pinta.suit ? 40 : 20;
                
                if (entry.getValue() == Declaration.Player1)
                    player1Points += points;
    
                if (entry.getValue() == Declaration.Player2)
                    player2Points += points;
            }
            
            if (player1Points > player2Points)
                this.player1Games ++;
    
            else if (player1Points < player2Points)
                this.player2Games ++;
            
            if (this.out != null)
                this.out.println("POINTS " + player1Points + " " + player2Points); 
        }
            
        if (this.out != null)
            this.out.println("GAMES " + this.player1Games + " " + this.player2Games); 
        
        this.skipCardCount = false;
        
        this.deck.addAll(this.player1Cards);
        this.deck.addAll(this.player2Cards);
        this.deck.addAll(this.player1Baza);
        this.deck.addAll(this.player2Baza);
        
        this.declarations.clear();
        
        this.player1Cards.clear();
        this.player2Cards.clear();
        this.player1Baza.clear();
        this.player2Baza.clear();
        
        this.player1Mano = !this.player1Mano;
        this.player1Turn = !this.player1Mano;
        
        this.shuffleDeck();
    }

    public void declareRenuncio(Card firstCard, Card secondCard)
    {
        boolean renuncio = false;
        
        if (this.player1Turn)
        {
            renuncio = this.calculateIfRenuncio(firstCard, secondCard, this.player2Cards);
        }
        else
        {
            renuncio = this.calculateIfRenuncio(firstCard, secondCard, this.player1Cards);
        }
        
        if (this.player1Turn)
        {
            if (renuncio)
                this.player1Games += 2;
            else
                this.player2Games += 2;
        }
        else 
        {
            if (renuncio)
                this.player2Games += 2;
            else
                this.player1Games += 2;
        }

        this.skipCardCount = true;
    }
    
    public void declareTute()
    {
        if (this.player1Turn && 
                (this.canDeclareTute(Rank.Knight, this.player1Cards) ||
                 this.canDeclareTute(Rank.King,   this.player1Cards)))
        {
            this.player1Games ++;
            this.skipCardCount = true;
        }
        else if (!this.player1Turn &&
                (this.canDeclareTute(Rank.Knight, this.player2Cards) ||
                 this.canDeclareTute(Rank.King,   this.player2Cards)))
        {
            this.player2Games ++;
            this.skipCardCount = true;
        }
        else
        {
            throw new AssertionError();
        }
    }
    
    public boolean canMakeDeclarations()
    {
        return (!this.player1Baza.isEmpty() || !this.player2Baza.isEmpty()) && !this.deck.isEmpty();
    }
    
    public boolean canDeclareTute(Rank rank, Collection<Card> playerCards)
    {
        if (!this.canMakeDeclarations())
            return false;
            
        for (Suit suit : Suit.values())
        {
            if (this.hasCard(rank, suit, playerCards) == null)
                return false;
        }
        
        return true;
    }
}