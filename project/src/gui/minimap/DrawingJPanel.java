
package gui.minimap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Panel v ktorom sa zobrazuje len obrazok a do tohto obrazka
 * je mozne kreslit pixeli.
 * 
 * @author Lukas Sekerak 
 */
@SuppressWarnings("serial")
public class DrawingJPanel extends JPanel
{
	private BufferedImage	imageBuffer;
	protected Graphics2D	graphics;
	protected Dimension		size;

	public DrawingJPanel(Dimension size) {
		this.size = size;
		imageBuffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		graphics = imageBuffer.createGraphics();
	}
	
	public Dimension getPreferredSize() {
		return size;
	}
	
	public void paintComponent(Graphics graphic) {
		super.paintComponent(graphic);
		((Graphics2D) graphic).drawImage(imageBuffer, null, null);
	}
	
	public void fill(int color) {
		int x, y;
		for (x = 0; x < size.width; x++) {
			for (y = 0; y < size.height; y++) {
				imageBuffer.setRGB(x, y, color);
			}
		}
	}
	
	public void drawString(String txt, Color color, float x, float y) {
		graphics.setPaint(color);
		graphics.setFont(new Font("Tahoma", Font.BOLD, 12));
		graphics.drawString(txt, (int) (x * size.width), (int) (y * size.height));
	}
	
	public void putPixel(int x, int y, int rgb) {
		imageBuffer.setRGB(x, y, rgb);
	}
	
	public Graphics2D getGraphic() {
		return graphics;
	}
	
	/**
	 * Mame za ulohu zmensit / zvacsit elemnbt, treba obrazok
	 * pretransformovat.
	 */
	public void resize() {
		throw new UnsupportedOperationException();
	}
	
	public static void main(String[] args) {
		Dimension size = new Dimension(200, 100);
		DrawingJPanel panel = new DrawingJPanel(size);
		panel.fill(Color.WHITE.getRGB());
		panel.repaint();

		JFrame frame = new JFrame("");
		frame.add(panel);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
	}

}
