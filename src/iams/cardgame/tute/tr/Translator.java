package iams.cardgame.tute.tr;

import java.util.Locale;

import iams.cardgame.tute.CardModel;
import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.CardModel.Suit;

abstract public class Translator
{
    abstract public String getWindowTitle();
    
    abstract public String getPlayerGamesString(int player1Games, int player2Games);
    
    abstract public String getPlayerPointsString(int playerPoints);
    
    abstract public String getRankName(Rank rank);
    
    abstract public String getSuitName(Suit suit);
    
    abstract public String getCardNameString(CardModel currentCard);

    abstract public String getPlus10DeMonteString();

    abstract public String getChangePintaString();

    abstract public String getTuteDeclarationString(Rank rank);

    abstract public String getPlusPointsString(int countValue);

    abstract public String getPlusTwentyFortyPointsString(int countValue, Suit suit);

    abstract public String getTwentyFortyDeclarationString(Suit pintaSuit, Suit declarationSuit);

    abstract public String getDeclareRenuncioString();

    abstract public String getMenuItemNames(String key);

    public static Translator get()
    {
        Locale currentLocale = Locale.getDefault();

        if (currentLocale.getLanguage().toLowerCase().equals("es"))
            return new TranslatorEs();
        else
            return new TranslatorPtBr();
    }

    
}