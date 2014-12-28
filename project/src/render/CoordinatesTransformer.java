
package render;

import java.awt.Shape;
import java.awt.geom.Point2D;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.MultiLayerTransformer;

/**
 * Pomocna trieda, ktora zabaluje vsetky transformery.
 * Praca s nimy je potom jednoduchasia.
 * TRANSFORMACIE PREBIEHAJU V 2 FAZACH !
 * 
 * @author Lukas Sekerak
 */
public class CoordinatesTransformer
{
	protected MultiLayerTransformer	lt;

	public CoordinatesTransformer(MultiLayerTransformer lt) {
		this.lt = lt;
	}

	/**
	 * Metoda pre debugovanie
	 */
	public void dumpPositions(Point2D pos) {
		Point2D temp;
		System.out.println("");
		temp = lt.transform(Layer.LAYOUT, (Point2D) pos.clone());
		temp = lt.transform(Layer.VIEW, temp);
		System.out.println(temp);
		temp = lt.transform(Layer.VIEW, (Point2D) pos.clone());
		temp = lt.transform(Layer.LAYOUT, temp);
		System.out.println(temp);
		temp = lt.inverseTransform(Layer.LAYOUT, (Point2D) pos.clone());
		temp = lt.inverseTransform(Layer.VIEW, temp);
		System.out.println(temp);
		temp = lt.inverseTransform(Layer.VIEW, (Point2D) pos.clone());
		temp = lt.inverseTransform(Layer.LAYOUT, temp);
		System.out.println(temp);
	}

	/**
	 * transformLocal2World(Point2D pos)
	 * 
	 * @param pos
	 * @return
	 */
	public Point2D transformLocal2World(Point2D pos) {
		pos = lt.inverseTransform(Layer.VIEW, pos);
		pos = lt.inverseTransform(Layer.LAYOUT, pos);
		return pos;
	}

	/**
	 * transformWorld2Local(Point2D pos)
	 * 
	 * @param pos
	 * @return
	 */
	public Point2D transformWorld2Local(Point2D pos) {
		pos = lt.transform(Layer.LAYOUT, pos);
		pos = lt.transform(Layer.VIEW, pos);
		return pos;
	}

	/**
	 * transformLocal2World(Shape screen)
	 * 
	 * @param screen
	 * @return
	 */
	public Shape transformLocal2World(Shape screen) {
		screen = lt.getTransformer(Layer.VIEW).inverseTransform(screen);
		screen = lt.getTransformer(Layer.LAYOUT).inverseTransform(screen);
		return screen;
	}

	/**
	 * transformWorld2Local(Shape screen)
	 * 
	 * @param screen
	 * @return
	 */
	public Shape transformWorld2Local(Shape screen) {
		screen = lt.transform(Layer.LAYOUT, screen);
		screen = lt.transform(Layer.VIEW, screen);
		return screen;
	}
}
