package iams.cardgame.tute.tr;

import iams.cardgame.tute.CardModel;
import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.CardModel.Suit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TranslatorPtBr extends Translator
{
    @Override
    public String getWindowTitle()
    {
        return "Tute para dois jogadores";
    }

    @Override
    public String getPlayerPointsString(int playerPoints)
    {
        return "Pontos: " + playerPoints;
    }

    @Override
    public String getPlayerGamesString(int player1Games, int player2Games)
    {
        return "Jogos: " + player1Games + " - " + player2Games;
    }

    @Override
    public String getSuitName(Suit suit)
    {
        switch (suit)
        {
            case Batons: return "Paus";
            case Cups: return "Copas";
            case Swords: return "Espadas";
            case Coins: return "Ouros";
            default: throw new AssertionError();
        }
    }

    @Override
    public String getRankName(Rank rank)
    {
        switch (rank)
        {
            case Ace: return "Ás";
            case King: return "Rei";
            case Knight: return "Cavalo";
            case Knave: return "Valete";
            default: return rank.name().replaceAll("^V", "");
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
        return "+10 bônus para o vencedor da última rodada!";
    }

    @Override
    public String getChangePintaString()
    {
        return "Troca de carta pelo coringa!";
    }

    @Override
    public String getTuteDeclarationString(Rank rank)
    {
        return "Tute (" + (rank == Rank.King ? "Reis" : "Cavalos") + ")";
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
            return "Quarenta!";
        else
            return "Vinte (" + this.getSuitName(declarationSuit) + ")";
    }

    @Override
    public String getDeclareRenuncioString()
    {
        return "Renúncio";
    }

    @Override
    public String getMenuItemNames(String key)
    {
        switch (key)
        {
            case "OPTIONS": return "Opções";
            case "RESTART": return "Reiniciar";
            case "QUIT": return "Sair do jogo";
            case "GAME": return "Jogo";
            case "RULES": return "Regras";
            case "LANGUAGES": return "Idioma";
            case "PORTUGUESE": return "Português (PT-BR)";
            case "ENGLISH": return "Inglês";
            case "SPANISH": return "Espanhol";
            default: throw new AssertionError();
        }
    }

    @Override
    public String getRulesText() throws IOException {
        return Files.readString(Path.of("./Regras.txt"), StandardCharsets.US_ASCII);
    }
}
