package iams.cardgame.tute.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import iams.cardgame.tute.Card;
import iams.cardgame.tute.TuteGamePlayerContext;
import iams.cardgame.tute.CardModel.Rank;

public class SmartTuteAI implements TuteAI
{
    final private TuteGamePlayerContext context;
    
    public SmartTuteAI(TuteGamePlayerContext context)
    {
        this.context = context;
    }

    @Override
    public Card calculatePlayerCardBegin()
    {
        return this.calculatePlayerCardBegin(this.context.getMyCards());
    }
    
    private Card calculatePlayerCardBegin(Collection<Card> myCards)
    {
        ArrayList<Card> candidateCardsToThrow = new ArrayList<>(); 

        if (candidateCardsToThrow.isEmpty())
        {
            if (this.context.isDeckEmpty())
            {
                if (candidateCardsToThrow.isEmpty())
                {
                    int maximumValue = 0;
                    
                    for (Card c : myCards)
                    {
                        if (c.suit != this.context.getPintaSuit())
                        {
                            if (c.rank.countValue == maximumValue)
                            {
                                candidateCardsToThrow.add(c);
                            }
                            else if (c.rank.countValue > maximumValue)
                            {
                                maximumValue = c.rank.countValue;
                                
                                candidateCardsToThrow.clear();
                                
                                candidateCardsToThrow.add(c);
                            }
                        }
                    }
                }
            }
            else
            {
                if (candidateCardsToThrow.isEmpty())
                {
                    int minimumValue = Rank.Ace.countValue;
                    
                    for (Card c : myCards)
                    {
                        if (c.suit != this.context.getPintaSuit())
                        {
                            if (c.rank.countValue == minimumValue)
                            {
                                candidateCardsToThrow.add(c);
                            }
                            else if (c.rank.countValue < minimumValue)
                            {
                                minimumValue = c.rank.countValue;
                                
                                candidateCardsToThrow.clear();
                                
                                candidateCardsToThrow.add(c);
                            }
                        }
                    }
                }
            }

            if (candidateCardsToThrow.isEmpty())
                candidateCardsToThrow.add(myCards.iterator().next());
            
        }

        return candidateCardsToThrow.get(ThreadLocalRandom.current().nextInt(candidateCardsToThrow.size()));
    }
    
    @Override
    public Card calculatePlayerCardResponse(Card thrownCard)
    {
        Collection<Card> myCards = this.context.calculateAllowedCardsToAvoidRenuncio(thrownCard);
        
        ArrayList<Card> candidateCardsToThrow = new ArrayList<>();
        
        if (thrownCard.rank.countValue == 0)
        {
            for (Card c : myCards)
            {
                if (c.suit != this.context.getPintaSuit() &&
                    c.suit == thrownCard.suit &&
                    (c.rank == Rank.Ace || c.rank == Rank.V3))
                {
                    candidateCardsToThrow.add(c);
                }
            }

            if (candidateCardsToThrow.isEmpty())
                return this.calculatePlayerCardBegin(myCards);
        }
        else
        {
            if (candidateCardsToThrow.isEmpty())
            {
                for (Card c : myCards)
                {
                    if (c.suit != this.context.getPintaSuit() &&
                        c.suit == thrownCard.suit &&
                        c.rank.countValue > thrownCard.rank.countValue &&
                        (c.rank == Rank.Ace || c.rank == Rank.V3))
                    {
                        candidateCardsToThrow.add(c);
                    }
                }
            }
            
            if (candidateCardsToThrow.isEmpty())
            {
                for (Card c : myCards)
                {
                    if (c.suit == thrownCard.suit &&
                        c.rank.countValue > thrownCard.rank.countValue)
                    {
                        candidateCardsToThrow.add(c);
                    }
                }
            }
            
            if (candidateCardsToThrow.isEmpty())
            {
                if (thrownCard.suit != this.context.getPintaSuit())
                {
                    for (Card c : myCards)
                    {
                        if (c.suit == this.context.getPintaSuit() &&
                            c.rank.countValue == 0)
                        {
                            candidateCardsToThrow.add(c);
                        }
                    }
                }
            }
        }
        
        if (candidateCardsToThrow.isEmpty())
            candidateCardsToThrow.add(myCards.iterator().next());
        
        return candidateCardsToThrow.get(ThreadLocalRandom.current().nextInt(candidateCardsToThrow.size()));
    }
}
