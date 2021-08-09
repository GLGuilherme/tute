package iams.cardgame.tute;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Arrays;

import iams.cardgame.tute.movement.Movement;
import iams.ui.GraphicsPanel;

public class GameMouseListener implements MouseListener, MouseMotionListener
{
    private final GraphicsPanel graphicsPanel;
    private final TuteGame game;
    private final TuteGameUI gameUI;
    private final HumanPlayer human;
    
    private Point2D currentMousePoint;
    private Card hoverCard = null;

    public GameMouseListener(GraphicsPanel graphicsPanel, TuteGame game, TuteGameUI gameUI, HumanPlayer human)
    {
        this.graphicsPanel = graphicsPanel;
        this.game = game;
        this.gameUI = gameUI;
        this.human = human;
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        this.currentMousePoint = this.graphicsPanel.mapToScene(e.getPoint());
        
        this.mouseMoved();
    }
    
    public void mouseMoved()
    {
        if (this.currentMousePoint == null)
            return;
        
        Card[] cardRasters = this.game.getCardRasters().toArray(new Card[0]);
        
        Arrays.sort(cardRasters, Card.PAINT_COMPARATOR);
        
        for (int i = cardRasters.length - 1; i >= 0; i--)
        {
            Card card = cardRasters[i];
            
            if (card.containsPoint(this.currentMousePoint))
            {
                if (this.hoverCard != card)
                {
                    if (this.hoverCard != null)
                        this.human.onMouseOver(this.hoverCard, false);
                    
                    this.hoverCard = card;

                    if (this.hoverCard != null)
                        this.human.onMouseOver(this.hoverCard, true);
                    
                    this.graphicsPanel.repaint();
                }
                
                return;
            }
        }

        if (this.hoverCard != null)
        {
            if (this.hoverCard != null)
                this.human.onMouseOver(this.hoverCard, false);
            
            this.hoverCard = null;
            
            this.graphicsPanel.repaint();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            Movement movement = this.human.getPlayerMovement();
            
            if (movement != null)
            {
                this.gameUI.onHumanMovement(movement);
            }
            
            this.graphicsPanel.repaint();
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
}
