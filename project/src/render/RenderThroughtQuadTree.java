
package render;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections15.Transformer;
import quadtree.IQuadTree;
import quadtree.QuadTree;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import graph.GraphHolder;

/**
 * Render spolupracujuci s quad tree, upraveny pre lepsiu performance.
 * 
 * @author Lukas Sekerak
 */
public class RenderThroughtQuadTree
		extends
			BasicRenderer<graph.objects.Vertex, Integer>
		implements
		IQuadTree.IWalkThrought<graph.objects.Vertex>
{
	private Rectangle										deviceRectangle;
	private Transformer<Integer, String>					edgeTransformer;
	private VRender<graph.objects.Vertex, Integer>			vRender;
	private ERender<graph.objects.Vertex, Integer>			eRender;

	protected DisplayControl								control;
	protected Layout<graph.objects.Vertex, Integer>			layout;
	protected RenderContext<graph.objects.Vertex, Integer>	rc;
	protected GraphHolder									holder;

	public RenderThroughtQuadTree(GraphHolder holder) {
		this.holder = holder;
		vRender = new VRender<graph.objects.Vertex, Integer>();
		eRender = new ERender<graph.objects.Vertex, Integer>();
	}

	/**
	 * Prechadzaj quad tree a renderuj objekty.
	 */
	public void walkThrought(ArrayList<graph.objects.Vertex> elements) {
		Shape shape;
		while (true) {
			try {
				if (!control.evaluateRenderVertices()) return;
				for (graph.objects.Vertex v : elements) {
					if (!control.evaluateRenderVertex(v)) continue;
					shape = makeShape(v);
					if (!vertexHit(shape)) continue;
					renderEdges(v);
					renderVertex(v, shape);
				}
				return;
			}
			catch (ConcurrentModificationException cme) {
				// V celkom JUNGU sa ConcurrentModificationException riesi takto
				System.out.println("ConcurrentModificationException - Process");
			}
		}
	}

	protected void renderVertex(graph.objects.Vertex v, Shape shape) {
		vRender.renderVertex(rc, v, shape);
		if (rc.getVertexLabelTransformer() != null) renderVertexLabel(rc, layout, v); // Labeli
	}

	private void renderEdges(graph.objects.Vertex main) {
		if (!control.evaluateRenderEdges(main)) return;
		graph.objects.Edge hrana = new graph.objects.Edge();
		Collection<Integer> edges;

		// Incoming hrany renderujeme vsetke
		edges = main.getIncomingC();
		if (edges.size() > 0) {
			for (Integer e : edges) {
				holder.getEdges().get(e, hrana);
				if (!control.evaluateRenderEdge(hrana)) continue;
				graph.objects.Vertex another = holder.getVertices().getSource(hrana);
				if (another == main) another = holder.getVertices().getTarget(hrana);
				if (!control.evaluateRenderVertex(another)) continue;
				renderEdge(rc, layout, e, main, another);
			}
		}

		// Outgoing hrany renderujeme len pre vrcholy ktore su mimo obrazovky...
		// to aby sme nekreslili ciari 2x
		edges = main.getOutcomingC();
		if (edges.size() > 0) {
			for (Integer e : edges) {
				holder.getEdges().get(e, hrana);
				if (!control.evaluateRenderEdge(hrana)) continue;
				graph.objects.Vertex another = holder.getVertices().getSource(hrana);
				if (another == main) another = holder.getVertices().getTarget(hrana);
				if (!control.evaluateRenderVertex(another)) continue;
				if (vertexHit(makeShape(another))) continue; // brani 2x renderovaniu
				renderEdge(rc, layout, e, main, another);
			}
		}

	}

	protected void renderEdge(RenderContext<graph.objects.Vertex, Integer> rc,
			Layout<graph.objects.Vertex, Integer> layout, Integer e,
			graph.objects.Vertex v1, graph.objects.Vertex v2) {
		eRender.drawSimpleEdge(rc, layout, e, v1, v2);
		eRender.renderLabel(rc, layout, e, v1, v2, edgeTransformer.transform(e));
	}

	private Shape makeShape(graph.objects.Vertex v) {
		Shape shape = rc.getVertexShapeTransformer().transform(v);
		Point2D p = v;
		p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
		// p = rc.getMultiLayerTransformer().transform(Layer.VIEW, p); //!!
		return AffineTransform.getTranslateInstance((float) p.getX(), (float) p.getY()).createTransformedShape(shape);
	}

	private boolean vertexHit(Shape shape) {
		shape = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(shape);
		return shape.intersects(deviceRectangle);
	}

	/**
	 * Ziskaj rozmery a polohu obrazovky voci layout.
	 * 
	 * @param rc
	 * @return Rectangle obrazovky
	 */
	private Rectangle getScreen() {
		Shape screen = rc.getScreenDevice().getBounds();
		screen = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).inverseTransform(screen);
		screen = rc.getMultiLayerTransformer().getTransformer(Layer.LAYOUT).inverseTransform(screen);
		return screen.getBounds();
	}

	private float getZoomLevel() {
		float modelScale = (float) rc.getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
		float viewScale = (float) rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
		return modelScale * viewScale;
	}

	protected void frameStart() {
		if (control.isFilterByAutoZoomVolume()) {
			float minVolume = 0.25f / getZoomLevel();
			control.setAutoMinVolume(minVolume);
		}

		// Vypocitaj novu poziciu obrazovky
		Dimension d = rc.getScreenDevice().getSize();
		deviceRectangle = new Rectangle(0, 0, d.width, d.height);
		edgeTransformer = rc.getEdgeLabelTransformer();
		// System.out.println(screen); // Debug
	}

	protected void frameEnd() {
		// je to len callback..
	}

	/**
	 * Ma za ulohu renderovat vertexi a hrany.
	 */
	public void render(RenderContext<graph.objects.Vertex, Integer> rc,
			Layout<graph.objects.Vertex, Integer> layout) {
		/*
		 * // Zamkni render kym laout nieje pripravena
		 * if (!itc.done()) {
		 * System.out.println(itc.getStatus());
		 * return;
		 * }
		 */

		// TODO: RC a layout by mali byt final a mal by som ich poslat do funkcie
		this.layout = layout;
		this.rc = rc;
		frameStart();

		try {
			Rectangle screen = getScreen();
			// System.out.println("V " + screen);
			QuadTree<graph.objects.Vertex> quadtree = holder.getQuad();
			synchronized (quadtree) {
				quadtree.walk(screen, this);
			}
		}
		catch (ConcurrentModificationException ce) {
			ce.printStackTrace();
			// Spomaluje kreslenie, sposobuje sekanie obrazu
			// rc.getScreenDevice().repaint();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		frameEnd();
	}

	public void renderVertex(RenderContext<graph.objects.Vertex, Integer> rc,
			Layout<graph.objects.Vertex, Integer> layout, graph.objects.Vertex v) {
		// Nepodporuje ine Vertex rendery pretoze novy render si chce
		// vypocitat shape znova coje zle pre performance
		throw new UnsupportedOperationException();
	}
	public void setVertexRenderer(
			Renderer.Vertex<graph.objects.Vertex, Integer> r) {
		throw new UnsupportedOperationException();
	}
	public Renderer.Vertex<graph.objects.Vertex, Integer> getVertexRenderer() {
		throw new UnsupportedOperationException();
	}
	public void setEdgeRenderer(Renderer.Vertex<graph.objects.Vertex, Integer> r) {
		throw new UnsupportedOperationException();
	}
	public Renderer.EdgeLabel<graph.objects.Vertex, Integer> getEdgeLabelRenderer() {
		throw new UnsupportedOperationException();
	}
	public void setEdgeLabelRenderer(
			Renderer.EdgeLabel<graph.objects.Vertex, Integer> edgeLabelRenderer) {
		throw new UnsupportedOperationException();
	}
	public void setVertexLabelRenderer(
			Renderer.VertexLabel<graph.objects.Vertex, Integer> vertexLabelRenderer) {
		throw new UnsupportedOperationException();
	}
	public Renderer.Edge<graph.objects.Vertex, Integer> getEdgeRenderer() {
		throw new UnsupportedOperationException();
	}

	public DisplayControl getControl() {
		return control;
	}
	public void setControl(DisplayControl predicate) {
		this.control = predicate;
	}
}
