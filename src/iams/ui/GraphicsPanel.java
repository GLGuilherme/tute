package iams.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

abstract public class GraphicsPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private AffineTransform tx = new AffineTransform();

	public Point2D sceneToMap(Point2D point)
	{
		return this.tx.transform(point, null);
	}

	public Point2D mapToScene(Point point)
	{
		try
		{
			return this.tx.inverseTransform(point, null);
		}
		catch(NoninvertibleTransformException ex)
		{
			return null;
		}
	}
	
	public void initializeBoundingBox(Rectangle2D boundingBox)
	{
		double scale = Math.min(
				getWidth()  / boundingBox.getWidth(), 
				getHeight() / boundingBox.getHeight());

		this.tx.setToIdentity();

		this.tx.translate(getWidth() / 2, getHeight() / 2);
		this.tx.scale(scale, scale);
		this.tx.translate(- boundingBox.getMinX() - boundingBox.getWidth() / 2, 
				 	 - boundingBox.getMinY() - boundingBox.getHeight() / 2);
        
        this.repaint();
	}
	
	@Override
	public void paint(Graphics g)
	{
		this.paint((Graphics2D) g, this.tx);
	}

    public double getScreenFactor()
    {
        return 1.0 / this.tx.deltaTransform(new Point2D.Double(1, 0), null).getX();
    }

	abstract protected void paint(Graphics2D g2, AffineTransform tx2);
}
