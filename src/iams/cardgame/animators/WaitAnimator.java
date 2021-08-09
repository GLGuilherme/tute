package iams.cardgame.animators;

public class WaitAnimator implements AnimationController.Animator
{
    private int delay;
    
    public WaitAnimator(int delay)
    {
        this.delay = delay;
    }
    
    @Override
    public boolean tick()
    {
        if (this.delay <= 0)
            return true;
        
        this.delay --;
        
        return false;
    }
}