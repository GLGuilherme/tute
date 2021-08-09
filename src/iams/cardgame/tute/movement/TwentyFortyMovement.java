package iams.cardgame.tute.movement;

import iams.cardgame.tute.CardModel.Suit;
import iams.cardgame.tute.tr.Translator;

public class TwentyFortyMovement implements Movement
{
    final private Suit pintaSuit, suit;
    
    public TwentyFortyMovement(Suit pintaSuit, Suit suit)
    {
        this.pintaSuit = pintaSuit;
        this.suit = suit;
    }

    public Suit getSuit()
    {
        return this.suit;
    }

    @Override
    public String toString(Translator tr)
    {
        return tr.getTwentyFortyDeclarationString(this.pintaSuit, this.suit);
    }

}
