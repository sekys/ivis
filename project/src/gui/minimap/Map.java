
package gui.minimap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import org.apache.log4j.Logger;

/**
 * Vytvorenie akejsi Mapy, ktora kresli objekty do DrawingJPanel.
 * 
 * @author Lukas Sekerak 
 */
public abstract class Map
{
	protected DrawingJPanel		panel;
	protected int				bgColor;
	private final static Logger	logger	= Logger.getLogger(Map.class.getName());

	public Map(Dimension map, Color bg) {
		bgColor = bg.getRGB();
		panel = new DrawingJPanel(map);
		panel.fill(bgColor);
		panel.drawString("loading", Color.GRAY, 0.35f, 0.5f);
		logger.info(null);
	}

	/**
	 * Pouzival chce vytvorit zrekonstruvovat mapu
	 */
	public void resetMap(Dimension d) {
		throw new UnsupportedOperationException();
	}
	public void resetWorld(Dimension d) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Item zatial kreslime en ako jeden pixel, cez interface
	 * sa to da neskor zmenit a mozu sa kreslit rozne tvary.
	 * 
	 * Toto sa da urcite lepsie spravit !
	 * 
	 * @param pos
	 * @param color
	 */
	protected void addItem(Point2D pos, int color) {
		int localX, localY;
		localX = ((int) pos.getX()) % panel.getPreferredSize().width;
		localY = ((int) pos.getY()) % panel.getPreferredSize().height;
		panel.putPixel(localX, localY, color);
	}
	
	/**
	 * Doslo k zmene pozicii prvku, staru poziciu zafarby, novu zafarby
	 */
	protected void changeItem(Point2D old, Point2D pos, int color) {
		int localX, localY;
		localX = ((int) old.getX()) % panel.getPreferredSize().width;
		localY = ((int) old.getY()) % panel.getPreferredSize().height;
		panel.putPixel(localX, localY, bgColor);
		localX = ((int) pos.getX()) % panel.getPreferredSize().width;
		localY = ((int) pos.getY()) % panel.getPreferredSize().height;
		panel.putPixel(localX, localY, color);
	}
	
	public JPanel getPanel() {
		return panel;
	}
}
