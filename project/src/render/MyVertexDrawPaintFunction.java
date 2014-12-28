
package render;

import graph.objects.Vertex;
import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;

/**
 * Transformuj vrchol na farbu vrchola ( len na farbu hran)
 * 
 * @author Lukas Sekerak
 */
public class MyVertexDrawPaintFunction implements Transformer<Vertex, Paint>
{
	protected Transformer<Vertex, Color>	trans;

	public MyVertexDrawPaintFunction(Transformer<Vertex, Color> trans) {
		this.trans = trans;
	}
	public Paint transform(Vertex v) {
		Color pixel = trans.transform(v);
		// TODO: Posielame referenciu nie kopiu, neviem ci je to dobre
		return pixel;
	}
}
