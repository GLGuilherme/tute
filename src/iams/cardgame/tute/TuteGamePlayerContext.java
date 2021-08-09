package iams.cardgame.tute;

import java.util.Collection;
import java.util.TreeSet;

import iams.cardgame.tute.CardModel.Suit;
import iams.cardgame.tute.TuteGame.Declaration;

public class TuteGamePlayerContext
{
    final private TuteGame game;
    final private boolean player1;
    
    public TuteGamePlayerContext(TuteGame game, boolean player1)
    {
        this.game = game;
        this.player1 = player1;
    }
    
    public Collection<Card> getMyCards()
    {
        return this.player1 ? this.game.getPlayer1Cards() : this.game.getPlayer2Cards();
    }

    public TreeSet<Card> calculateAllowedCardsToAvoidRenuncio(Card firstCard)
    {
        return this.game.calculateAllowedCardsToAvoidRenuncio(firstCard, this.getMyCards());
    }
    
    public Suit getPintaSuit()
    {
        return this.game.getPinta().suit;
    }

    public boolean isDeckEmpty()
    {
        return this.game.isDeckEmpty();
    }

    public Declaration getDeclarations(Suit suit)
    {
        return this.game.getDeclaration(suit);
    }
}
