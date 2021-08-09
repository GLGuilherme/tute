package iams.cardgame.tute.movement;

import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.tr.Translator;

public class TuteMovement implements Movement
{
    final private Rank rank;
    
    public TuteMovement(Rank rank)
    {
        this.rank = rank;
    }

    public Rank getRank()
    {
        return this.rank;
    }

    @Override
    public String toString(Translator tr)
    {
        return tr.getTuteDeclarationString(this.rank);
    }

}
