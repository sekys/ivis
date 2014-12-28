
package gui.control;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import quadtree.QuadTree;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import graph.objects.Vertex;

/**
 * Presuvanie vrcholov po pracovnej ploche.
 * 
 * @author Lukas Sekerak
 */
public class PickingPlugin extends PickingGraphMousePlugin<Vertex, Integer>
{
	private QuadTree<Vertex>	quad;
	private final static Logger	logger	= Logger.getLogger(PickingPlugin.class.getName());

	public PickingPlugin(QuadTree<Vertex> quad) {
		this.quad = quad;
		logger.info(null);
	}
	/**
	 * If the mouse is over a picked vertex, drag all picked
	 * vertices with the mouse.
	 * If the mouse is not over a Vertex, draw the rectangle
	 * to select multiple Vertices
	 * 
	 * @author Tom Nelson
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void mouseDragged(MouseEvent e) {
		if (locked == false) {
			VisualizationViewer<Vertex, Integer> vv = (VisualizationViewer) e.getSource();
			if (vertex != null) {
				Point p = e.getPoint();
				Point2D graphPoint = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p);
				Point2D graphDown = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
				double dx = graphPoint.getX() - graphDown.getX();
				double dy = graphPoint.getY() - graphDown.getY();
				pohybVrcholov(vv, dx, dy);
				down = p;

			} else {
				Point2D out = e.getPoint();
				if (e.getModifiers() == this.addToSelectionModifiers
						|| e.getModifiers() == modifiers) {
					rect.setFrameFromDiagonal(down, out);
				}
			}
			if (vertex != null) e.consume();
			vv.repaint();
		}
	}

	private void pohybVrcholov(VisualizationViewer<Vertex, Integer> vv, double dx,
			double dy) {
		try {
			Set<Vertex> vertices = vv.getPickedVertexState().getPicked();
			Layout<Vertex, Integer> layout = vv.getGraphLayout();
			Point2D oldPos, newPos;
			for (Vertex v : vertices) {
				oldPos = layout.transform(v);
				newPos = new Point2D.Double(oldPos.getX() + dx, oldPos.getY()
						+ dy);

				if (!quad.intersects(newPos)) {
					JOptionPane.showMessageDialog(vv, "Vrchol nemÙûe byù umiestnen˝ prÌliû Ôaleko.", "Editor", 0);
					break; // A skonci
				} else {
					// oldPos.setLocation(newPos); netreba layout to spravy
					layout.setLocation(v, newPos); // vp
				}
			}
		}
		catch (IndexOutOfBoundsException exc) {
			exc.printStackTrace();
		}
	}
}
