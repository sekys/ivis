
package gui.minimap;

import graph.VerticesPool;
import graph.objects.VertexGroup;
import graph.objects.Vertex;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import org.apache.log4j.Logger;

/**
 * VertexMapa, vrcholy sa kreslia do mapy.
 * 
 * @author Lukas Sekerak
 * 
 */
public class VertexMap extends Map implements Runnable
{
	protected VerticesPool		vertices;
	protected Point2D			world;
	
	private final static Logger	logger	= Logger.getLogger(Map.class.getName());

	public VertexMap(Dimension map, Point2D world, VerticesPool vertices) {
		super(map, new Color(255, 255, 255));
		this.world = world;
		this.vertices = vertices;

		Thread thread = new Thread(this, "MapBuilder");
		Thread.UncaughtExceptionHandler exh = new Thread.UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread th, Throwable ex) {
				logger.error(ex, ex);
				ex.printStackTrace();
			}
		};
		thread.setUncaughtExceptionHandler(exh);
		thread.start();
	}

	private void build() {
		logger.info("start");
		try {
			int color;
			for (VertexGroup group : vertices.getGroups()) {
				color = group.getColor().getRGB();
				// System.out.println("getColor " + group.getColor());
				for (Vertex vertex : group.getVertices()) {
					addItem(vertex, color);
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		logger.info("end");
	}
	@Override
	public void run() {
		try {
			// Spravyme si efekt spracovavania dat
			Thread.sleep(4000);
		}
		catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		panel.fill(bgColor);
		build();
		getPanel().repaint();
	}
}
