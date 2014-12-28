
package render;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import graph.GraphHolder;
import graph.objects.Edge;
import graph.objects.Vertex;

/**
 * Trieda v sebe uklada konfiguraciu aktualneho renderu.
 * Render podla tej triedy a jej nastaveni zobrazuje prvky.
 * 
 * @author Lukas Sekerak
 */
public class DisplayControl implements ISearchText, IRenderEvaluator
{
	// Vrcholy
	protected boolean								vZoomAutoFilter;
	protected int									minSusedov;
	protected float									autoMinVolume;
	protected float									manualMinVolume;
	protected float									vertexMinVolume;
	protected Transformer<Vertex, Float>			vertexVolumeTrans;

	// Hrany
	protected boolean								edgeRenderAllow;
	protected boolean								edgeOnlyPicked;
	protected float									edgeMinVolume;
	protected Transformer<Edge, Float>				edgeVolumeTrans;

	// Ostatne
	protected Graph<Vertex, Integer>				graph;
	protected GraphHolder							holder;
	protected PickedInfo<Vertex>					pickedState;
	protected Pattern								hladanyretazec;
	protected VisualizationViewer<Vertex, Integer>	vv;
	protected int									maxTreshold;

	public DisplayControl(Graph<Vertex, Integer> g,
			Transformer<Vertex, Float> vTrans,
			Transformer<Edge, Float> eTrans) {
		graph = g;
		vertexVolumeTrans = vTrans;
		edgeVolumeTrans = eTrans;
		hladanyretazec = null;
	}

	private Transformer<Vertex, String>	cachedLabelTrans;

	/*
	 * (non-Javadoc)
	 * 
	 * @see render.IRenderEvaluator#evaluateRenderVertices()
	 */
	@Override
	public boolean evaluateRenderVertices() {
		cachedLabelTrans = vv.getRenderContext().getVertexLabelTransformer();
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see render.IRenderEvaluator#evaluateRenderVertex(graph.objects.Vertex)
	 */
	@Override
	public boolean evaluateRenderVertex(Vertex v) {
		if (!v.isVisible()) return false;
		if (vertexMinVolume > 0.0
				&& (vertexVolumeTrans.transform(v) < vertexMinVolume)) return false;
		if (minSusedov > 0 && graph.getNeighborCount(v) <= minSusedov) return false;

		// Filtrovanie podla labelu
		if (cachedLabelTrans != null && hladanyretazec != null) {
			// matches je zly
			if (hladanyretazec.matcher(cachedLabelTrans.transform(v)).find() == false) return false;
		}
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see render.IRenderEvaluator#evaluateRenderEdge(graph.objects.Edge)
	 */
	@Override
	public boolean evaluateRenderEdge(Edge hrana) {
		if (edgeMinVolume > 0.0
				&& (edgeVolumeTrans.transform(hrana) < edgeMinVolume)) return false;
		return true;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see render.IRenderEvaluator#evaluateRenderEdges(graph.objects.Vertex)
	 */
	@Override
	public boolean evaluateRenderEdges(Vertex v) {
		if (!edgeRenderAllow) return false;
		if (edgeOnlyPicked && !pickedState.isPicked(v)) return false;
		return true;
	}

	private void minVolumeFix() {
		if (vZoomAutoFilter) {
			this.vertexMinVolume = Math.max(manualMinVolume, autoMinVolume);
		} else {
			this.vertexMinVolume = manualMinVolume;
		}
	}
	
	public void setMinSusedov(int minDegree) {
		this.minSusedov = minDegree;
	}
	public void setManualMinVolume(float min) {
		this.manualMinVolume = min;
		minVolumeFix();
	}
	public void setFilterByAutoZoomVolume(boolean filterByAutoZoomVolume) {
		this.vZoomAutoFilter = filterByAutoZoomVolume;
		minVolumeFix();
	}
	public void setAutoMinVolume(float autoMinVolume) {
		this.autoMinVolume = autoMinVolume;
		minVolumeFix();
	}
	public boolean isFilterByAutoZoomVolume() {
		return vZoomAutoFilter;
	}
	public boolean isEdgeRenderAllow() {
		return edgeRenderAllow;
	}
	public void setEdgeRenderAllow(boolean edgeRenderAllow) {
		this.edgeRenderAllow = edgeRenderAllow;
	}
	public boolean isEdgeOnlyPicked() {
		return edgeOnlyPicked;
	}
	public void setEdgeOnlyPicked(boolean edgeOnlyPicked) {
		this.edgeOnlyPicked = edgeOnlyPicked;
	}
	public float getEdgeMinVolume() {
		return edgeMinVolume;
	}
	public void setEdgeMinVolume(float edgeMinVolume) {
		this.edgeMinVolume = edgeMinVolume;
	}
	public String toString() {
		return vertexMinVolume + " vertex volume " + minSusedov + " susedov "
				+ edgeMinVolume + " edge volume";
	}
	public void setVisualizationViewer(VisualizationViewer<Vertex, Integer> vv) {
		this.pickedState = vv.getPickedVertexState();
		this.vv = vv;
	}
	public Pattern getHladanyretazec() {
		return hladanyretazec;
	}
	@Override
	public void setHladanyretazec(Pattern ret) throws PatternSyntaxException {
		this.hladanyretazec = ret;
	}

	public int getMaxTreshold() {
		return maxTreshold;
	}

	public void setMaxTreshold(int maxTreshold) {
		this.maxTreshold = maxTreshold;
	}
}
