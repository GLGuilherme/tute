package iams.cardgame.tute.movement;

import iams.cardgame.tute.tr.Translator;

public class PintaMovement implements Movement
{
    @Override
    public String toString(Translator tr)
    {
        return tr.getChangePintaString();
    }
}
