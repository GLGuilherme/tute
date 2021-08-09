package iams.cardgame.animators;

import iams.cardgame.tute.Card;

public class MoveToBackAnimator implements AnimationController.Animator
{
    final private Card card;

    public MoveToBackAnimator(Card card)
    {
        this.card = card;
    }

    @Override
    public boolean tick()
    {
        this.card.moveToBack();
        
        return true;
    }

}
