
import { Tr } from "./tr-es.js";

export class PintaMovement
{
    toString()
    {
        return Tr.getChangePintaString();
    }
}

export class ThrowMovement
{
    constructor(currentCard)
    {
        this.currentCard = currentCard;
    }

    toString()
    {
        return Tr.getCardNameString(this.currentCard);
    }
}

export class ThrowResponseMovement
{
    constructor(currentCard)
    {
        this.currentCard = currentCard;
    }

    toString()
    {
        return Tr.getCardNameString(this.currentCard);
    }
}

export class TuteMovement
{
    constructor(rank)
    {
        this.rank = rank;
    }

    toString()
    {
        return Tr.getTuteDeclarationString(this.rank);
    }

}

export class TwentyFortyMovement
{
    constructor(pintaSuit, suit)
    {
        this.pintaSuit = pintaSuit;
        this.suit = suit;
    }

    toString()
    {
        return Tr.getTwentyFortyDeclarationString(this.pintaSuit, this.suit);
    }
}