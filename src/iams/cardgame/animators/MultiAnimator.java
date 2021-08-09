package iams.cardgame.animators;

import java.util.ArrayList;

import iams.cardgame.animators.AnimationController.Animator;

public class MultiAnimator implements AnimationController.Animator
{
    final private ArrayList<Animator> animators = new ArrayList<Animator>();

    public void add(Animator animator)
    {
        this.animators.add(animator);
    }

    @Override
    public boolean tick()
    {
        if (!this.animators.isEmpty())
        {
            for (Animator animator : new ArrayList<Animator>(this.animators))
            {
                if (animator.tick())
                {
                    this.animators.remove(animator);
                }
            }
        }
        
        return this.animators.isEmpty();
    }
}
