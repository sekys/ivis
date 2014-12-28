
package render;

import java.awt.Shape;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import graph.GraphHolder;
import gui.tooltip.IStatus;

/**
 * Trieda pridava do render casti statistiku.
 * Statistika sa potom vyhodnocuje, aby sme vedeli aky pocet objektov zobrazujeme.
 * Pomaha nam to upozronit pouzivatela nech pouzije filter.
 * 
 * @author Lukas Sekerak
 */
public class RenderWithStats extends RenderThroughtQuadTree
{
	private int		eCount;
	private int		vCount;
	private IStatus	status;

	public RenderWithStats(GraphHolder holder, IStatus status) {
		super(holder);
		this.status = status;
	}

	protected void renderEdge(RenderContext<graph.objects.Vertex, Integer> rc,
			Layout<graph.objects.Vertex, Integer> layout, Integer e,
			graph.objects.Vertex v1, graph.objects.Vertex v2) {
		eCount++;
		super.renderEdge(rc, layout, e, v1, v2);
	}
	
	protected void renderVertex(graph.objects.Vertex v, Shape shape) {
		vCount++;
		super.renderVertex(v, shape);
	}

	protected void frameStart() {
		//perftest = new Debug();
		eCount = vCount = 0;
		super.frameStart();
	}

	private boolean	prazdnaObrazovka	= true;
	private boolean	velaEntit			= true;

	protected void frameEnd() {
		super.frameEnd();
		if (vCount == 0 && prazdnaObrazovka) {
			prazdnaObrazovka = false;
			status.setStatus("...nezobrazuj˙ sa vrcholy, sk˙s zmeÚiù filter vrcholov.", 2);
		}
		if (status.getStatus() == 2 && vCount > 0) {
			status.setStatus("...ok.", 3);
		}
		if (velaEntit && (eCount + vCount) > 100) {
			velaEntit = false;
			status.setStatus("...sk˙s pouûiù filter vrcholov.", 4);

		}
		// perftest.End("Vyfiltrovane na " + vCount + " vertexov " + eCount
		// + " hran " + control);
	}

}
