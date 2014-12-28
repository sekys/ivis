package quadtree;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Vseobecna trieda pre uzol v quad tree.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
public abstract class BaseNode<T extends INodeElement<T>> extends AABB implements Serializable
{
	private static final long	serialVersionUID	= 2986725838721696545L;
	/**
	 * Defaultne hodnoty pre max pocet prvkov a max hlbku.
	 */
	public final static int		MAX_ELEMENTS		= 800;
	public final static int		MAX_DEPTH			= 50;

	protected int				depth;
	protected ArrayList<T>		elements;
	protected boolean			hasChildren;

	public String toString() {
		return "[P:" + position + " S:" + size + " D:" + depth + "]";
	}
	
	public int getDepth() {
		return depth;
	}
	
	public ArrayList<T> getElements() {
		return elements;
	}
	
	protected boolean hasChild() {
		synchronized (this) {
			return hasChildren;
		}
	}
	protected void sort() {
		// Deti este zorad
		/*Comparator<T> comparator = new CompareNodeElementByPosition<T>();
		Collections.sort(elements, comparator);
		elements = new SortedArrayList<T>(elements, comparator);*/
	}
	protected void removeAllOccurrence(Object element) {
		for(int index = elements.size()-1; index > -1; index--) {
			if( element.equals(elements.get(index)) ) {
				elements.remove(index);
			}
		}
	}
}
