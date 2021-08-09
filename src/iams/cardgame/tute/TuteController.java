package iams.cardgame.tute;

import iams.cardgame.animators.MoveCardAnimator;
import iams.cardgame.animators.MoveToFrontAnimator;
import iams.cardgame.animators.MultiAnimator;
import iams.cardgame.animators.ReverseAnimator;
import iams.cardgame.animators.ThrowCardAnimator;

import java.util.Collection;

import javax.swing.JPanel;

import iams.cardgame.animators.AnimationController;

public class TuteController extends AnimationController
{
    static final private int SEPARATION = 40;

    public TuteController(JPanel panel)
    {
        super(panel);
    }

    Animator getDeckCardMove(Collection<Card> cards)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        for (Card card : cards)
        {
            m1.add(new MoveCardAnimator(card, 20 + CardModel.WIDTH / 2, Main.BOARD_HEIGHT / 2, 0));
        }
                
        return m1;
    }
    
    Animator getPintaMovement(Card card)
    {
        return new MoveCardAnimator(card, 40 + CardModel.HEIGHT / 2, Main.BOARD_HEIGHT / 2, 90);
    }

    Animator getPlayer1PintaCardThrow(Card card)
    {
        return new ThrowCardAnimator(card, 40 + 3 * CardModel.HEIGHT / 2, Main.BOARD_HEIGHT / 2 + CardModel.HEIGHT, 0);
    }
    
    Animator getPlayer2PintaCardThrow(Card card)
    {
        return new ThrowCardAnimator(card, 40 + 3 * CardModel.HEIGHT / 2, Main.BOARD_HEIGHT / 2 - CardModel.HEIGHT, 0);
    }
    
    Animator getPlayer1CardThrow(Card card)
    {
        return new ThrowCardAnimator(card, Main.BOARD_WIDTH / 2, Main.BOARD_HEIGHT - CardModel.HEIGHT, 0);
    }

    Animator getPlayer2PlayerCardThrow(Card card)
    {
        return new ThrowCardAnimator(card, Main.BOARD_WIDTH / 2, CardModel.HEIGHT, 0);
    }

    Animator getCenterCardThrow(Card card)
    {
        return new ThrowCardAnimator(card, Main.BOARD_WIDTH / 2, Main.BOARD_HEIGHT / 2, 0);
    }
    
    Animator getPlayer1CardMovement(Card card, int i, int numCards)
    {
        double centering = - (numCards - 1.) / 2 + i;
        
        MoveCardAnimator player1CardLocation = new MoveCardAnimator(card,
                Main.BOARD_WIDTH / 2 + SEPARATION * centering, 
                Main.BOARD_HEIGHT - (CardModel.HEIGHT / 2 + 30 + (30 * Math.cos(Math.PI / 8 * centering))), 
                5 * centering);
        
        return player1CardLocation;
    }

    Animator getPlayer2CardMovement(Card card, int i, int numCards)
    {
        double centering = - (numCards - 1.) / 2 + i;
        
        MoveCardAnimator player1CardLocation = new MoveCardAnimator(card,
                Main.BOARD_WIDTH / 2 - SEPARATION * centering, 
                CardModel.HEIGHT / 2 + 30 + (30 * Math.cos(Math.PI / 8 * centering)), 
                180 + 5 * centering);
                
        return player1CardLocation;
    }

    Animator relocatePlayer1Cards(Collection<Card> player1)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        int i = 0;
        
        for (Card card : player1)
        {
            m1.add(new MoveToFrontAnimator(card));
            
            m1.add(this.getPlayer1CardMovement(card, i ++, player1.size()));
        }
                
        return m1;
    }

    Animator relocatePlayer2Cards(Collection<Card> player2)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        int i = 0;
        
        for (Card card : player2)
        {
            m1.add(new MoveToFrontAnimator(card));
            
            m1.add(this.getPlayer2CardMovement(card, i ++, player2.size()));

        }
                
        return m1;
    }

    public Animator getPlayer1WinDeckMovement(Card card1, Card card2)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        m1.add(new ThrowCardAnimator(card1, Main.BOARD_WIDTH, Main.BOARD_HEIGHT - CardModel.HEIGHT, 0));
        m1.add(new ThrowCardAnimator(card2, Main.BOARD_WIDTH, Main.BOARD_HEIGHT - CardModel.HEIGHT, 0));
        
        m1.add(new ReverseAnimator(card1, false));
        m1.add(new ReverseAnimator(card2, false));
        
        return m1;
    }

    public Animator getPlayer2WinDeckMovement(Card card1, Card card2)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        m1.add(new ThrowCardAnimator(card1, Main.BOARD_WIDTH, CardModel.HEIGHT, 0));
        m1.add(new ThrowCardAnimator(card2, Main.BOARD_WIDTH, CardModel.HEIGHT, 0));
        
        m1.add(new ReverseAnimator(card1, false));
        m1.add(new ReverseAnimator(card2, false));
        
        return m1;
    }

    public Animator getCenterWinCardThrow(Collection<Card> cards)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        for (Card card : cards)
        {
            m1.add(new MoveCardAnimator(card, 
                    card.getX() - Main.BOARD_WIDTH / 2, 
                    card.getY(), card.getRotation()));
        }
                
        return m1;
    }

    Animator getCenterCardMove(Collection<Card> cards)
    {
        MultiAnimator m1 = new MultiAnimator();
        
        for (Card card : cards)
        {
            m1.add(new MoveCardAnimator(card, Main.BOARD_WIDTH / 2, Main.BOARD_HEIGHT / 2, 0));
        }
                
        return m1;
    }
}
