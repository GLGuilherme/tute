package iams.cardgame.tute;

import java.util.ArrayList;
import java.util.Collections;

import iams.cardgame.animators.AnimationController;
import iams.cardgame.animators.MoveToBackAnimator;
import iams.cardgame.animators.MoveToFrontAnimator;
import iams.cardgame.animators.ReverseAnimator;
import iams.cardgame.animators.WaitAnimator;
import iams.cardgame.tute.CardModel.Rank;
import iams.cardgame.tute.CardModel.Suit;
import iams.cardgame.tute.TuteGame.Declaration;
import iams.cardgame.tute.ai.BasicTuteAI;
import iams.cardgame.tute.ai.TuteAI;
import iams.cardgame.tute.movement.Movement;
import iams.cardgame.tute.movement.PintaMovement;
import iams.cardgame.tute.movement.ThrowMovement;
import iams.cardgame.tute.movement.ThrowResponseMovement;
import iams.cardgame.tute.movement.TuteMovement;
import iams.cardgame.tute.movement.TwentyFortyMovement;
import iams.cardgame.tute.tr.Translator;

public class TuteGameUI
{
    final public Translator tr;
    
    final private TuteGame game;
    final private TuteController controller;
    
    private HumanPlayer human;
    private TuteAI ai;
    
    private String notification = null;
    
    private Card player2playedCard = null;
    
    private int player1Points = -1;
    private int player2Points = -1;
    
    class MessageAnimator implements AnimationController.Animator
    {
        final private String message;
        
        private int delay;
        
        public MessageAnimator(String message, int delay)
        {
            this.message = message;
            
            this.delay = delay;
        }
        
        @Override
        public boolean tick()
        {
            if (this.delay > 0)
            {
                TuteGameUI.this.notification = message;

                this.delay --;
            
                return false;
            }
            else
            {
                TuteGameUI.this.notification = null;

                return true;
            }
        }
    };

    public int getPlayer1Points()
    {
        return this.player1Points;
    }

    public int getPlayer2Points()
    {
        return this.player2Points;
    }
    
    public String getNotification()
    {
        return this.notification;
    }

    public TuteGameUI(Translator tr, TuteGame game, TuteController controller, HumanPlayer humanPlayer)
    {
        this.tr = tr;
        this.game = game;
        
        this.human = humanPlayer;
        
        this.controller = controller;
        
        this.initialize();
    }
    
    void initialize()
    {
        for (Card card : this.game.getDeck())
            this.controller.add(new ReverseAnimator(card, false));

        this.controller.add(this.controller.getDeckCardMove(this.game.getDeck()));
        
        this.ai = new BasicTuteAI(new TuteGamePlayerContext(this.game, false));
        
        for (int i = 0; i < TuteGame.NUM_CARDS_PER_PLAYER; i ++)
            this.dealNewCards();
        
        this.controller.add(new ReverseAnimator(this.game.getPinta(), true));
        
        this.controller.add(this.controller.relocatePlayer1Cards(this.game.getPlayer1Cards()));
        
        this.controller.add(this.controller.relocatePlayer2Cards(this.game.getPlayer2Cards()));

        this.controller.add(this.controller.getPintaMovement(this.game.getPinta()));
        
        this.controller.add(new WaitAnimator(50));
        
        this.controller.add(new AnimationController.Animator()
        {
            @Override
            public boolean tick()
            {
                TuteGameUI.this.startTurn();
                
                return true;
            }
        });
    }

    private void startTurn()
    {
        if (this.game.canMakeDeclarations())
        {
            Card cardChangeableByPinta = this.game.getPlayer2CardChangeableByPinta();
            
            if (cardChangeableByPinta != null)
            {
                this.controller.add(new ReverseAnimator(cardChangeableByPinta, true));
                
                this.controller.add(new MoveToFrontAnimator(cardChangeableByPinta));
                
                this.controller.add(this.controller.getPlayer2PintaCardThrow(cardChangeableByPinta));
    
                this.controller.add(new MoveToBackAnimator(cardChangeableByPinta));

                this.controller.add(new MessageAnimator(this.tr.getChangePintaString(), 100));

                Card previousPinta = this.game.changePintaPlayer2();
                
                this.controller.add(this.controller.relocatePlayer2Cards(this.game.getPlayer2Cards()));
                
                this.controller.add(this.controller.getPintaMovement(this.game.getPinta()));
                
                this.controller.add(new ReverseAnimator(previousPinta, false));
                
                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.startTurn();
                        
                        return true;
                    }
                });
                
                return;
            }
        }
        
        if (!this.game.isPlayer1Turn() && !this.game.getPlayer1Cards().isEmpty())
        {
            if (this.game.canMakeDeclarations())
            {
                for (Rank rank : new Rank[] { Rank.King, Rank.Knight })
                {
                    if (this.game.canDeclareTute(rank, this.game.getPlayer2Cards()))
                    {
                        for (Card c : this.game.getPlayer2Cards())
                        {
                            if (c.rank == rank)
                                this.controller.add(new ReverseAnimator(c, true));
                        }
        
                        this.controller.add(new AnimationController.Animator()
                        {
                            @Override
                            public boolean tick()
                            {
                                TuteGameUI.this.tute(rank);
                                return true;
                            }
                        });
                        
                        return;
                    }
                }
    
                for (Suit declarationSuit : Suit.values())
                {
                    if (this.game.getDeclaration(declarationSuit) != null ||
                        this.game.getDeclaration(this.game.getPinta().suit) != null)
                        continue;
                    
                    Card knight = this.game.hasCard(Rank.Knight, declarationSuit, this.game.getPlayer2Cards());
                    Card king   = this.game.hasCard(Rank.King,   declarationSuit, this.game.getPlayer2Cards());
                    
                    if (knight != null && king != null)
                    {   
                        this.game.declare(declarationSuit);
                        
                        this.controller.add(new WaitAnimator(20));
                        
                        this.controller.add(new ReverseAnimator(knight, true));
                        this.controller.add(new ReverseAnimator(king,   true));
    
                        this.controller.add(new MessageAnimator(
                                this.tr.getTwentyFortyDeclarationString(this.game.getPinta().suit, declarationSuit), 100));
                                
                        this.controller.add(new AnimationController.Animator()
                        {
                            @Override
                            public boolean tick()
                            {
                                TuteGameUI.this.fireWaitForUserClick(true);
    
                                return true;
                            }
                        });
    
                        this.controller.add(new ReverseAnimator(knight, false));
                        this.controller.add(new ReverseAnimator(king,   false));
    
                        this.controller.add(new AnimationController.Animator()
                        {
                            @Override
                            public boolean tick()
                            {
                                TuteGameUI.this.startTurn();
                                
                                return true;
                            }
                        });
                        
                        return;
                    }
                }
            }
            
            this.controller.add(new WaitAnimator(20));
            
            this.player2playedCard = TuteGameUI.this.ai.calculatePlayerCardBegin();
            
            this.controller.add(new ReverseAnimator(this.player2playedCard, true));

            this.controller.add(new MoveToFrontAnimator(this.player2playedCard));
            
            this.controller.add(this.controller.getCenterCardThrow(this.player2playedCard));
        }
        
        this.controller.add(new AnimationController.Animator()
        {
            @Override
            public boolean tick()
            {
                TuteGameUI.this.fireWaitForUserClick(true);
                
                return true;
            }
        });
    }

    private void dealNewCards()
    {
        if (this.game.isDeckEmpty())
        {
            return; 
        }
        
        if (this.game.isPlayer1Turn())
        {
            Card c1 = this.game.dealCardToPlayer1();
    
            this.controller.add(new MoveToFrontAnimator(c1));
    
            this.controller.add(this.controller.getPlayer1CardThrow(c1));
            
            this.controller.add(new ReverseAnimator(c1, true));
        }
        
        Card c2 = this.game.dealCardToPlayer2();
        
        this.controller.add(new MoveToFrontAnimator(c2));
        
        this.controller.add(this.controller.getPlayer2PlayerCardThrow(c2));
        
        this.controller.add(new ReverseAnimator(c2, false));

        if (!this.game.isPlayer1Turn())
        {
            Card c1 = this.game.dealCardToPlayer1();
    
            this.controller.add(new MoveToFrontAnimator(c1));
    
            this.controller.add(this.controller.getPlayer1CardThrow(c1));
            
            this.controller.add(new ReverseAnimator(c1, true));
        }
    }
    
    private void renuncio(Card currentCard)
    {
        this.game.declareRenuncio(this.player2playedCard, currentCard);
        
        this.abortGame(this.tr.getDeclareRenuncioString());
    }
    
    private void tute(Rank rank)
    {
        this.game.declareTute();
    
        this.abortGame(this.tr.getTuteDeclarationString(rank));
    }
    
    private void abortGame(String message)
    {
        this.controller.add(new MessageAnimator(message, 100));
        
        this.controller.add(this.controller.getCenterCardMove(this.game.getCardRasters()));

        this.human.clearSelection();
        
        this.controller.add(new AnimationController.Animator()
        {
            @Override
            public boolean tick()
            {        
                TuteGameUI.this.game.countPointsAndRestart(TuteGameUI.this.game.isPlayer1Turn());
                TuteGameUI.this.initialize();
                return true;
            }
        });
    }
    
    private void completeGame(boolean player1WonLastTrick)
    {
        this.human.clearSelection();
        
        ArrayList<Card> player1Wins = new ArrayList<Card>(this.game.getPlayer1Baza());
        ArrayList<Card> player2Wins = new ArrayList<Card>(this.game.getPlayer2Baza());
        
        Collections.reverse(player1Wins);
        Collections.reverse(player2Wins);
        
        this.controller.add(this.controller.getCenterWinCardThrow(player1Wins));
        
        this.player1Points = 0;
        this.player2Points = 0;
        
        for (Card card : player1Wins)
        {
            this.controller.add(new MoveToFrontAnimator(card));
            this.controller.add(this.controller.getCenterCardThrow(card));
            this.controller.add(new ReverseAnimator(card, true));
            
            if (card.rank.countValue > 0)
            {
                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.player1Points += card.rank.countValue;
                        
                        return true;
                    }
                });

                this.controller.add(new MessageAnimator(this.tr.getPlusPointsString(card.rank.countValue), 30));
            }
        }

        if (player1WonLastTrick)
        {
            this.controller.add(new WaitAnimator(15));

            this.controller.add(new AnimationController.Animator()
            {
                @Override
                public boolean tick()
                {
                    TuteGameUI.this.player1Points += 10;
                    return true;
                }
            });

            this.controller.add(new MessageAnimator(this.tr.getPlus10DeMonteString(), 30));
        }

        for (Suit suit : Suit.values())
        {
            Declaration declaration = this.game.getDeclaration(suit);
            
            if (declaration == Declaration.Player1)
            {
                this.controller.add(new WaitAnimator(15));

                int points = suit == TuteGameUI.this.game.getPinta().suit ? 40 : 20;
                
                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.player1Points += points;
                        return true;
                    }
                });

                this.controller.add(new MessageAnimator(this.tr.getPlusTwentyFortyPointsString(points, suit), 30));
            }
        }
        
        this.controller.add(new WaitAnimator(50));
        
        this.controller.add(this.controller.getCenterWinCardThrow(player2Wins));

        for (Card card : player2Wins)
        {
            this.controller.add(new MoveToFrontAnimator(card));
            this.controller.add(this.controller.getCenterCardThrow(card));
            this.controller.add(new ReverseAnimator(card, true));
            
            if (card.rank.countValue > 0)
            {
                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.player2Points += card.rank.countValue;
                        
                        return true;
                    }
                });

                this.controller.add(new MessageAnimator(this.tr.getPlusPointsString(card.rank.countValue), 30));
            }
        }

        if (!player1WonLastTrick)
        {
            this.controller.add(new WaitAnimator(15));

            this.controller.add(new AnimationController.Animator()
            {
                @Override
                public boolean tick()
                {
                    TuteGameUI.this.player2Points += 10;
                    return true;
                }
            });

            this.controller.add(new MessageAnimator(this.tr.getPlus10DeMonteString(), 30));
        }
        
        for (Suit suit : Suit.values())
        {
            Declaration declaration = this.game.getDeclaration(suit);
            
            if (declaration == Declaration.Player2)
            {
                this.controller.add(new WaitAnimator(15));

                int points = suit == TuteGameUI.this.game.getPinta().suit ? 40 : 20;
                
                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.player2Points += points;
                        return true;
                    }
                });

                this.controller.add(new MessageAnimator(this.tr.getPlusTwentyFortyPointsString(points, suit), 30));
            }
        }
        
        this.controller.add(new AnimationController.Animator()
        {
            @Override
            public boolean tick()
            {
                TuteGameUI.this.game.countPointsAndRestart(player1WonLastTrick);
                
                return true;
            }
        });
        
        this.controller.add(new WaitAnimator(100));
        
        this.controller.add(this.controller.getCenterCardMove(this.game.getCardRasters()));
        
        this.controller.add(new AnimationController.Animator()
        {
            @Override
            public boolean tick()
            {
                TuteGameUI.this.player1Points = TuteGameUI.this.player2Points = -1;

                TuteGameUI.this.initialize();
                
                return true;
            }
        });
    }

    public void onHumanMovement(Movement movement)
    {
        if (movement != null)
        {
            this.fireWaitForUserClick(false);
            
            if (movement instanceof PintaMovement)
            {
                Card cardChangeableByPinta = this.game.getPlayer1CardChangeableByPinta();
                
                if (cardChangeableByPinta == null)
                    throw new AssertionError();
                
                this.controller.add(new MoveToFrontAnimator(cardChangeableByPinta));
                
                this.controller.add(this.controller.getPlayer1PintaCardThrow(cardChangeableByPinta));
                
                this.controller.add(new MoveToBackAnimator(cardChangeableByPinta));
    
                this.controller.add(new MessageAnimator(this.tr.getChangePintaString(), 100));
    
                this.game.changePintaPlayer1();
    
                this.controller.add(this.controller.relocatePlayer1Cards(this.game.getPlayer1Cards()));
    
                this.controller.add(this.controller.getPintaMovement(this.game.getPinta()));

                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.fireWaitForUserClick(true);
    
                        return true;
                    }
                });
            }
            else if (movement instanceof TuteMovement)
            {
                this.tute(((TuteMovement) movement).getRank());

                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.fireWaitForUserClick(true);
    
                        return true;
                    }
                });
            }
            else if (movement instanceof TwentyFortyMovement)
            {
                Suit declarationSuit = ((TwentyFortyMovement) movement).getSuit();
                
                this.game.declare(declarationSuit);
                
                this.controller.add(new MessageAnimator(
                        this.tr.getTwentyFortyDeclarationString(
                                this.game.getPinta().suit, declarationSuit), 100));
    
                this.controller.add(new AnimationController.Animator()
                {
                    @Override
                    public boolean tick()
                    {
                        TuteGameUI.this.fireWaitForUserClick(true);
    
                        return true;
                    }
                });
            }
            else if (movement instanceof ThrowMovement)
            {
                Card currentCard = ((ThrowMovement) movement).getCurrentCard();
                
                this.controller.add(new MoveToFrontAnimator(currentCard));
                
                this.controller.add(this.controller.getCenterCardThrow(currentCard));
    
                this.controller.add(new WaitAnimator(20));
                
                Card player2Card = TuteGameUI.this.ai.calculatePlayerCardResponse(currentCard);
    
                this.controller.add(new MoveToFrontAnimator(player2Card));
                
                this.controller.add(this.controller.getCenterCardThrow(player2Card));
    
                this.controller.add(new ReverseAnimator(player2Card, true));
                
                boolean player1Wins = this.game.playCards(currentCard, player2Card);
                
                this.playTurn(currentCard, player2Card, player1Wins);
            }
            else if (movement instanceof ThrowResponseMovement)
            {
                Card currentCard = ((ThrowResponseMovement) movement).getCurrentCard();
                
                this.controller.add(new MoveToFrontAnimator(currentCard));
    
                this.controller.add(this.controller.getCenterCardThrow(currentCard));
    
                if (this.game.calculateIfRenuncio(this.player2playedCard, currentCard, this.game.getPlayer1Cards()))
                {
                    this.renuncio(currentCard);
                }
                else
                {
                    boolean player1Wins = this.game.playCards(this.player2playedCard, currentCard);
                    
                    this.playTurn(currentCard, this.player2playedCard, player1Wins);
                    
                    this.player2playedCard = null;
                }
            }
        }
        
        this.human.clearSelection();
    }

    protected void fireWaitForUserClick(boolean b)
    {
        for (Card card : this.game.getCardRasters())
            card.enableHighlight(b);

        this.human.fireWaitForUserClick(b);
        
        this.controller.repaint();
    }

    private void playTurn(Card card1, Card card2, boolean player1Wins)
    {
        this.controller.add(new WaitAnimator(20));
        
        if (player1Wins)
            this.controller.add(this.controller.getPlayer1WinDeckMovement(card1, card2));
        else
            this.controller.add(this.controller.getPlayer2WinDeckMovement(card1, card2));
        
        this.controller.add(new AnimationController.Animator()
        {
            @Override
            public boolean tick()
            {
                newTurn(player1Wins);
                
                return true;
            }
        });
    }

    private void newTurn(boolean player1WonLastTrick)
    {
        if (this.game.arePlayerCardsEmpty())
        {
            this.completeGame(player1WonLastTrick);
        }
        else
        {
            this.dealNewCards();
            
            this.controller.add(this.controller.relocatePlayer1Cards(this.game.getPlayer1Cards()));
            
            this.controller.add(this.controller.relocatePlayer2Cards(this.game.getPlayer2Cards()));
            
            this.controller.add(new AnimationController.Animator()
            {
                @Override
                public boolean tick()
                {
                    TuteGameUI.this.startTurn();
                    
                    return true;
                }
            });
        }
    }
}
