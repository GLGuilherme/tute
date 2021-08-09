
package iams.cardgame.tute;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import iams.cardgame.tute.tr.Translator;
import iams.ui.GraphicsPanel;

@SuppressWarnings("serial")
public class Main extends GraphicsPanel
{
    final public Translator tr = Translator.get();
    
    private static final Font FONT = new Font("Times New Roman", Font.BOLD, 28); //$NON-NLS-1$
    
    private static final Font SMALL_FONT = new Font("Times New Roman", Font.BOLD, 22); //$NON-NLS-1$
    
    private static final Color BACKGROUND_COLOR = new Color(13, 98, 69);
    
    public static final int BOARD_WIDTH = 700, BOARD_HEIGHT = 700;

    private final TuteController controller = new TuteController(this);
    
    final public TuteGame game = new TuteGame();
    
    final private HumanPlayer humanPlayer = new HumanPlayer(this.game, this.tr);
    
    private final TuteGameUI gameUI = new TuteGameUI(this.tr, this.game, this.controller, this.humanPlayer);

    private final GameMouseListener mouseListener = new GameMouseListener(this, this.game, this.gameUI, this.humanPlayer);

    public Main()
    {
        this.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentShown(ComponentEvent e)
            {
                this.componentResized(e);
            }
            
            @Override
            public void componentResized(ComponentEvent e)
            {
                Main.this.initializeBoundingBox(new Rectangle2D.Double(0, 0, BOARD_WIDTH, BOARD_HEIGHT));
            }
        });
        
        this.addMouseListener(this.mouseListener);
        this.addMouseMotionListener(this.mouseListener);
    }
    
    @Override
    protected void paint(Graphics2D g2, AffineTransform tx2)
    {
        this.mouseListener.mouseMoved();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());

        g2.setFont(FONT);
        FontMetrics fm = g2.getFontMetrics();
        
        g2.transform(tx2);
        
        g2.setColor(Color.yellow);
        
        if (this.gameUI.getPlayer1Points() >= 0)
        {
            String pointsString = this.tr.getPlayerPointsString(this.gameUI.getPlayer1Points());
            g2.drawString(pointsString, 150 - fm.stringWidth(pointsString), BOARD_HEIGHT - 100);
        }

        if (this.gameUI.getPlayer2Points() >= 0)
        {
            String pointsString = this.tr.getPlayerPointsString(this.gameUI.getPlayer2Points());
            g2.drawString(pointsString, 150 - fm.stringWidth(pointsString), 100 + fm.getMaxAscent());
        }

        g2.setColor(Color.white);
        
        String notification = this.gameUI.getNotification();
        
        if (notification != null && !notification.isEmpty())
        {
            g2.drawString(notification, BOARD_WIDTH / 2 + Card.HEIGHT, BOARD_HEIGHT / 2 + fm.getMaxAscent() / 2);
        }

        g2.setFont(SMALL_FONT);
        FontMetrics fm2 = g2.getFontMetrics();

        g2.setColor(new Color(152, 251, 152));

        String gameMessage = this.tr.getPlayerGamesString(
                this.game.getPlayer1Games(), 
                this.game.getPlayer2Games());
        
        g2.drawString(gameMessage, 
                BOARD_WIDTH / 2 - fm.stringWidth(gameMessage) / 2, 
                5 + fm.getMaxAscent());

        if (this.humanPlayer.player1Message != null && !this.humanPlayer.player1Message.isEmpty())
        {
            g2.drawString(this.humanPlayer.player1Message, 
                    BOARD_WIDTH / 2 - fm2.stringWidth(this.humanPlayer.player1Message) / 2, 
                    BOARD_HEIGHT - fm2.getMaxAscent());
        }

        Card[] cardRasters = this.game.getCardRasters().toArray(new Card[0]);
        
        Arrays.sort(cardRasters, Card.PAINT_COMPARATOR);
        
        for (Card card : cardRasters)
            card.draw(g2, tx2, this);
    }
    
    static public void main(String[] args) throws IOException
    {
        final Translator tr = Translator.get();
        
        BufferedImage appIcon = ImageIO.read(
                CardModel.class.getResourceAsStream("/iams/cardgame/icon.png"));
        
        JFrame frame = new JFrame(tr.getWindowTitle());
        frame.setIconImage(appIcon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.add(new Main());
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}
