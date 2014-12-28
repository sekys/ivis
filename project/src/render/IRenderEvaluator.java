
package render;

import graph.objects.Edge;
import graph.objects.Vertex;

/**
 * Interface IRenderEvaluator
 * 
 * @author Lukas Sekerak
 */
public interface IRenderEvaluator
{

	/**
	 * Predicat sa zavola pred renderovanim vrcholov, vola sa to velmi malo krat
	 * takze si tu mozem nacachovat veci. Mame zobrazovat vrcholy ?
	 * 
	 * @return
	 */
	public boolean evaluateRenderVertices();

	/**
	 * Predicat - mame renderovat dany vrchol ?
	 * 
	 * @param v
	 * @return
	 */
	public boolean evaluateRenderVertex(Vertex v);

	/**
	 * Predicat, mame zobrazovat danu hranu ?
	 * 
	 * @param hrana
	 * @return
	 */
	public boolean evaluateRenderEdge(Edge hrana);

	/**
	 * Predicat, mame zobrazovat hrany pre dany vrchol ?
	 * 
	 * @param v
	 * @return
	 */
	public boolean evaluateRenderEdges(Vertex v);

}
