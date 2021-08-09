
import { Suit, Rank, CardModel, Card } from "./cards.js";

import { GameUI } from "./gameui.js";

export const AnimationController =
{
	animators : [],
	
	initialize : function()
	{
		setInterval(AnimationController.tick.bind(this), 20);
	},
	
	add: function(animator)
	{
		AnimationController.animators.push(animator);
	},
	
	addF: function(func)
	{
		AnimationController.animators.push({ tick: func });
	},
	
	tick: function()
	{
		if (AnimationController.animators.length > 0)
        {
            if (AnimationController.animators[0].tick())
            {
                AnimationController.animators.shift();
            }
        }
	}
}

export class MoveCardAnimator
{
	constructor(card, x, y, rotation)
	{
		this.card = card;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
	}
	
	tick()
	{
		return this.card.stepTo(this.x, this.y, this.rotation, 30, 0);
	}
}

export class ThrowCardAnimator
{
	constructor(card, x, y, rotation)
	{
		this.card = card;
		this.x = x - 30 + 60 * Math.random();
		this.y = y - 30 + 60 * Math.random();
		this.rotation = rotation - 0.5 + 1.0 * Math.random();
	}
	
	tick()
	{
		return this.card.stepTo(this.x, this.y, this.rotation, 30, 10);
	}
}

export class MoveToBackAnimator
{
	constructor(card)
	{
		this.card = card;
	}
	
	tick()
	{
		this.card.moveToBack();
		
		GameUI.repaint();
		
		return true;
	}
}

export class MoveToFrontAnimator
{
	constructor(card)
	{
		this.card = card;
	}
	
	tick()
	{
		this.card.moveToFront();
		
		GameUI.repaint();
		
		return true;
	}
}

export class ReverseAnimator
{
	constructor(card, faceUp)
	{
		this.card = card;
		this.faceUp = faceUp;
	}
	
	tick()
	{
		this.card.faceUp(this.faceUp);
		
		return true;
	}
}

export class MultiAnimator
{
	constructor()
	{
		this.animators = [];
	}
	
	add(animator)
	{
		this.animators.push(animator);
	}
	
	tick()
    {
		let remainingAnimators = [];
		
        if (this.animators.length > 0)
        {
            for (let animator of this.animators)
            {
                if (!animator.tick())
					remainingAnimators.push(animator);
            }
        }
		
		this.animators = remainingAnimators;
        
        return this.animators.length == 0;
	}
}

export class WaitAnimator
{
	constructor(delay)
	{
		this.delay = delay;
	}
	
	tick()
	{
		if (this.delay <= 0)
            return true;
        
        this.delay --;
        
        return false;
	}
}


export class MessageAnimator
{
	constructor(message, delay)
	{
		this.message = message;
		
		this.delay = delay;
	}
	
	tick()
	{
		if (this.delay > 0)
		{
			$("#message").html(this.message);

			this.delay --;
		
			return false;
		}
		else
		{
			$("#message").html("");

			return true;
		}
	
	}
}

export const TuteController =
{
    SEPARATION: 40,

	getDeckCardMove: function(cards)
    {
        const m1 = new MultiAnimator();
        
        for (let card of cards)
        {
            m1.add(new MoveCardAnimator(card, 20 + CardModel.WIDTH / 2, GameUI.BOARD_HEIGHT / 2, 0));
        }
                
        return m1;
    },

    getPintaMovement: function(card)
    {
        return new MoveCardAnimator(card, 40 + CardModel.HEIGHT / 2, GameUI.BOARD_HEIGHT / 2, 90);
    },

    getPlayer1PintaCardThrow: function(card)
    {
        return new ThrowCardAnimator(card, 40 + 3 * CardModel.HEIGHT / 2, GameUI.BOARD_HEIGHT / 2 + CardModel.HEIGHT, 0);
    },
    
    getPlayer2PintaCardThrow: function(card)
    {
        return new ThrowCardAnimator(card, 40 + 3 * CardModel.HEIGHT / 2, GameUI.BOARD_HEIGHT / 2 - CardModel.HEIGHT, 0);
    },
    
    getPlayer1CardThrow: function(card)
    {
        return new ThrowCardAnimator(card, GameUI.BOARD_WIDTH / 2, GameUI.BOARD_HEIGHT - CardModel.HEIGHT, 0);
    },

    getPlayer2PlayerCardThrow: function(card)
    {
        return new ThrowCardAnimator(card, GameUI.BOARD_WIDTH / 2, CardModel.HEIGHT, 0);
    },

    getCenterCardThrow: function(card)
    {
        return new ThrowCardAnimator(card, GameUI.BOARD_WIDTH / 2, GameUI.BOARD_HEIGHT / 2, 0);
    },
    
    getPlayer1CardMovement: function(card, i, numCards)
    {
        const centering = - (numCards - 1.) / 2 + i;
        
        return new MoveCardAnimator(card,
                GameUI.BOARD_WIDTH / 2 + TuteController.SEPARATION * centering, 
                GameUI.BOARD_HEIGHT - (CardModel.HEIGHT / 2 + 30 + (30 * Math.cos(Math.PI / 8 * centering))), 
                5 * centering);
    },

    getPlayer2CardMovement: function(card, i, numCards)
    {
        const centering = - (numCards - 1.) / 2 + i;
        
        return new MoveCardAnimator(card,
                GameUI.BOARD_WIDTH / 2 - TuteController.SEPARATION * centering, 
                CardModel.HEIGHT / 2 + 30 + (30 * Math.cos(Math.PI / 8 * centering)), 
                180 + 5 * centering);
    },

    relocatePlayer1Cards: function(player1)
    {
        const m1 = new MultiAnimator();
        
        let i = 0;
        
        for (let card of player1)
        {
            m1.add(new MoveToFrontAnimator(card));
            
            m1.add(this.getPlayer1CardMovement(card, i ++, player1.length));
        }
                
        return m1;
    },

    relocatePlayer2Cards: function(player2)
    {
        const m1 = new MultiAnimator();
        
        let i = 0;
        
        for (let card of player2)
        {
            m1.add(new MoveToFrontAnimator(card));
            
            m1.add(this.getPlayer2CardMovement(card, i ++, player2.length));

        }
                
        return m1;
    },

    getPlayer1WinDeckMovement: function(card1, card2)
    {
        const m1 = new MultiAnimator();
        
        m1.add(new ThrowCardAnimator(card1, GameUI.BOARD_WIDTH, GameUI.BOARD_HEIGHT - CardModel.HEIGHT, 0));
        m1.add(new ThrowCardAnimator(card2, GameUI.BOARD_WIDTH, GameUI.BOARD_HEIGHT - CardModel.HEIGHT, 0));
        
        m1.add(new ReverseAnimator(card1, false));
        m1.add(new ReverseAnimator(card2, false));
        
        return m1;
    },

    getPlayer2WinDeckMovement: function(card1, card2)
    {
        const m1 = new MultiAnimator();
        
        m1.add(new ThrowCardAnimator(card1, GameUI.BOARD_WIDTH, CardModel.HEIGHT, 0));
        m1.add(new ThrowCardAnimator(card2, GameUI.BOARD_WIDTH, CardModel.HEIGHT, 0));
        
        m1.add(new ReverseAnimator(card1, false));
        m1.add(new ReverseAnimator(card2, false));
        
        return m1;
    },

    getCenterWinCardThrow: function(cards)
    {
        const m1 = new MultiAnimator();
        
        for (let card of cards)
        {
            m1.add(new MoveCardAnimator(card, 
                    card.x - GameUI.BOARD_WIDTH / 2, 
                    card.y, card.rotation));
        }
                
        return m1;
    },

    getCenterCardMove: function(cards)
    {
        const m1 = new MultiAnimator();
        
        for (let card of cards)
        {
            m1.add(new MoveCardAnimator(card, GameUI.BOARD_WIDTH / 2, GameUI.BOARD_HEIGHT / 2, 0));
        }
                
        return m1;
    }
}
