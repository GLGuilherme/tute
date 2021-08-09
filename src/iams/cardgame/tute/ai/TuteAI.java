package iams.cardgame.tute.ai;

import iams.cardgame.tute.Card;

public interface TuteAI
{
    public Card calculatePlayerCardBegin();
    
    public Card calculatePlayerCardResponse(Card thrownCard);
}
