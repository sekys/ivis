
package quadtree;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Rozsireny uzol v strome quadtree o dalsie metody.
 * Cielom bolo rozdelit logiku do viacerych suborov pre vacsiu prehladnost.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
public class Node<T extends INodeElement<T>> extends BaseNode<T>
{
	private static final long	serialVersionUID	= -3825222227684459860L;
	/**
	 * Array mapa len na svoje pod deti.
	 */
	protected Node<T>			TOP_LEFT			= null;
	protected Node<T>			BOTTOM_RIGHT;
	protected Node<T>			BOTTOM_LEFT;
	protected Node<T>			TOP_RIGHT;

	public Node(Point2D position, Point2D size, int depth) {
		this.position = position;
		this.size = size;
		this.depth = depth;

		// MAX_ELEMENTS / 4 tolko prvkov dostane isto dieta
		elements = new ArrayList<T>(MAX_ELEMENTS + 1);
	}

	protected Node<T> findIndex(Point2D point) {
		boolean right = point.getX() > (position.getX() + (size.getX() / 2.0));
		boolean down = point.getY() > (position.getY() + (size.getY() / 2.0));
		if (right) {
			if (down) return BOTTOM_RIGHT;
			return TOP_RIGHT;
		}
		if (down) return BOTTOM_LEFT;
		return TOP_LEFT;
	}

	/**
	 * Vrat objekty, ktore sa nachadzaju v danej lokacii.
	 * 
	 * @param point
	 * @return
	 */
	public ArrayList<T> getElements(Point2D point) {
		intersectsException(point);
		if (!hasChildren) {
			return getElements();
		}
		return findIndex(point).getElements(point);
	}

	protected void insert(T element, Point2D point) {
		Point2D Filepos = element.getPoint();
		if (Filepos.getX() != point.getX() || Filepos.getY() != point.getY()) {
			throw new RuntimeException();
		}
		intersectsException(point); // - Debug
		if (!hasChildren) {
			elements.add(element);
			// positions.add(point);
			presetAllElements();
			return;
		}
		findIndex(point).insert(element, point);
	}

	/**
	 * Presmeruj vsetke prvky z mojho bloku k detom.
	 */
	private void presetAllElements() {
		if (!(depth >= MAX_DEPTH) && elements.size() > MAX_ELEMENTS) {
			subdivide();
			// for (T current : elements) {
			for (int i = 0; i < elements.size(); i++) {
				Point2D Filepos = elements.get(i).getPoint();
				/*
				 * Point2D Savedpos = positions.get(i);
				 * 
				 * if(Filepos.getX() != Savedpos.getX() || Filepos.getY() != Savedpos.getY()) {
				 * throw new RuntimeException();
				 * }
				 * intersectsException(Savedpos);
				 */
				intersectsException(Filepos);
				findIndex(Filepos).insert(elements.get(i), Filepos);
			}
			elements.clear();
			hasChildren = true;

			// Deti este zorad
			TOP_LEFT.sort();
			BOTTOM_RIGHT.sort();
			BOTTOM_LEFT.sort();
			TOP_RIGHT.sort();
		}
	}
	
	protected void subdivide() {
		int depth = this.depth + 1;
		double bx = position.getX();
		double by = position.getY();
		Point2D newBounds = new Point2D.Double(size.getX() / 2.0, size.getY() / 2.0);
		double newXsc = bx + newBounds.getX();
		double newYsc = by + newBounds.getY();

		TOP_LEFT = new Node<T>(new Point2D.Double(bx, by), newBounds, depth);
		BOTTOM_RIGHT = new Node<T>(new Point2D.Double(newXsc, newYsc), newBounds, depth);
		BOTTOM_LEFT = new Node<T>(new Point2D.Double(bx, newYsc), newBounds, depth);
		TOP_RIGHT = new Node<T>(new Point2D.Double(newXsc, by), newBounds, depth);
	}

	/**
	 * Vymaz vsetky objekty v podstrome.
	 */
	public void clear() {
		if (hasChildren) {
			TOP_LEFT.clear();
			BOTTOM_RIGHT.clear();
			BOTTOM_LEFT.clear();
			TOP_RIGHT.clear();
			TOP_LEFT = BOTTOM_RIGHT = BOTTOM_LEFT = TOP_RIGHT = null;
			hasChildren = false;
		} else {
			elements.clear();
		}
	}
	
	/**
	 * trimToSize() pre dany podstrom.
	 */
	public void trimToSize() {
		if (hasChild()) {
			TOP_LEFT.trimToSize();
			BOTTOM_RIGHT.trimToSize();
			BOTTOM_LEFT.trimToSize();
			TOP_RIGHT.trimToSize();
		} else {
			elements.trimToSize();
		}
	}
	
	/**
	 * Pocet objektov v tomto podstrome.
	 * @return
	 */
	public int size() {
		int size = 0;
		if (hasChild()) {
			size += TOP_LEFT.size();
			size += BOTTOM_RIGHT.size();
			size += BOTTOM_LEFT.size();
			size += TOP_RIGHT.size();
		} else {
			size = elements.size();
		}
		return size;
	}

	/**
	 * Hladaj prvok v strome, pouzivam hladanie do hlbky.
	 * 
	 * @param element
	 * @return vracia node kde sa nachadza element
	 */
	public Node<T> findSlow(T element) {
		if (!hasChild()) {
			return (elements.indexOf(element) > -1) ? this : null;
		}
		Node<T> success;
		// Mna sa dotkol takze asi aj deti, spusti to dalej
		success = TOP_LEFT.findSlow(element);
		if (success != null) return success;
		success = BOTTOM_RIGHT.findSlow(element);
		if (success != null) return success;
		success = BOTTOM_LEFT.findSlow(element);
		if (success != null) return success;
		success = TOP_RIGHT.findSlow(element);
		if (success != null) return success;
		return null;
	}

	/**
	 * Metoda hlada podla starej polohy prvok a pripadne ho vymaze.
	 * Vyhladavanie vrchola podla pozicie, zlozitost O(log n)
	 * 
	 * @param element
	 * @return boolean - true ak prvok bol vymazany
	 */
	public boolean remove(T element, Point2D pos) {
		// TODO: Nespravna pracuje - nemaze vrcholy a nastava duplicita
		return getElements(pos).remove(element);
		/*
		 * quadtree konfliktu predchadzaju metody find a remove ktore funguju na
		 * equals metode a ta identififikuje 2 rovnake objekty ?!
		 */
	}

	/**
	 * Vymaz prvok v strome, pouzivam hladanie do hlbky. Metoda prechadza kazdy
	 * jeden vrchol zlozitost O(n)
	 * 
	 * @param element
	 * @return boolean - true ak ho vymazalo
	 */
	public boolean remove(T element) {
		if (!hasChild()) return elements.remove(element);
		if (TOP_LEFT.remove(element)) return true;
		if (BOTTOM_RIGHT.remove(element)) return true;
		if (BOTTOM_LEFT.remove(element)) return true;
		if (TOP_RIGHT.remove(element)) return true;
		return false;
	}

	/**
	 * Vymaz vsetky objekty, ktore su podobne s element.
	 * @param element
	 */
	public void removeAll(Object element) {
		if (hasChild()) {
			TOP_LEFT.removeAll(element);
			BOTTOM_RIGHT.removeAll(element);
			BOTTOM_LEFT.removeAll(element);
			TOP_RIGHT.removeAll(element);
		} else {
			removeAllOccurrence(element);
		}
	}

	/**
	 * Metoda spusti interface na skupinu prvkov ktore sa nachadzaju v
	 * Rectangle. Je to robene cez interface, je to rychlejsie na velke data.
	 * Inac to mohlo vraciat zoznam prvkov ktore su v tom rectangle.
	 * 
	 * @param element
	 * @return true ak prvok bol vymazany
	 */
	public void walk(Rectangle re, IQuadTree.IWalkThrought<T> i) {
		// Nedotyka sa to mna, ignorujem
		if (!intersects(re)) return;

		if (hasChildren) {
			// if (hasChild()) {
			// Mna sa dotkol takze asi aj deti, spusti to dalej
			TOP_LEFT.walk(re, i);
			BOTTOM_RIGHT.walk(re, i);
			BOTTOM_LEFT.walk(re, i);
			TOP_RIGHT.walk(re, i);
			return;
		}

		// Nemam deti takze spusti process na moje prvky
		i.walkThrought(elements);
	}

}
