
package render;

import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.objects.Vertex;
import java.awt.Color;
import java.awt.Paint;
import org.apache.commons.collections15.Transformer;

/**
 * Transformer z vrchola na farbu jeho vyplne.
 * 
 * @author Lukas Sekerak
 */
public class MyVertexFillPaintFunction implements Transformer<Vertex, Paint>
{
	protected PickedState<Vertex>	state;

	public MyVertexFillPaintFunction(PickedState<Vertex> state) {
		this.state = state;
	}

	/**
	 * Posli metode vrcholy ktore su oznacene pouzivatelom.
	 * 
	 * @param state
	 */
	public void setPickedState(PickedState<Vertex> state) {
		this.state = state;
	}

	/**
	 * Transformuj.
	 */
	public Paint transform(Vertex v) {
		if (state != null && state.isPicked(v)) return Color.LIGHT_GRAY;
		if (v.isZvyrazneny()) return Color.GREEN;
		if (v.isPacked()) return Color.MAGENTA;
		return null;
	}
}
