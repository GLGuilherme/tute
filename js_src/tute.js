import { Tr } from "./tr-es.js";

import { Suit, Rank, CardModel, Card } from "./cards.js";

import { AnimationController, 
		MoveCardAnimator, 
		ThrowCardAnimator, 
		MoveToBackAnimator, 
		MoveToFrontAnimator, 
		ReverseAnimator, 
		MessageAnimator,
		WaitAnimator,
		MultiAnimator,
		TuteController } from "./animators.js";

import { PintaMovement,
		ThrowMovement,
		ThrowResponseMovement,
		TuteMovement,
		TwentyFortyMovement	} from "./movements.js";

import { GameUI } from "./gameui.js";

const Color = 
{
	green:  "#00FF00",
	cyan:   "#00FFFF",
	yellow: "#FFFF00",
	pink:   "#FF69B4"
}

const TuteGame =
{
    NUM_CARDS_PER_PLAYER: 8,

    cardValueComparator: function(o1, o2)
    {
		let n = (TuteGame.pinta != null && o1.suit == TuteGame.pinta.suit ? 10 : o1.suit.index) - 
				(TuteGame.pinta != null && o2.suit == TuteGame.pinta.suit ? 10 : o2.suit.index);
		
		if (n != 0)
			return n;
		
		return o1.rank.relativeValue - o2.rank.relativeValue;
    },
	
    deck: [],
    player1Cards: [],
    player2Cards: [],
    player1Baza: [],
    player2Baza: [],
    pinta: null,

    skipCardCount: false,
    player1Games: 0,
    player2Games: 0,
    player1Turn: false,
    player1Mano: true,

    declarations: {},

    initialize: function()
    {
        for (let card of GameUI.cards)
        {
            card.moveTo(GameUI.BOARD_WIDTH / 2, GameUI.BOARD_HEIGHT / 2, 0)

            TuteGame.deck.push(card);
        }

        TuteGame.shuffleDeck();

    },

    shuffleDeck: function()
    {
        for (let i = TuteGame.deck.length - 1; i > 0; i--)
        {
            const j = Math.floor(Math.random() * (i + 1));

            [TuteGame.deck[i], TuteGame.deck[j]] = [TuteGame.deck[j], TuteGame.deck[i]];
        }
		
        for (let card of TuteGame.deck)
            card.moveToFront();

        TuteGame.pinta = TuteGame.deck[TuteGame.deck.length - 1];

        TuteGame.pinta.moveToBack();
		
        GameUI.repaint();
	},

    hasCard: function(rank, suit, playerCards)
    {
        for (let c of playerCards)
        {
            if (c.rank == rank && c.suit == suit)
                return c;
        }

        return null;
    },

    calculateIfFirstCardWins: function(card1, card2)
    {
        if ((card1.suit == this.pinta.suit) && (card2.suit == this.pinta.suit))
            return card1.rank.relativeValue > card2.rank.relativeValue;

        if ((card1.suit == this.pinta.suit) != (card2.suit == this.pinta.suit))
            return card1.suit == this.pinta.suit;

        if (card1.suit == card2.suit)
            return card1.rank.relativeValue > card2.rank.relativeValue;

        return true;
    },

    calculateAllowedCardsToAvoidRenuncio: function(firstCard, playerCards)
    {
        if (TuteGame.deck.length == 0)
        {
            let alternatives = [];
            
            for (let c of playerCards)
            {
                if (firstCard.suit == c.suit 
                        && c.rank.relativeValue > firstCard.rank.relativeValue)
                {
                    alternatives.push(c);
                }
            }
    
            if (alternatives.length == 0)
			{
				for (let c of playerCards)
				{
					if (firstCard.suit == c.suit)
					{
						alternatives.push(c);
					}
				}
			}
            
			if (firstCard.suit != TuteGame.pinta.suit)
			{
				for (let c of playerCards)
				{
					if (TuteGame.pinta.suit == c.suit)
					{
						alternatives.push(c);
					}
				}
			}
            
            if (alternatives.length > 0)
                return alternatives;
        }
        
        return playerCards;
    },
    
    calculateIfRenuncio: function(firstCard, secondCard, playerCards)
    {
        return !TuteGame.calculateAllowedCardsToAvoidRenuncio(firstCard, playerCards).includes(secondCard);
    },
	
    declareRenuncio: function(firstCard, secondCard)
    {
        let renuncio = false;
        
        if (this.player1Turn)
        {
            renuncio = this.calculateIfRenuncio(firstCard, secondCard, this.player2Cards);
        }
        else
        {
            renuncio = this.calculateIfRenuncio(firstCard, secondCard, this.player1Cards);
        }
        
        if (this.player1Turn)
        {
            if (renuncio)
                this.player1Games += 2;
            else
                this.player2Games += 2;
        }
        else // if (!player1Turn)
        {
            if (renuncio)
                this.player2Games += 2;
            else
                this.player1Games += 2;
        }
		
		TuteGameUI.updateGames();

        this.skipCardCount = true;
    },
    
	remove(arr, elem)
	{
		let index = arr. indexOf(elem);
	
		if (index > -1)
		{
			arr.splice(index, 1)
			return true;
		}
		
		return false;
	},
    
    playCards: function(firstCard, secondCard)
    {
        let firstCardWins = TuteGame.calculateIfFirstCardWins(firstCard, secondCard);
        
        let player1Wins = false;
        
        if (TuteGame.player1Turn)
        {
            if (TuteGame.calculateIfRenuncio(firstCard, secondCard, TuteGame.player2Cards))
                throw new AssertionError();
            
            if (!TuteGame.remove(TuteGame.player1Cards, firstCard))
                throw new AssertionError();
            
            if (!TuteGame.remove(TuteGame.player2Cards, secondCard))
                throw new AssertionError();
            
            player1Wins = firstCardWins;
        }
        else
        {
            if (TuteGame.calculateIfRenuncio(firstCard, secondCard, TuteGame.player1Cards))
                throw new AssertionError();

            if (!TuteGame.remove(TuteGame.player2Cards, firstCard))
                throw new AssertionError();
            
            if (!TuteGame.remove(TuteGame.player1Cards, secondCard))
                throw new AssertionError();
            
            player1Wins = !firstCardWins;
        }
        
        if (player1Wins)
        {
            TuteGame.player1Baza.push(firstCard);
            TuteGame.player1Baza.push(secondCard);
        }
        else
        {
            TuteGame.player2Baza.push(firstCard);
            TuteGame.player2Baza.push(secondCard);
        }
        
        TuteGame.player1Turn = player1Wins;

        return player1Wins;
    },

    arePlayerCardsEmpty: function()
    {
        return this.player1Cards.length == 0 && this.player2Cards.length == 0;
    },

    declare: function(suit)
    {
        if (this.canMakeDeclarations())
        {
			if (this.player1Turn && 
				this.hasCard(CardModel.rankFromName("King"),   suit, this.player1Cards) != null && 
				this.hasCard(CardModel.rankFromName("Knight"), suit, this.player1Cards) != null)
			{
				this.declarations[suit.name] = "1";
				return;
			}

			if (!this.player1Turn && 
				this.hasCard(CardModel.rankFromName("King"),   suit, this.player2Cards) != null && 
				this.hasCard(CardModel.rankFromName("Knight"), suit, this.player2Cards) != null)
			{
				this.declarations[suit.name] = "2";
				return;
			}
		}
        
        throw new AssertionError();
    },
    
    getDeclaration: function(suit)
    {
        if (suit.name in this.declarations)
			return this.declarations[suit.name];
		
		return null;
    },

    dealCardToPlayer: function(playerCards)
    {
        const c1 = this.deck.shift();

        playerCards.push(c1);
		
		playerCards = playerCards.sort(TuteGame.cardValueComparator);

        return c1;
    },

    dealCardToPlayer1: function()
    {
        return this.dealCardToPlayer(TuteGame.player1Cards);
    },

    dealCardToPlayer2: function()
    {
        return this.dealCardToPlayer(TuteGame.player2Cards);
    },

    getPlayer1CardChangeableByPinta: function()
    {
        return this.getCardChangeableByPinta(TuteGame.player1Cards);
    },

    getPlayer2CardChangeableByPinta: function()
    {
        return this.getCardChangeableByPinta(TuteGame.player2Cards);
    },

    getCardChangeableByPinta: function(playerCards)
    {
        if (this.pinta.rank.name != "V2")
        {
            for (let card of playerCards)
            {
                if (card.suit == this.pinta.suit)
                {
                    if (card.rank.name == "V7" &&
                       (this.pinta.rank.name == "V8" ||
                        this.pinta.rank.name == "V9" ||
                        this.pinta.rank.name == "Knave" ||
                        this.pinta.rank.name == "Knight" ||
                        this.pinta.rank.name == "King" ||
                        this.pinta.rank.name == "V3" ||
                        this.pinta.rank.name == "Ace"))
                    {
                        return card;
                    }

                    if (card.rank.name == "V2" &&
                        (this.pinta.rank.name == "V4" ||
                         this.pinta.rank.name == "V5" ||
                         this.pinta.rank.name == "V6" ||
                         this.pinta.rank.name == "V7"))
                    {
                        return card;
                    }
                }
            }
        }

        return null;
    },

	changePintaPlayer: function(cardChangeableByPinta, playerCards)
    {
        if (!TuteGame.remove(playerCards, cardChangeableByPinta))
            throw new AssertionError();
        
        if (!playerCards.push(this.pinta))
            throw new AssertionError();
		
		playerCards = playerCards.sort(TuteGame.cardValueComparator);
        
        if (!TuteGame.remove(this.deck, this.pinta))
            throw new AssertionError();
        
        if (!this.deck.push(cardChangeableByPinta))
            throw new AssertionError();
        
        let previousPinta = this.pinta;
        
        this.pinta = cardChangeableByPinta;
        
        return previousPinta;
    },
    
    changePintaPlayer1: function()
    {
        return this.changePintaPlayer(this.getCardChangeableByPinta(this.player1Cards), this.player1Cards);
    },

    changePintaPlayer2: function()
    {
        return this.changePintaPlayer(this.getCardChangeableByPinta(this.player2Cards), this.player2Cards);
    },
    
    declareTute: function()
    {
        if (this.player1Turn &&
                (this.canDeclareTute(CardModel.rankFromName("Knight"), TuteGame.player1Cards) ||
                 this.canDeclareTute(CardModel.rankFromName("King"),   TuteGame.player1Cards)))
        {
            this.player1Games ++;
            this.skipCardCount = true;
        }
        else if (!this.player1Turn &&
                (this.canDeclareTute(CardModel.rankFromName("Knight"), TuteGame.player2Cards) ||
                 this.canDeclareTute(CardModel.rankFromName("King"),   TuteGame.player2Cards)))
        {
            this.player2Games ++;
            this.skipCardCount = true;
        }
        else
        {
            throw new AssertionError();
        }
		
		TuteGameUI.updateGames();
    },

	canMakeDeclarations: function()
	{
		return (this.player1Baza.length > 0 || this.player2Baza.length > 0) && this.deck.length > 0;
	},
	
    canDeclareTute: function(rank, playerCards)
    {
        if (!this.canMakeDeclarations())
            return false;
            
		for (let suit of Suit)
		{
			if (this.hasCard(rank, suit, playerCards) == null)
				return false;
		}
		
        return true;
    },
	
    countPointsAndRestart: function(player1WinnedLastTrick)
    {
        if (!this.skipCardCount)
        {
            let player1Points = 0, player2Points = 0;
            
            for (let card of this.player1Baza)
                player1Points += card.rank.countValue;
                
            for (let card of this.player2Baza)
                player2Points += card.rank.countValue;
            
            if (player1WinnedLastTrick)
                player1Points += 10;
            else
                player2Points += 10;
            
            for (let key in this.declarations)
            {
				let value = this.declarations[key];
				
                let points = key == this.pinta.suit.name ? 40 : 20;
                
                if (value == "1")
                    player1Points += points;
    
                if (value == "2")
                    player2Points += points;
            }
            
            if (player1Points > player2Points)
                this.player1Games ++;
    
            else if (player1Points < player2Points)
                this.player2Games ++;
			
			TuteGameUI.updateGames();
        }
            
        this.skipCardCount = false;
        
        this.deck = this.deck.concat(this.player1Cards);
        this.deck = this.deck.concat(this.player2Cards);
        this.deck = this.deck.concat(this.player1Baza);
        this.deck = this.deck.concat(this.player2Baza);
        
		this.declarations = {};
		
        this.player1Cards = [];
        this.player2Cards = [];
        this.player1Baza  = [];
        this.player2Baza  = [];
        
        this.player1Mano = !this.player1Mano;
        this.player1Turn = !this.player1Mano;
        
        this.shuffleDeck();
    }

}

const TuteAI =
{
    calculatePlayerCardBegin: function(myCards)
    {
        let candidateCardsToThrow = [];

        if (candidateCardsToThrow.length == 0)
        {
            if (TuteGame.deck.length == 0)
            {
                if (candidateCardsToThrow.length == 0)
                {
                    let maximumValue = 0;

                    for (let c of myCards)
                    {
                        if (c.suit != TuteGame.pinta.suit)
                        {
                            if (c.rank.countValue == maximumValue)
                            {
                                candidateCardsToThrow.push(c);
                            }
                            else if (c.rank.countValue > maximumValue)
                            {
                                maximumValue = c.rank.countValue;

                                candidateCardsToThrow = [];

                                candidateCardsToThrow.push(c);
                            }
                        }
                    }
                }
            }
            else
            {
                if (candidateCardsToThrow.length == 0)
                {
                    let minimumValue = CardModel.rankFromName("Ace").countValue;

                    for (let c of myCards)
                    {
                        if (c.suit != TuteGame.pinta.suit)
                        {
                            if (c.rank.countValue == minimumValue)
                            {
                                candidateCardsToThrow.push(c);
                            }
                            else if (c.rank.countValue < minimumValue)
                            {
                                minimumValue = c.rank.countValue;

                                candidateCardsToThrow = [];

                                candidateCardsToThrow.push(c);
                            }
                        }
                    }
                }
            }

            if (candidateCardsToThrow.length == 0)
                candidateCardsToThrow.push(myCards[0]);

        }

        return candidateCardsToThrow[parseInt(Math.random() * candidateCardsToThrow.length)];
    },

    calculatePlayerCardResponse: function(thrownCard)
    {
        const myCards = TuteGame.calculateAllowedCardsToAvoidRenuncio(thrownCard, TuteGame.player2Cards);

        let candidateCardsToThrow = [];

        if (thrownCard.rank.countValue == 0)
        {
            for (let c of myCards)
            {
                if (c.suit != TuteGame.pinta.suit &&
                    c.suit == thrownCard.suit &&
                    (c.rank.name == "Ace" || c.rank.name == "V3"))
                {
                    candidateCardsToThrow.push(c);
                }
            }

            if (candidateCardsToThrow.length == 0)
                return this.calculatePlayerCardBegin(myCards);
        }
        else
        {
            if (candidateCardsToThrow.length == 0)
            {
                for (let c of myCards)
                {
                    if (c.suit != TuteGame.pinta.suit &&
                        c.suit == thrownCard.suit &&
                        c.rank.countValue > thrownCard.rank.countValue &&
                        (c.rank.name == "Ace" || c.rank.name == "V3"))
                    {
                        candidateCardsToThrow.push(c);
                    }
                }
            }

            if (candidateCardsToThrow.length == 0)
            {
                for (let c of myCards)
                {
                    if (c.suit == thrownCard.suit &&
                        c.rank.countValue > thrownCard.rank.countValue)
                    {
                        candidateCardsToThrow.push(c);
                    }
                }
            }

            if (candidateCardsToThrow.length == 0)
            {
                if (thrownCard.suit != TuteGame.pinta.suit)
                {
                    for (let c of myCards)
                    {
                        if (c.suit == TuteGame.pinta.suit &&
                            c.rank.countValue == 0)
                        {
                            candidateCardsToThrow.push(c);
                        }
                    }
                }
            }
        }

        if (candidateCardsToThrow.length == 0)
            candidateCardsToThrow.push(myCards[0]);

        return candidateCardsToThrow[parseInt(Math.random() * candidateCardsToThrow.length)];
    }
}

const HumanPlayer =
{
    hoverCard: null,
	
    otherCurrentCards: [],
	
    onMouseOver: function(card, over)
    {
        if (over)
        {
            if (HumanPlayer.hoverCard == card)
                return;

            HumanPlayer.clearSelection();

            HumanPlayer.hoverCard = card;
        }
        else if (HumanPlayer.hoverCard != null)
        {
            HumanPlayer.clearSelection();

            HumanPlayer.hoverCard = null;
        }

        HumanPlayer._onMouseOver();
    },
    
	lastTouchedMovement: null,
	
	is_touch_device: function()
	{
	  var prefixes = ' -webkit- -moz- -o- -ms- '.split(' ');
	  var mq = function(query) {
		return window.matchMedia(query).matches;
	  }

	  if (('ontouchstart' in window) || window.DocumentTouch && document instanceof DocumentTouch) {
		return true;
	  }

	  // include the 'heartz' as a way to have a non matching MQ to help terminate the join
	  // https://git.io/vznFH
	  var query = ['(', prefixes.join('touch-enabled),('), 'heartz', ')'].join('');
	  return mq(query);
	},

    _onMouseOver: function()
    {
		if (!HumanPlayer.is_touch_device())
		{
			let movement = HumanPlayer.getPlayerMovement();
	        
	        if (movement != null)
	            $("#player1Message").html(movement.toString());
		}
    },
	
	getPlayerMovement: function()
	{
		$("#player1Message").html("");

		let touchedMovement = HumanPlayer._getPlayerMovement();
			
		if (HumanPlayer.is_touch_device())
		{
			if (touchedMovement != null && 
				HumanPlayer.lastTouchedMovement != null &&
				touchedMovement.toString() == HumanPlayer.lastTouchedMovement.toString())
			{
				return touchedMovement;
			}
			else
			{
				$("#player1Message").html(touchedMovement.toString());
				
				HumanPlayer.lastTouchedMovement = touchedMovement;
			}
		
			return null;
		}
		else
		{
			return touchedMovement;
		}
	},
    
    _getPlayerMovement: function()
    {
        if (HumanPlayer.hoverCard != null)
        {
            if (TuteGame.player1Turn)
            {
                if (TuteGame.canMakeDeclarations())
                {
                    if (HumanPlayer.hoverCard == TuteGame.pinta && TuteGame.getPlayer1CardChangeableByPinta() != null)
                    {
                        HumanPlayer.hoverCard.setHighlightColor(Color.green);
                        
                        return new PintaMovement();
                    }
				}
				
				if (TuteGame.player1Cards.includes(HumanPlayer.hoverCard))
				{
					if (TuteGame.canMakeDeclarations())
					{
						if (HumanPlayer.hoverCard.rank.name == "King" || HumanPlayer.hoverCard.rank.name == "Knight")
						{
                                if (TuteGame.canDeclareTute(HumanPlayer.hoverCard.rank, TuteGame.player1Cards))
							{
								for (let c of TuteGame.player1Cards)
								{
									if (c.rank == HumanPlayer.hoverCard.rank)
									{
										HumanPlayer.otherCurrentCards.push(c);
										
										c.setHighlightColor(Color.cyan);
									}
								}

								return new TuteMovement(HumanPlayer.hoverCard.rank);
							}
						}
						
						if (HumanPlayer.hoverCard.rank.name == "King" &&
							TuteGame.getDeclaration(TuteGame.pinta.suit) == null &&
							TuteGame.getDeclaration(HumanPlayer.hoverCard.suit) == null)
						{
							let knight = TuteGame.hasCard(CardModel.rankFromName("Knight"), HumanPlayer.hoverCard.suit, TuteGame.player1Cards);
							
							if (knight != null)
							{
								HumanPlayer.otherCurrentCards.push(knight);
								
								HumanPlayer.hoverCard.setHighlightColor(Color.cyan);
								
								knight.setHighlightColor(Color.cyan);

								return new TwentyFortyMovement(TuteGame.pinta.suit, HumanPlayer.hoverCard.suit);
							}
						}
					}

					HumanPlayer.hoverCard.setHighlightColor(Color.yellow);
					
					return new ThrowMovement(HumanPlayer.hoverCard);
				}
            }
            else if (TuteGame.player1Cards.includes(HumanPlayer.hoverCard))
            {
				if (TuteGame.calculateIfRenuncio(TuteGameUI.player2playedCard, HumanPlayer.hoverCard, TuteGame.player1Cards))
					HumanPlayer.hoverCard.setHighlightColor(Color.pink);
				else
					HumanPlayer.hoverCard.setHighlightColor(Color.yellow);
			
                return new ThrowResponseMovement(HumanPlayer.hoverCard);
            }
        }
        
        return null;
    },

    clearSelection: function()
    {
        if (HumanPlayer.hoverCard != null)
            HumanPlayer.hoverCard.setHighlightColor(null);
		
		HumanPlayer.hoverCard = null;
        
        for (let c of HumanPlayer.otherCurrentCards)
            c.setHighlightColor(null);
        
        HumanPlayer.otherCurrentCards = [];
    }
}

const TuteGameUI =
{
    initialize: function()
    {
		TuteGameUI.updateGames();

        for (let card of GameUI.cards)
            AnimationController.add(new ReverseAnimator(card, false));

        AnimationController.add(TuteController.getDeckCardMove(GameUI.cards))

        for (let i = 0; i < TuteGame.NUM_CARDS_PER_PLAYER; i ++)
            TuteGameUI.dealNewCards();

        AnimationController.add(new ReverseAnimator(TuteGame.pinta, true));

        AnimationController.add(TuteController.relocatePlayer1Cards(TuteGame.player1Cards));

        AnimationController.add(TuteController.relocatePlayer2Cards(TuteGame.player2Cards));

        AnimationController.add(TuteController.getPintaMovement(TuteGame.pinta));

        AnimationController.add(new WaitAnimator(50));

        AnimationController.addF(function()
        {
            TuteGameUI.startTurn();

            return true;
        })
    },

    dealNewCards: function()
    {
        if (TuteGame.deck.length == 0)
            return;

        if (TuteGame.player1Turn)
        {
            const c1 = TuteGame.dealCardToPlayer1();

            AnimationController.add(new MoveToFrontAnimator(c1));

            AnimationController.add(TuteController.getPlayer1CardThrow(c1));

            AnimationController.add(new ReverseAnimator(c1, true));
        }

        const c2 = TuteGame.dealCardToPlayer2();

        AnimationController.add(new MoveToFrontAnimator(c2));

        AnimationController.add(TuteController.getPlayer2PlayerCardThrow(c2));

        AnimationController.add(new ReverseAnimator(c2, false));

        if (!TuteGame.player1Turn)
        {
            const c1 = TuteGame.dealCardToPlayer1();

            AnimationController.add(new MoveToFrontAnimator(c1));

            AnimationController.add(TuteController.getPlayer1CardThrow(c1));

            AnimationController.add(new ReverseAnimator(c1, true));
        }
    },

    startTurn: function()
    {
		if (TuteGame.canMakeDeclarations())
		{
            const cardChangeableByPinta = TuteGame.getPlayer2CardChangeableByPinta();

            if (cardChangeableByPinta != null)
            {
                AnimationController.add(new ReverseAnimator(cardChangeableByPinta, true));

                AnimationController.add(new MoveToFrontAnimator(cardChangeableByPinta));

                AnimationController.add(TuteController.getPlayer2PintaCardThrow(cardChangeableByPinta));

                AnimationController.add(new MoveToBackAnimator(cardChangeableByPinta));

                AnimationController.add(new MessageAnimator(Tr.getChangePintaString(), 100));

                const previousPinta = TuteGame.changePintaPlayer2();

                AnimationController.add(TuteController.relocatePlayer2Cards(TuteGame.player2Cards));

                AnimationController.add(TuteController.getPintaMovement(TuteGame.pinta));

                AnimationController.add(new ReverseAnimator(previousPinta, false));

                AnimationController.addF(function()
                {
                    TuteGameUI.startTurn();

                    return true;
                });

                return;
            }
        }

        if (!TuteGame.player1Turn && TuteGame.player1Cards.length > 0)
        {
			if (TuteGame.canMakeDeclarations())
			{
				for (let rank of [ CardModel.rankFromName("King"), CardModel.rankFromName("Knight") ])
				{
					if (TuteGame.canDeclareTute(rank, TuteGame.player2Cards))
					{
						for (let c of TuteGame.player2Cards)
						{
							if (c.rank == rank)
								AnimationController.add(new ReverseAnimator(c, true));
						}

						AnimationController.addF(function()
						{
							TuteGameUI.tute(rank);
							return true;
						});

						return;
					}
				}

				for (let declarationSuit of Suit)
				{
					if (TuteGame.getDeclaration(declarationSuit) != null ||
						TuteGame.getDeclaration(TuteGame.pinta.suit) != null)
						continue;

					const knight = TuteGame.hasCard(CardModel.rankFromName("Knight"), declarationSuit, TuteGame.player2Cards);
					const king   = TuteGame.hasCard(CardModel.rankFromName("King"),   declarationSuit, TuteGame.player2Cards);

					if (knight != null && king != null)
					{
						TuteGame.declare(declarationSuit);

						AnimationController.add(new WaitAnimator(20));

						AnimationController.add(new ReverseAnimator(knight, true));
						AnimationController.add(new ReverseAnimator(king,   true));

						AnimationController.add(new MessageAnimator(
								Tr.getTwentyFortyDeclarationString(TuteGame.pinta.suit, declarationSuit), 100));

						AnimationController.addF(function()
						{
							TuteGameUI.fireWaitForUserClick(true);

							return true;
						});

						AnimationController.add(new ReverseAnimator(knight, false));
						AnimationController.add(new ReverseAnimator(king,   false));

						AnimationController.addF(function()
						{
							TuteGameUI.startTurn();

							return true;
						});

						return;
					}
				}
			}

            AnimationController.add(new WaitAnimator(20));

            TuteGameUI.player2playedCard = TuteAI.calculatePlayerCardBegin(TuteGame.player2Cards);

            AnimationController.add(new ReverseAnimator(TuteGameUI.player2playedCard, true));

            AnimationController.add(new MoveToFrontAnimator(TuteGameUI.player2playedCard));

            AnimationController.add(TuteController.getCenterCardThrow(TuteGameUI.player2playedCard));
        }

        AnimationController.addF(function()
        {
            TuteGameUI.fireWaitForUserClick(true);

            return true;
        });
    },

    fireWaitForUserClick: function(enable)
    {
		for (let card of GameUI.cards)
            card.highlightingEnabled = enable;

        if (enable)
        {
            GameUI.onCardClick = function(card)
            {
                let movement = HumanPlayer.getPlayerMovement();
            
				if (movement != null)
					TuteGameUI.onHumanMovement(movement);
				
                GameUI.repaint();
            };

            GameUI.onCardMouseEnter = function(card)
            {
                HumanPlayer.onMouseOver(card, true);
            };

            GameUI.onCardMouseLeave = function(card)
            {
                HumanPlayer.onMouseOver(card, false);
            };
        }
        else
        {
            GameUI.onCardClick      = null;
            GameUI.onCardMouseEnter = null;
            GameUI.onCardMouseLeave = null;
        }
    },

    onHumanMovement: function(movement)
    {
        if (movement != null)
        {
            this.fireWaitForUserClick(false);
            
            if (movement instanceof PintaMovement)
            {
                let cardChangeableByPinta = TuteGame.getPlayer1CardChangeableByPinta();
                
                if (cardChangeableByPinta == null)
                    throw new AssertionError();
                
                AnimationController.add(new MoveToFrontAnimator(cardChangeableByPinta));
                
                AnimationController.add(TuteController.getPlayer1PintaCardThrow(cardChangeableByPinta));
                
                AnimationController.add(new MoveToBackAnimator(cardChangeableByPinta));
    
                AnimationController.add(new MessageAnimator(Tr.getChangePintaString(), 100));
    
                TuteGame.changePintaPlayer1();
    
                AnimationController.add(TuteController.relocatePlayer1Cards(TuteGame.player1Cards));
    
                AnimationController.add(TuteController.getPintaMovement(TuteGame.pinta));

                AnimationController.addF(function()
                {
					TuteGameUI.fireWaitForUserClick(true);

					return true;
                });
            }
            else if (movement instanceof TuteMovement)
            {
                TuteGameUI.tute(movement.rank);
				
				return;
            }
            else if (movement instanceof TwentyFortyMovement)
            {
                TuteGame.declare(movement.suit);
                
                AnimationController.add(new MessageAnimator(
                        Tr.getTwentyFortyDeclarationString(
                                TuteGame.pinta.suit, movement.suit), 100));
    
                AnimationController.addF(function()
				{
					TuteGameUI.fireWaitForUserClick(true);

					return true;
                });
            }
            else if (movement instanceof ThrowMovement)
            {
                let currentCard = movement.currentCard;
                
                AnimationController.add(new MoveToFrontAnimator(currentCard));
                
                AnimationController.add(TuteController.getCenterCardThrow(currentCard));
    
                AnimationController.add(new WaitAnimator(20));
                
                let player2Card = TuteAI.calculatePlayerCardResponse(currentCard);
    
                AnimationController.add(new MoveToFrontAnimator(player2Card));
                
                AnimationController.add(TuteController.getCenterCardThrow(player2Card));
    
                AnimationController.add(new ReverseAnimator(player2Card, true));
                
                let player1Wins = TuteGame.playCards(currentCard, player2Card);
                
                TuteGameUI.playTurn(currentCard, player2Card, player1Wins);
            }
            else if (movement instanceof ThrowResponseMovement)
            {
                let currentCard = movement.currentCard;
                
                AnimationController.add(new MoveToFrontAnimator(currentCard));
    
                AnimationController.add(TuteController.getCenterCardThrow(currentCard));
    
                if (TuteGame.calculateIfRenuncio(TuteGameUI.player2playedCard, currentCard, TuteGame.player1Cards))
                {
                    TuteGameUI.renuncio(currentCard);
					
					return;
                }
                else
                {
                    let player1Wins = TuteGame.playCards(TuteGameUI.player2playedCard, currentCard);
                    
                    TuteGameUI.playTurn(currentCard, TuteGameUI.player2playedCard, player1Wins);
                    
                    TuteGameUI.player2playedCard = null;
                }
            }
        }
        
        HumanPlayer.clearSelection();
    },


    playTurn: function(card1, card2, player1Wins)
    {
        AnimationController.add(new WaitAnimator(20));
        
        if (player1Wins)
            AnimationController.add(TuteController.getPlayer1WinDeckMovement(card1, card2));
        else
            AnimationController.add(TuteController.getPlayer2WinDeckMovement(card1, card2));
        
        AnimationController.addF(function()
        {
			TuteGameUI.newTurn(player1Wins);
			
			return true;
        });
    },

    newTurn: function(player1WonLastTrick)
    {
        if (TuteGame.arePlayerCardsEmpty())
        {
            TuteGameUI.completeGame(player1WonLastTrick);
        }
        else
        {
            TuteGameUI.dealNewCards();
            
            AnimationController.add(TuteController.relocatePlayer1Cards(TuteGame.player1Cards));
            
            AnimationController.add(TuteController.relocatePlayer2Cards(TuteGame.player2Cards));
            
            AnimationController.addF(function()
            {
				GameUI.repaint();
		
				TuteGameUI.startTurn();
				
				return true;
            });
        }
    },

    completeGame(player1WonLastTrick)
    {
		HumanPlayer.clearSelection();
		
        let player1Wins = TuteGame.player1Baza.reverse();
        let player2Wins = TuteGame.player2Baza.reverse();
        
        AnimationController.add(TuteController.getCenterWinCardThrow(player1Wins));
        
        this.player1Points = 0;
        this.player2Points = 0;
        
        for (let card of player1Wins)
        {
            AnimationController.add(new MoveToFrontAnimator(card));
            AnimationController.add(TuteController.getCenterCardThrow(card));
            AnimationController.add(new ReverseAnimator(card, true));
            
            if (card.rank.countValue > 0)
            {
                AnimationController.addF(function()
                {
					TuteGameUI.player1Points += card.rank.countValue;
					TuteGameUI.updateGames();
					return true;
                });

                AnimationController.add(new MessageAnimator(Tr.getPlusPointsString(card.rank.countValue), 30));
            }
        }

        if (player1WonLastTrick)
        {
            AnimationController.add(new WaitAnimator(15));

            AnimationController.addF(function()
            {
				TuteGameUI.player1Points += 10;
				TuteGameUI.updateGames();
				return true;
            });

            AnimationController.add(new MessageAnimator(Tr.getPlus10DeMonteString(), 30));
        }

        for (let suit of Suit)
        {
            let declaration = TuteGame.getDeclaration(suit);
            
            if (declaration == "1")
            {
                AnimationController.add(new WaitAnimator(15));

                let points = suit == TuteGame.pinta.suit ? 40 : 20;
                
                AnimationController.addF(function()
                {
					TuteGameUI.player1Points += points;
					TuteGameUI.updateGames();
					return true;
                });

                AnimationController.add(new MessageAnimator(Tr.getPlusTwentyFortyPointsString(points, suit), 30));
            }
        }
        
        AnimationController.add(new WaitAnimator(50));
        
        AnimationController.add(TuteController.getCenterWinCardThrow(player2Wins));

        for (let card of player2Wins)
        {
            AnimationController.add(new MoveToFrontAnimator(card));
            AnimationController.add(TuteController.getCenterCardThrow(card));
            AnimationController.add(new ReverseAnimator(card, true));
            
            if (card.rank.countValue > 0)
            {
                AnimationController.addF(function()
                {
					TuteGameUI.player2Points += card.rank.countValue;
					TuteGameUI.updateGames();
					return true;
                });

                AnimationController.add(new MessageAnimator(Tr.getPlusPointsString(card.rank.countValue), 30));
            }
        }

        if (!player1WonLastTrick)
        {
            AnimationController.add(new WaitAnimator(15));

            AnimationController.addF(function()
            {
				TuteGameUI.player2Points += 10;
				TuteGameUI.updateGames();
				return true;
            });

            AnimationController.add(new MessageAnimator(Tr.getPlus10DeMonteString(), 30));
        }
        
        for (let suit of Suit)
        {
            let declaration = TuteGame.getDeclaration(suit);
            
            if (declaration == "2")
            {
                AnimationController.add(new WaitAnimator(15));

                let points = suit == TuteGame.pinta.suit ? 40 : 20;
                
                AnimationController.addF(function()
                {
					TuteGameUI.player2Points += points;
					TuteGameUI.updateGames();
					return true;
                });

                AnimationController.add(new MessageAnimator(Tr.getPlusTwentyFortyPointsString(points, suit), 30));
            }
        }
        
        AnimationController.addF(function()
        {
			TuteGame.countPointsAndRestart(player1WonLastTrick);
			
			return true;
        });
        
        AnimationController.add(new WaitAnimator(100));
        
        AnimationController.add(TuteController.getCenterCardMove(GameUI.cards));
        
        AnimationController.addF(function()
        {
			TuteGameUI.player1Points = TuteGameUI.player2Points = -1;

			TuteGameUI.initialize();
			
			return true;
        });
    },

    renuncio: function(currentCard)
    {
        TuteGame.declareRenuncio(this.player2playedCard, currentCard);
        
        this.abortGame(Tr.getDeclareRenuncioString());
    },
    
    tute: function(rank)
    {
        TuteGame.declareTute();
    
        this.abortGame(Tr.getTuteDeclarationString(rank));
    },
    
    abortGame: function(message)
    {
        AnimationController.add(new MessageAnimator(message, 100));
        
        AnimationController.add(TuteController.getCenterCardMove(GameUI.cards));

		HumanPlayer.clearSelection();
		
        AnimationController.addF(function()
        {
			TuteGame.countPointsAndRestart(TuteGame.player1Turn);
			TuteGameUI.initialize();
			return true;
        });
    },
	
	updateGames: () =>
	{
		if (TuteGameUI.player1Points > 0)
			$("#player1Points").html(Tr.getPlayerPointsString(TuteGameUI.player1Points));
		else
			$("#player1Points").html("");
		
		if (TuteGameUI.player2Points > 0)
			$("#player2Points").html(Tr.getPlayerPointsString(TuteGameUI.player2Points));
		else
			$("#player2Points").html("");

		$("#player2Message").html(Tr.getPlayerGamesString(TuteGame.player1Games, TuteGame.player2Games));
	}
}

$(() =>
{
	$("title").html(Tr.getWindowTitle());
	
    GameUI.initialize();

    $(window).resize(GameUI.onResize);

    GameUI.repaint();

    AnimationController.initialize();

    TuteGame.initialize();

    TuteGameUI.initialize();
});
