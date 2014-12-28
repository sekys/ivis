
package graph;

import java.awt.geom.Point2D;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import graph.objects.Vertex;

/**
 * Moja vlastna trieda odovdena od ISOMLayout.
 * Trieda pretazuje setInitializer s cielom nastavit
 * permanentny pozicny transformer
 * 
 * @author Lukas Sekerak
 * 
 */
public class MyLayout extends ISOMLayout<Vertex, Integer>
{
	public MyLayout(DirectedGraph<Vertex, Integer> graph) {
		super(graph);
	}

	/**
	 * Nastav permanentny pozicny transformer
	 */
	public void setInitializer(Transformer<Vertex, Point2D> initializer) {
		super.setInitializer(GraphHolder.getPositionTransformer());
	}

	/**
	 * Pretransformuj vrchol na poziciu vrchola na pracovnej ploche.
	 */
	public Point2D transform(Vertex v) {
		return v;
	}
}
