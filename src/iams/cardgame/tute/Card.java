package iams.cardgame.tute;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.Comparator;

public class Card extends CardModel
{
    public static final Comparator<Card> PAINT_COMPARATOR = new Comparator<Card>()
    {
        @Override
        public int compare(Card o1, Card o2)
        {
            return -Integer.compare(o1.zValue, o2.zValue);
        }
    };
    
    static private int MIN_Z_VALUE = 0;
    static private int MAX_Z_VALUE = 0;
    
    public boolean faceUp = false;
    private double x, y, rotation;
    private AffineTransform at, iat;
    private Color highlightColor;
    private boolean highlightingEnabled;
    private int zValue = MAX_Z_VALUE ++;
    
    Card(CardModel card)
    {
        super(card.suit, card.rank, card.image);
    }

    Card(CardModel card, double x, double y, double rotation)
    {
        super(card.suit, card.rank, card.image);
        
        this.moveTo(x, y, rotation);
    }
    
    public boolean stepTo(double x, double y, double rotation, double moveSpeed, double rotSpeed)
    {
        double d = Math.sqrt((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y));
        
        boolean movementCompleted = true;
        
        double newX = 0, newY = 0, newRotation = 0;
        
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
            double theta = Math.atan2(y - this.y, x - this.x);
            
            newX = this.x + moveSpeed * Math.cos(theta);
            newY = this.y + moveSpeed * Math.sin(theta);

            if (rotSpeed != 0)
            {
                newRotation = this.rotation + rotSpeed;
            }
            else
            {
                double ratioMovement = moveSpeed / d;
                
                newRotation = this.rotation + ratioMovement * (rotation - this.rotation);
            }
            
            movementCompleted = false;
        }
        
        this.moveTo(newX, newY, newRotation);

        return movementCompleted;
    }

    public void moveTo(double x, double y, double rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        
        this.at = new AffineTransform();
        this.at.translate(this.x, this.y);
        this.at.rotate(Math.PI * this.rotation / 180);
        this.at.translate(- CardModel.WIDTH / 2, - CardModel.HEIGHT / 2);

        try
        {
            this.iat = this.at.createInverse();
        }
        catch (NoninvertibleTransformException e)
        {
            throw new AssertionError(e);
        }
    }

    public void moveToFront()
    {
        this.zValue = MIN_Z_VALUE --;
    }

    public void moveToBack()
    {
        this.zValue = MAX_Z_VALUE ++;
    }

    public void draw(Graphics2D g, AffineTransform tx, ImageObserver observer)
    {
        AffineTransform prevAt = new AffineTransform(g.getTransform());
        g.transform(this.at);
        
        g.drawImage(this.faceUp ? this.image : CardModel.BACK, 0, 0, observer);
        
        if (this.highlightingEnabled && this.highlightColor != null)
        {
            g.setColor(this.highlightColor);
            
            final int HIGHLIGHT_BORDER = 2;
            
            g.setStroke(new BasicStroke(3.0f));
            
            g.drawRect(-1 - HIGHLIGHT_BORDER / 2, 
                       -1 - HIGHLIGHT_BORDER / 2, 
                       Card.WIDTH + 2 + HIGHLIGHT_BORDER, 
                       Card.HEIGHT + 2 + HIGHLIGHT_BORDER);
        }
            
        
        g.setTransform(prevAt);
    }

    public boolean containsPoint(Point2D p)
    {
        p = this.iat.transform(p, null);
        
        return p.getX() > 0 && p.getX() < Card.WIDTH &&
                p.getY() > 0 && p.getY() < Card.HEIGHT;
    }
    
    public void setHighlightColor(Color color)
    {
        this.highlightColor = color;
    }

    public void enableHighlight(boolean b)
    {
        this.highlightingEnabled = b;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getRotation()
    {
        return this.rotation;
    }

}
