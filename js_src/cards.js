
export const Suit = [
    { "index": 0, "name": "Coins" },
    { "index": 1, "name": "Cups" },
    { "index": 2, "name": "Swords" },
    { "index": 3, "name": "Batons" },
];

export const Rank = [
    { "index":  0, "name": "Ace",    "relativeValue": 12, "countValue": 11 },
    { "index":  1, "name": "V2",     "relativeValue":  1, "countValue":  0 },
    { "index":  2, "name": "V3",     "relativeValue": 11, "countValue": 10 },
    { "index":  3, "name": "V4",     "relativeValue":  2, "countValue":  0 },
    { "index":  4, "name": "V5",     "relativeValue":  3, "countValue":  0 },
    { "index":  5, "name": "V6",     "relativeValue":  4, "countValue":  0 },
    { "index":  6, "name": "V7",     "relativeValue":  5, "countValue":  0 },
    { "index":  7, "name": "V8",     "relativeValue":  6, "countValue":  0 },
    { "index":  8, "name": "V9",     "relativeValue":  7, "countValue":  0 },
    { "index":  9, "name": "Knave",  "relativeValue":  8, "countValue":  2 },
    { "index": 10, "name": "Knight", "relativeValue":  9, "countValue":  3 },
    { "index": 11, "name": "King",   "relativeValue": 10, "countValue":  4 }
];

export const CardModel =
{
    WIDTH: 80,

    HEIGHT: 123,

    minZ: 0,

    maxZ: 0,

    suitFromName: function(name)
    {
        for (let suit of Suit)
        {
            if (suit.name == name)
                return suit;
        }

        return null;
    },

    rankFromName: function(name)
    {
        for (let rank of Rank)
        {
            if (rank.name == name)
                return rank;
        }

        return null;
    }
};

export class Card
{
    constructor(s, r)
    {
        this.s = s;
        this.r = r;

        this.suit = Suit[s];
        this.rank = Rank[r];

        this.name = this.rank.name + "-" + this.suit.name;

        this.x = 0;
        this.y = 0;
        this.rotation = 0;

        this.z = 0;
        this.moveToFront();
		
		this.highlightingEnabled = false;
    }

    getCardName()
    {
        return this.name;
    }

    moveTo(x, y, rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;

        $("#" + this.getCardName()).attr("transform",
            'translate(' + x + ',' + y + ') rotate(' + rotation + ',0,0)');
    }

    stepTo(x, y, rotation, moveSpeed, rotSpeed)
    {
        const d = Math.sqrt((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y));

        let movementCompleted = true;

        let newX = 0, newY = 0, newRotation = 0;

        if (d < moveSpeed)
        {
            newX = x;
            newY = y;

            if (rotSpeed != 0)
                newRotation = this.rotation + rotSpeed;
            else
                newRotation = rotation;
        }
        else
        {
            const theta = Math.atan2(y - this.y, x - this.x);

            newX = this.x + moveSpeed * Math.cos(theta);
            newY = this.y + moveSpeed * Math.sin(theta);

            if (rotSpeed != 0)
            {
                newRotation = this.rotation + rotSpeed;
            }
            else
            {
                const ratioMovement = moveSpeed / d;

                newRotation = this.rotation + ratioMovement * (rotation - this.rotation);
            }

            movementCompleted = false;
        }

        this.moveTo(newX, newY, newRotation);

        return movementCompleted;
    }

    faceUp(faceUp)
    {
        let cardName = this.getCardName();

        $("#" + cardName + "-COVER").attr("visibility", faceUp ? "hidden" : "visible");
        $("#" + cardName + "-FRONT").attr("visibility", faceUp ? "visible" : "hidden");
    }
	
	setHighlightColor(color)
    {
        let cardName = this.getCardName();

        $("#" + cardName + "-RECT").attr("visibility", !this.highlightingEnabled || color == null ? "hidden" : "visible");

        if (this.highlightingEnabled && color != null)
            $("#" + cardName + "-RECT").attr("stroke", color);
    }

    moveToFront()
    {
        this.z = CardModel.maxZ ++;
    }

    moveToBack()
    {
        this.z = CardModel.minZ --;
    }

}
