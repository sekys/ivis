
package render;

import java.awt.BasicStroke;
import java.awt.Stroke;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.picking.PickedInfo;

/**
 * Trieda prevzata z PluggableRendererDemo.VertexStrokeHighlight.
 * Boli pridane prvky pre lepsiu vizualizaciu
 * 
 * @author JUNG Project
 * @author Lukas Sekerak - edited
 * @param <V>
 * @param <E>
 */
public class VertexStrokeHighlight<V, E> implements Transformer<V, Stroke>
{
	protected boolean		allowed	= false;
	protected Stroke		big		= new BasicStroke(5);
	protected Stroke		mid		= new BasicStroke(3);
	protected Stroke		small	= new BasicStroke(1);
	protected PickedInfo<V>	pi;
	protected Graph<V, E>	graph;

	public VertexStrokeHighlight(Graph<V, E> graph, PickedInfo<V> pi) {
		this.graph = graph;
		this.pi = pi;
	}
	
	/**
	 * Zapni zvyraznenie vrcholov.
	 * @param a
	 */
	public void setHighlight(boolean a) {
		allowed = a;
	}
	
	/**
	 * Transformuj vrchol na stroke.
	 */
	public Stroke transform(V v) {
		if (allowed) {
			if (pi.isPicked(v)) {
				return big;
			} else {
				// Ak vrchol nieje oznaceny ale nas vrchol je susedom nejakeho
				// tak ho zvyrazni
				for (V w : graph.getNeighbors(v)) {
					if (pi.isPicked(w)) return mid;
				}
				return small;
			}
		}
		return null;
	}
}
