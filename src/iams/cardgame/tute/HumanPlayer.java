package iams.cardgame.tute;

import java.awt.Color;
import java.util.ArrayList;

import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.movement.Movement;
import iams.cardgame.tute.movement.PintaMovement;
import iams.cardgame.tute.movement.ThrowMovement;
import iams.cardgame.tute.movement.ThrowResponseMovement;
import iams.cardgame.tute.movement.TuteMovement;
import iams.cardgame.tute.movement.TwentyFortyMovement;
import iams.cardgame.tute.tr.Translator;

public class HumanPlayer
{
    final private Translator tr;
    final private TuteGame game;
    
    private boolean waitForUserClick = false;

    public String player1Message = null;

    private Card hoverCard = null;
    final private ArrayList<Card> otherCurrentCards = new ArrayList<>();
    
    public HumanPlayer(TuteGame game, Translator tr)
    {
        this.tr = tr;
        this.game = game;
    }

    public void onMouseOver(Card card, boolean over)
    {
        if (over)
        {
            if (this.hoverCard == card)
                return;

            this.clearSelection();

            this.hoverCard = card;
        }
        else if (this.hoverCard != null)
        {
            this.clearSelection();

            this.hoverCard = null;
        }

        this.onMouseOver();
    }
    
    private void onMouseOver()
    {      
        if (this.getPlayerMovement() != null)
            this.player1Message = this.getPlayerMovement().toString(this.tr);
        else
            this.player1Message = null;
    }
    
    public Movement getPlayerMovement()
    {
        this.player1Message = null;

        if (!this.waitForUserClick)
            return null;

        if (this.hoverCard != null)
        {
            if (this.game.isPlayer1Turn())
            {
                if (this.game.canMakeDeclarations())
                {
                    if (this.hoverCard == this.game.getPinta() && this.game.getPlayer1CardChangeableByPinta() != null)
                    {
                        this.hoverCard.setHighlightColor(Color.green);
                        
                        return new PintaMovement();
                    }
                }
                
                if (this.game.getPlayer1Cards().contains(this.hoverCard))
                {
                    if (this.game.canMakeDeclarations())
                    {
                        if (this.hoverCard.rank == Rank.King || this.hoverCard.rank == Rank.Knight)
                        {
                            if (this.game.canDeclareTute(this.hoverCard.rank, this.game.getPlayer1Cards()))
                            {
                                for (Card c : this.game.getPlayer1Cards())
                                {
                                    if (c.rank == this.hoverCard.rank)
                                    {
                                        this.otherCurrentCards.add(c);
                                        
                                        c.setHighlightColor(Color.cyan);
                                    }
                                }
    
                                return new TuteMovement(this.hoverCard.rank);
                            }
                        }
                        
                        if (this.hoverCard.rank == Rank.King &&
                            this.game.getDeclaration(this.game.getPinta().suit) == null &&
                            this.game.getDeclaration(this.hoverCard.suit) == null)
                        {
                            Card knight = this.game.hasCard(Rank.Knight, this.hoverCard.suit, this.game.getPlayer1Cards());
                            
                            if (knight != null)
                            {
                                this.otherCurrentCards.add(knight);
                                
                                this.hoverCard.setHighlightColor(Color.cyan);
                                
                                knight.setHighlightColor(Color.cyan);
    
                                return new TwentyFortyMovement(this.game.getPinta().suit, this.hoverCard.suit);
                            }
                        }
                    }

                    this.hoverCard.setHighlightColor(Color.yellow);
                
                    return new ThrowMovement(this.hoverCard);
                }
            }
            else if (this.game.getPlayer1Cards().contains(this.hoverCard))
            {
//                if (this.game.calculateIfRenuncio(this.player2playedCard, this.hoverCard, this.game.getPlayer1Cards()))
 //                   this.hoverCard.setHighlightColor(Color.pink);
   //             else
                    this.hoverCard.setHighlightColor(Color.yellow);
                
                return new ThrowResponseMovement(this.hoverCard);
            }
        }
        
        return null;
    }

    public void clearSelection()
    {
        if (this.hoverCard != null)
            this.hoverCard.setHighlightColor(null);
        
        this.hoverCard = null;
        
        for (Card c : this.otherCurrentCards)
            c.setHighlightColor(null);
        
        this.otherCurrentCards.clear();
    }

    public void fireWaitForUserClick(boolean b)
    {
        this.waitForUserClick = b;

        if (this.waitForUserClick)
            this.onMouseOver();
    }
}
