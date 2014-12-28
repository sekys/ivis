
package quadtree;

import java.awt.geom.Point2D;

/**
 * Trieda pre quad strom.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
@SuppressWarnings("serial")
public class QuadTree<T extends INodeElement<T>> extends Node<T>
		implements
		IQuadTree<T>
{
	// private final static Logger logger = Logger.getLogger(QuadTree.class.getName());

	/**
	 * Vytvor quad strom.
	 */
	public QuadTree(double size) {
		this(new Point2D.Double(0.0, 0.0), size);
	}
	
	/**
	 * Vytvor quad strom.
	 */
	public QuadTree(Point2D start, double size) {
		this(start, new Point2D.Double(size, size));
	}
	
	/**
	 * Vytvor quad strom.
	 * @param start
	 * @param size
	 */
	public QuadTree(Point2D start, Point2D size) {
		super(start, size, 0);
	}

	/**
	 * Pridaj objekt do stromu.
	 * 
	 * @param Point2D
	 * @param element
	 */
	public void insert(T element, final Point2D pos) {
		intersectsException(pos);
		// logger.info(element);
		super.insert(element, pos);
	}
	
	/**
	 * Vrat koren stromu.
	 * @return
	 */
	public Node<T> getRootNode() {
		return this;
	}

	/**
	 * Vymaz prvok v strome s danou poziciou.
	 */
	public boolean remove(T element, final Point2D pos) {
		// logger.info(element);
		return super.remove(element, pos);
	}
	
	public Point2D getPreferredSize() {
		return new Point2D.Double(size.getX() * 0.75, size.getY() * 0.75);
	}
}
