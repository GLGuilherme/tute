package iams.cardgame.tute.ai;

import iams.cardgame.tute.Card;
import iams.cardgame.tute.TuteGame;
import iams.cardgame.tute.TuteGamePlayerContext;
import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.CardModel.Suit;

public class AITester
{
    static public void main(String[] args)
    {
        final int TOTAL_GAMES = 20000;
        
        TuteGame game = new TuteGame();
        
        TuteAI player1 = new SmartTuteAI(new TuteGamePlayerContext(game, true));
        
        TuteAI player2 = new BasicTuteAI(new TuteGamePlayerContext(game, false));
        
        for (int g = 0; g < TOTAL_GAMES; g ++)
        {
            for (int i = 0; i < TuteGame.NUM_CARDS_PER_PLAYER; i ++)
            {
                if (game.isPlayer1Mano())
                {
                    game.dealCardToPlayer2();
                    game.dealCardToPlayer1();
                }
                else
                {
                    game.dealCardToPlayer1();
                    game.dealCardToPlayer2();
                }
            }
            
            boolean player1WonLastTrick = false;
            
            while (!game.arePlayerCardsEmpty())
            {
                if (game.canMakeDeclarations())
                {
                    if (game.isPlayer1Turn())
                    {
                        Card player1CardChangeableByPinta = game.getPlayer1CardChangeableByPinta();
                        
                        if (player1CardChangeableByPinta != null)
                        {
                            game.changePintaPlayer1();
                            
                            continue;
                        }
                        
                        for (Suit declarationSuit : Suit.values())
                        {
                            if (game.getDeclaration(declarationSuit) != null)
                                continue;
                            
                            Card knight = game.hasCard(Rank.Knight, declarationSuit, game.getPlayer1Cards());
                            Card king   = game.hasCard(Rank.King,   declarationSuit, game.getPlayer1Cards());
                            
                            if (knight != null && king != null)
                            {   
                                game.declare(declarationSuit);
                                
                                continue;
                            }
                        }
                        
                        if (game.canDeclareTute(Rank.King, game.getPlayer1Cards()) ||
                            game.canDeclareTute(Rank.Knight, game.getPlayer1Cards()))
                        {
                            game.declareTute();
                            break;
                        }
                    }
                    else
                    {
                        Card player2CardChangeableByPinta = game.getPlayer2CardChangeableByPinta();
                        
                        if (player2CardChangeableByPinta != null)
                        {
                            game.changePintaPlayer2();
                        
                            continue;
                        }

                        for (Suit declarationSuit : Suit.values())
                        {
                            if (game.getDeclaration(declarationSuit) != null)
                                continue;
                            
                            Card knight = game.hasCard(Rank.Knight, declarationSuit, game.getPlayer2Cards());
                            Card king   = game.hasCard(Rank.King,   declarationSuit, game.getPlayer2Cards());
                            
                            if (knight != null && king != null)
                            {   
                                game.declare(declarationSuit);
                                
                                continue;
                            }
                        }
                        
                        if (game.canDeclareTute(Rank.King, game.getPlayer2Cards()) ||
                            game.canDeclareTute(Rank.Knight, game.getPlayer2Cards()))
                        {
                            game.declareTute();
                            break;
                        }
                    }
                }
                
                if (game.isPlayer1Turn())
                {
                    Card player1Card = player1.calculatePlayerCardBegin();
                    
                    Card player2Card = player2.calculatePlayerCardResponse(player1Card);
                    
                    player1WonLastTrick = game.playCards(player1Card, player2Card);
                }
                else
                {
                    Card player2Card = player2.calculatePlayerCardBegin();
                    
                    Card player1Card = player1.calculatePlayerCardResponse(player2Card);
                    
                    player1WonLastTrick = game.playCards(player2Card, player1Card);
                }
                
                if (!game.isDeckEmpty())
                {
                    if (game.isPlayer1Turn())
                    {
                        game.dealCardToPlayer1();
                        game.dealCardToPlayer2();
                    }
                    else
                    {
                        game.dealCardToPlayer2();
                        game.dealCardToPlayer1();
                    }
                }
            }
            
            game.countPointsAndRestart(player1WonLastTrick);
        }
        
        System.out.println("GAMES " + game.getPlayer1Games() + " " + game.getPlayer2Games()); //$NON-NLS-1$ //$NON-NLS-2$
        
        int nonDrawGames = game.getPlayer1Games() + game.getPlayer2Games();
        
        if (game.getPlayer1Games() > game.getPlayer2Games())
            System.out.println(player1.getClass().getSimpleName() + " WON " + (100 * game.getPlayer1Games() / nonDrawGames) + "%"); //$NON-NLS-1$ //$NON-NLS-2$
        else
            System.out.println(player2.getClass().getSimpleName() + " WON " + (100 * game.getPlayer2Games() / nonDrawGames) + "%"); //$NON-NLS-1$ //$NON-NLS-2$
        
    }
}
