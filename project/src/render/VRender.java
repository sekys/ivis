
package render;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * Extrahovana metoda z BasicVertexRenderer<V,E>, metoda je jemne upravena.
 * Funkcionalita ostala podobna.
 * 
 * @author JUNG Project
 * @author Lukas Sekerak - uprava
 * 
 * @param <V>
 * @param <E>
 */
public class VRender<V, E>
{
	protected void renderVertex(RenderContext<V, E> rc, V v, Shape shape) {
		GraphicsDecorator g = rc.getGraphicsContext();
		Paint oldPaint = g.getPaint();

		Paint fillPaint = rc.getVertexFillPaintTransformer().transform(v);
		if (fillPaint != null) {
			g.setPaint(fillPaint);
			g.fill(shape);
		}
		Paint drawPaint = rc.getVertexDrawPaintTransformer().transform(v);
		if (drawPaint != null) {
			g.setPaint(drawPaint);

			Stroke oldStroke = g.getStroke();
			Stroke stroke = rc.getVertexStrokeTransformer().transform(v);
			if (stroke != null) g.setStroke(stroke);

			g.draw(shape);

			g.setPaint(oldPaint);
			g.setStroke(oldStroke);
		}
		g.setPaint(oldPaint);
	}
}
