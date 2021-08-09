package iams.cardgame.tute.movement;

import iams.cardgame.tute.Card;
import iams.cardgame.tute.tr.Translator;

public class ThrowResponseMovement implements Movement
{
    final private Card currentCard;
    
    public ThrowResponseMovement(Card currentCard)
    {
        this.currentCard = currentCard;
    }

    public Card getCurrentCard()
    {
        return this.currentCard;
    }
    
    @Override
    public String toString(Translator tr)
    {
        return tr.getCardNameString(this.currentCard);
    }

}
