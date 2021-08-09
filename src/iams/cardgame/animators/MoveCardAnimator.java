package iams.cardgame.animators;

import iams.cardgame.tute.Card;

public class MoveCardAnimator implements AnimationController.Animator
{
    final public Card card;
    final public double x, y, rotation;
    
    public MoveCardAnimator(Card card, double x, double y, double rotation)
    {
        this.card = card;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
    
    @Override
    public boolean tick()
    {
        return this.card.stepTo(this.x, this.y, this.rotation, 30, 0);
    }
}
