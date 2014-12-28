
package quadtree;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Interface pre quad strom.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
public interface IQuadTree<T extends INodeElement<T>>
{
	/**
	 * Metoda sa vola pri zmene pozicii vrchola.
	 * 
	 * @param v
	 *            Vertex
	 * @param old
	 *            Stara pozicia = null tak prvok sa pridava prvykrat
	 */
	public void insert(T vertex, Point2D pos);
	
	/**
	 * Najdi a vymaz prvok zo stromu.
	 * @param element
	 * @param pos
	 * @return
	 */
	public boolean remove(T element, Point2D pos);

	/**
	 * Spusti IWalkThrought pre vsetky objekty ktory su v oblasti Rectangle.
	 * 
	 * @param re
	 * @param i
	 */
	public void walk(Rectangle re, IWalkThrought<T> i);

	/**
	 * Prehladaj cely strom a najdi prvok.
	 * Teda sa nehlada podla pozicie objektu ale vsade.
	 * 
	 * @param element
	 * @return
	 */
	public Node<T> findSlow(T element);

	/**
	 * Vymaz vsetky objekty v strome, ktore su totozne s element.
	 * 
	 * @param element
	 */
	public void removeAll(Object element);

	/**
	 * Interface ktory sa spusta na kazdu skupinu vertexov v 1 node.
	 * Prvky vo vnutri by som nemal modifikovat lebo su v QDtree.
	 * 
	 * Povodne to bol interface na 1 prvok, ale toto by to malo urychlit.
	 * 
	 * Interface pri VertexsInRectangle moze spustit vertexi hoci sedia mimo obrazovky.
	 * O dalsie sa postara JUNG ktory to este raz otestuje
	 * Alebo ked v jednom NODE su napriklad len 4 prvky tak mozme vypnut dalsiu kontrolu a zobrazit
	 * to.
	 * 
	 * @author Lukas Sekerak
	 * 
	 * @param <T>
	 */
	public interface IWalkThrought<T>
	{
		public void walkThrought(ArrayList<T> v);
	}

	/**
	 * Vrat elementy v danej oblasti.
	 * 
	 * @param point
	 * @return
	 */
	public ArrayList<T> getElements(Point2D point);

	/**
	 * Vymaz vsetko v strome.
	 */
	public void clear();
}
