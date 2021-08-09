package iams.cardgame.animators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimationController implements ActionListener
{
    public interface Animator
    {
        boolean tick();
    }
    
    final private JPanel panel;
    
    final private Timer t = new Timer(20, this);
    
    final private ArrayList<Animator> animators = new ArrayList<Animator>();

    public AnimationController(JPanel panel)
    {
        this.panel = panel;
        
        this.t.setRepeats(true);
        this.t.start();
    }

    public void add(Animator animator)
    {
        this.animators.add(animator);
    }

    public void repaint()
    {
        this.panel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (!this.animators.isEmpty())
        {
            Animator animator = this.animators.get(0);
            
            if (animator.tick())
            {
                this.animators.remove(0);
            }
            
            this.panel.repaint();
        }
    }
}
