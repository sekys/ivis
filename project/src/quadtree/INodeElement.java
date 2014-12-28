
package quadtree;

import java.awt.geom.Point2D;

/**
 * Interface pre objekt, ktory sa nachadza v zozname objektov pre uzol.
 * 
 * @author Lukas Sekerak
 */
public interface INodeElement<T> extends Comparable<T>
{
	public Point2D getPoint();
}
