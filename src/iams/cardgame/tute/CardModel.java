package iams.cardgame.tute;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CardModel implements Comparable<CardModel>
{
    public static final int HEIGHT = 123;
    public static final int WIDTH = 80;

    public enum Suit
    {
        Coins, 
        Cups, 
        Swords, 
        Batons;
    }
    
    public enum Rank
    {
        Ace    (12, 11), 
        V2     ( 1,  0), 
        V3     (11, 10), 
        V4     ( 2,  0), 
        V5     ( 3,  0), 
        V6     ( 4,  0), 
        V7     ( 5,  0), 
        V8     ( 6,  0), 
        V9     ( 7,  0),
        Knave  ( 8,  2), 
        Knight ( 9,  3), 
        King   (10,  4),
        ;
        
        final public int relativeValue;
        final public int countValue;
        
        Rank(int relativeValue, int countValue)
        {
            this.relativeValue = relativeValue;
            this.countValue = countValue;
        }
    }

    final public Suit suit;
    final public Rank rank;
    final public BufferedImage image;
    
    public CardModel(Suit suit, Rank rank, BufferedImage image)
    {
        this.suit = suit;
        this.rank = rank;
        this.image = image;
    }
    
    public String toString()
    {
        return this.rank.name().replaceAll("^V", "") + "-" + this.suit.name();
    }
    
    static final private BufferedImage SOURCE_IMAGE;
    
    static final public BufferedImage BACK;
    
    static 
    {
        try
        {
            SOURCE_IMAGE = ImageIO.read(
                    CardModel.class.getResourceAsStream("spanish-deck.png"));
            
            BACK = SOURCE_IMAGE.getSubimage(WIDTH * (Rank.values().length), 0, WIDTH, HEIGHT);
        }
        catch (IOException e)
        {
            throw new AssertionError(e);
        }
    }
    
    static ArrayList<CardModel> createDeck(boolean skip8And9)
    {
        ArrayList<CardModel> deck = new ArrayList<>();
        
        for (Suit suit : Suit.values())
        {
            for (Rank rank : Rank.values())
            {
                if (skip8And9 && (rank == Rank.V8 || rank == Rank.V9))
                    continue;
                
                deck.add(new CardModel(suit, rank, 
                    SOURCE_IMAGE.getSubimage(WIDTH * rank.ordinal(), 
                                             HEIGHT * suit.ordinal(), WIDTH, HEIGHT)));
            }
        }
        
        return deck;
    }

    @Override
    public int compareTo(CardModel o)
    {
        if (this.suit != o.suit)
            return this.suit.compareTo(o.suit);

        return this.rank.compareTo(o.rank);
    }
}
