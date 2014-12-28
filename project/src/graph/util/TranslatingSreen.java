
package graph.util;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.objects.Vertex;
import java.awt.geom.Point2D;
import org.apache.log4j.Logger;

/**
 * Trieda posuva obrazovku pomalicky k nejakemu bodu, urcite sa to da vylepsit...
 * 
 * @author Lukas Sekerak
 */
public class TranslatingSreen implements Runnable
{
	protected Thread								posuvac;
	protected VisualizationViewer<Vertex, Integer>	m_vv;
	private Point2D									delta;
	private Integer									step;
	private static final int						MAX_STEPS	= 10;

	private final static Logger						logger		= Logger.getLogger(TranslatingSreen.class.getName());

	public TranslatingSreen(VisualizationViewer<Vertex, Integer> vv) {
		m_vv = vv;
		step = MAX_STEPS;
		delta = new Point2D.Double();

		posuvac = new Thread(this, "Posuvac obrazovky");
		Thread.UncaughtExceptionHandler exh = new Thread.UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread th, Throwable ex) {
				logger.error(ex, ex);
				ex.printStackTrace();
			}
		};
		posuvac.setUncaughtExceptionHandler(exh);
		posuvac.start();
	}

	/**
	 * Posun obrazovku na poziciu.
	 * 
	 * @param chodKu
	 */
	public void goTo(Point2D chodKu) {
		Point2D stredObrazovky = m_vv.getRenderContext().getMultiLayerTransformer().inverseTransform(m_vv.getCenter());
		logger.info("Moving to " + chodKu);
		synchronized (step) {
			delta.setLocation((stredObrazovky.getX() - chodKu.getX())
					/ MAX_STEPS, (stredObrazovky.getY() - chodKu.getY())
					/ MAX_STEPS);
			step = 0;
		}
	}

	/**
	 * Nech posuvanie bezi vo vlakne.
	 */
	@Override
	public void run() {
		try {
			while (true) {
				synchronized (step) {
					if (step < MAX_STEPS) {
						m_vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(delta.getX(), delta.getY());
						step++;
					}
				}
				Thread.sleep(100);
			}
		}
		catch (InterruptedException ex) {}
	}

}
