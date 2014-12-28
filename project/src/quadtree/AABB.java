
package quadtree;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Trieda pre Axis Aligned Bounding Box
 * 
 * @author Lukas Sekerak
 */
public class AABB
{
	protected Point2D	size;
	protected Point2D	position;

	public Point2D getSize() {
		return size;
	}
	public Point2D getPosition() {
		return position;
	}

	/**
	 * Otestuj ci rectangle je v kolizii s node. Rectangle je top left a top
	 * left suradnica
	 * 
	 * @param re
	 * @return true ak doslo ku kolizii
	 */
	public boolean intersects(Rectangle re) {
		return re.intersects(position.getX(), position.getY(), size.getX(), size.getY());
	}

	/**
	 * Otestuj ci suradnice su v tomto strome .. Tuto kontrolu nemusime pridavat
	 * do kazdeho node, kedze tam je findIndex() ktory vrati vzdy rovnake
	 * hodnoty.
	 * 
	 * @param element
	 */
	public boolean intersects(Point2D point) {
		// Check if the element point are within size of the quadtree
		if (point.getX() > (position.getX() + size.getX())
				|| point.getX() < position.getX()) return false;
		if (point.getY() > (position.getY() + size.getY())
				|| point.getY() < position.getY()) return false;
		return true;
	}

	/**
	 * Ma nastat kolizia ?
	 * 
	 * @param point
	 */
	public void intersectsException(Point2D point) {
		if (!intersects(point)) {
			throw new IndexOutOfBoundsException("Chyba, kolizia mala nastat ale nenastala.");
		}
	}
}
