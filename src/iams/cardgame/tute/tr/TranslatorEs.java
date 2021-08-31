package iams.cardgame.tute.tr;

import iams.cardgame.tute.CardModel;
import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.CardModel.Suit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TranslatorEs extends Translator
{
    @Override
    public String getWindowTitle()
    {
        return "Tute para dos jugadores";
    }

    @Override
    public String getPlayerPointsString(int playerPoints)
    {
        return "Puntos: " + playerPoints;
    }

    @Override
    public String getPlayerGamesString(int player1Games, int player2Games)
    {
        return "Juegos: " + player1Games + " - " + player2Games;
    }

    @Override
    public String getSuitName(Suit suit)
    {
        switch (suit)
        {
            case Batons: return "Bastos";
            case Cups: return "Copas";
            case Swords: return "Espadas";
            case Coins: return "Oros";
            default: throw new AssertionError();
        }
    }
    
    @Override
    public String getRankName(Rank rank)
    {
        switch (rank)
        {
            case Ace: return "As";
            case V2: return "Dos";
            case V3: return "Tres";
            case V4: return "Cuatro";
            case V5: return "Cinco";
            case V6: return "Seis";
            case V7: return "Siete";
            case V8: return "Ocho";
            case V9: return "Nueve";
            case Knave: return "Sota";
            case Knight: return "Caballo";
            case King: return "Rey";
            default: return rank.name();
        }
    }
    
    @Override
    public String getCardNameString(CardModel currentCard)
    {
        return this.getRankName(currentCard.rank) + " de " + this.getSuitName(currentCard.suit);
    }

    @Override
    public String getPlus10DeMonteString()
    {
        return "+10 de monte";
    }

    @Override
    public String getChangePintaString()
    {
        return "Cambio de pinta";
    }

    @Override
    public String getTuteDeclarationString(Rank rank)
    {
        return "Tute de " + (rank == Rank.King ? "Reyes" : "Caballos");
    }

    @Override
    public String getPlusPointsString(int countValue)
    {
        return "+" + countValue;
    }

    @Override
    public String getPlusTwentyFortyPointsString(int countValue, Suit suit)
    {
        return "+" + countValue + " de " + this.getSuitName(suit);
    }

    @Override
    public String getTwentyFortyDeclarationString(Suit pintaSuit, Suit declarationSuit)
    {
        if (pintaSuit == declarationSuit)
            return "Cuarenta de " + this.getSuitName(declarationSuit);
        else
            return "Veinte de " + this.getSuitName(declarationSuit);
    }

    @Override
    public String getDeclareRenuncioString()
    {
        return "Renuncio";
    }

    @Override
    public String getMenuItemNames(String key)
    {
        switch (key)
        {
            case "OPTIONS": return "Opciones";
            case "RESTART": return "Reiniciar";
            case "QUIT": return "Abandonar el juego";
            case "GAME": return "Juego";
            case "RULES": return "Normas";
            case "LANGUAGES": return "Idiomas";
            case "PORTUGUESE": return "Portugués (BR)";
            case "ENGLISH": return "Inglés";
            case "SPANISH": return "Español";
            default: throw new AssertionError();
        }
    }

    @Override
    public String getRulesText() throws IOException {
        Path fileName = Paths.get("iams/cardgame/tute/tr/Normas.txt");

        return Files.readString(fileName.toAbsolutePath(), StandardCharsets.UTF_8);
    }
}
