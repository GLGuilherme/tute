package iams.cardgame.animators;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import iams.cardgame.tute.Card;

public class ThrowCardAnimator implements AnimationController.Animator
{
    final private Random r = ThreadLocalRandom.current();
    
    final public Card card;
    final public double x, y, rotation;
    
    public ThrowCardAnimator(Card card, double x, double y, double rotation)
    {
        this.card = card;
        this.x = x - 30 + 60 * this.r.nextDouble();
        this.y = y - 30 + 60 * this.r.nextDouble();
        this.rotation = rotation - 0.5 + 1.0 * this.r.nextDouble();
    }
    
    @Override
    public boolean tick()
    {
        return this.card.stepTo(this.x, this.y, this.rotation, 30, 10);
    }
}
