
const TranslatorEs =
{
    getWindowTitle: function()
    {
        return "Tute para dos jugadores";
    },

    getPlayerPointsString: function(playerPoints)
    {
        return "Puntos: " + playerPoints;
    },

    getPlayerGamesString: function(player1Games, player2Games)
    {
        return "Juegos: " + player1Games + " - " + player2Games;
    },

    getSuitName: function(suit)
    {
        switch (suit.name)
        {
            case "Batons": return "Bastos";
            case "Cups":   return "Copas";
            case "Swords": return "Espadas";
            case "Coins":  return "Oros";
            default: throw new AssertionError();
        }
    },
    
	getRankName: function(rank)
    {
        switch (rank.name)
        {
            case "Ace":    return "As";
            case "V2":     return "Dos";
            case "V3":     return "Tres";
            case "V4":     return "Cuatro";
            case "V5":     return "Cinco";
            case "V6":     return "Seis";
            case "V7":     return "Siete";
            case "V8":     return "Ocho";
            case "V9":     return "Nueve";
            case "Knave":  return "Sota";
            case "Knight": return "Caballo";
            case "King":   return "Rey";
            default: throw new AssertionError();
        }
    },
    
    getCardNameString: function(currentCard)
    {
        return this.getRankName(currentCard.rank) + " de " + this.getSuitName(currentCard.suit);
    },

    getPlus10DeMonteString: function()
    {
        return "+10 de monte";
    },

    getChangePintaString: function()
    {
        return "Cambio de pinta";
    },

    getTuteDeclarationString: function(rank)
    {
        return "Tute de " + (rank.name == "King" ? "Reyes" : "Caballos");
    },

    getPlusPointsString: function(countValue)
    {
        return "+" + countValue;
    },

    getPlusTwentyFortyPointsString: function(countValue, suit)
    {
        return "+" + countValue + " de " + TranslatorEs.getSuitName(suit);
    },

    getTwentyFortyDeclarationString: function(pintaSuit, declarationSuit)
    {
        if (pintaSuit == declarationSuit)
            return "Cuarenta de " + TranslatorEs.getSuitName(declarationSuit);
        else
            return "Veinte de " + TranslatorEs.getSuitName(declarationSuit);
    },

    getDeclareRenuncioString: function()
    {
        return "Renuncio";
    }

}

export const Tr = TranslatorEs;
