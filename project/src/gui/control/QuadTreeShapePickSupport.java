
package gui.control;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import quadtree.IQuadTree;
import render.CoordinatesTransformer;
import render.IRenderEvaluator;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import graph.objects.Vertex;

/**
 * A <code>GraphElementAccessor</code> that returns elements whose <code>Shape</code> contains the
 * specified pick point or region. Support by quad tree and dispaly predicate.
 * 
 * @author Tom Nelson
 * @autor Lukas Sekerad - edited
 */
public class QuadTreeShapePickSupport extends ShapePickSupport<Vertex, Integer>
{
	private IQuadTree<Vertex>		quad;
	private IRenderEvaluator		control;
	private CoordinatesTransformer	trans;
	private final static Logger		logger	= Logger.getLogger(QuadTreeShapePickSupport.class.getName());

	public QuadTreeShapePickSupport(VisualizationServer<Vertex, Integer> vv,
			IQuadTree<Vertex> quad,
			IRenderEvaluator control) {
		super(vv);
		this.quad = quad;
		this.control = control;
		trans = new CoordinatesTransformer(vv.getRenderContext().getMultiLayerTransformer());
		setStyle(Style.LOWEST);
		logger.info(null);
	}

	public Vertex getVertex(Layout<Vertex, Integer> layout, double x, double y) {
		Point2D worldPickCoordinate, localPickCoordinate = new Point2D.Double(x, y);
		// trans.dumpPositions(localPickCoordinate);
		worldPickCoordinate = trans.transformLocal2World(localPickCoordinate);

		// Cez quadtree zisti ake prvky su v oblasti
		while (true) {
			try {
				return findVertex(worldPickCoordinate, layout, x, y);
			}
			catch (ConcurrentModificationException cme) {
				// V celkom JUNGU sa ConcurrentModificationException riesi takto
				System.out.println("ConcurrentModificationException - getVertex");
			}
		}
	}

	private Vertex findVertex(Point2D worldPickCoordinate,
			Layout<Vertex, Integer> layout, double x, double y) {
		List<Vertex> vertices = null;
		List<Vertex> quadVertices;
		try {
			synchronized (quad) {
				quadVertices = quad.getElements(worldPickCoordinate);
				// Aby locknutie bolo co najkratsie
				vertices = new ArrayList<Vertex>(quadVertices);
			}
		}
		catch (IndexOutOfBoundsException e) {
			// Klikol mimo editor
			return null;
		}

		// Prechadzaj prvky a hladaj prvy bod
		Shape shape;
		Point2D vertexPositionAtScreen;
		double normalizedX, normalizedY;
		for (Vertex v : vertices) {

			if (!control.evaluateRenderVertex(v)) continue;
			vertexPositionAtScreen = trans.transformWorld2Local(layout.transform(v));

			// Teraz poziciu na obrazovke vynulujeme aby sme nemuseli
			// transformovat aj shape, shape postaci len vytvorit lebo to zavisi od tvaru
			normalizedX = x - vertexPositionAtScreen.getX();
			normalizedY = y - vertexPositionAtScreen.getY();
			shape = vv.getRenderContext().getVertexShapeTransformer().transform(v);
			if (shape.contains(normalizedX, normalizedY)) return v;
		}

		// Prvky v QD su zoradene podla X a potom Y suradnice
		// Na vyhladanie mozem pouzit indexOf co mi vrati prvky index
		// Nepotrebujeme to dokedy v QuadTree NODE nieje obrovsky pocet prvkov...
		//
		// index = vertices.indexOf(o)
		// for (; index < vertices.size(); i++) {
		// v = get(index);

		return null;
	}

	// Zial nie je ina moznost ako prechadzat vsetke hrany
	// zlozitost je O(n), pri pocte milion hran to je nemozne
	public Integer getEdge(Layout<Vertex, Integer> layout, double x, double y) {
		return null;
	}

	private class Walker implements IQuadTree.IWalkThrought<Vertex>
	{
		protected Set<Vertex>	pickedVertices;
		protected Rectangle		userRectangle;

		public Walker(Rectangle rectangle) {
			pickedVertices = new HashSet<Vertex>();
			this.userRectangle = rectangle;
		}

		public void walkThrought(ArrayList<Vertex> elements) {
			Shape shape;
			Point2D p;
			while (true) {
				try {
					for (Vertex v : elements) {
						if (!control.evaluateRenderVertex(v)) continue;
						shape = vv.getRenderContext().getVertexShapeTransformer().transform(v);
						p = trans.transformWorld2Local(v);
						shape = AffineTransform.getTranslateInstance((float) p.getX(), (float) p.getY()).createTransformedShape(shape);
						// Tento riadok sa nepouziva lebo shape co nam pride ma x, y suradnicu
						// narozdiel od MyRender
						// shape =
						// vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(shape);
						if (!shape.intersects(userRectangle)) continue;

						// Vrchol je v obdlzniku
						pickedVertices.add(v);
					}
					return;
				}
				catch (ConcurrentModificationException cme) {
					// V celkom JUNGU sa ConcurrentModificationException riesi takto
					System.out.println("ConcurrentModificationException - Process");
				}
			}

		}

		public Set<Vertex> getPickedVertices() {
			return pickedVertices;
		}
	}

	public Collection<Vertex> getVertices(Layout<Vertex, Integer> layout,
			Shape shape) {

		Rectangle localScreen = shape.getBounds();
		Rectangle worldScreen = trans.transformLocal2World(shape).getBounds();
		// System.out.println(localScreen);
		// System.out.println(worldScreen);
		Walker walker = new Walker(localScreen);
		try {
			synchronized (quad) {
				quad.walk(worldScreen, walker);
			}
		}
		catch (ConcurrentModificationException ce) {
			ce.printStackTrace(); // Nemalo by vzniknut, vo walk osetrujeme
		}
		return walker.getPickedVertices();
	}
}
