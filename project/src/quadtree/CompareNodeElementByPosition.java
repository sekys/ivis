package quadtree;

import java.util.Comparator;

/**
 * Zorad prvky v uzle podla jeho pozicie.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
public class CompareNodeElementByPosition<T extends INodeElement<T>>
		implements
		Comparator<T>
{
	@Override
	public int compare(T o1, T o2) {
		if (o1.getPoint().getX() > o2.getPoint().getX()) return 1;
		if (o1.getPoint().getX() < o2.getPoint().getX()) return -1;
		
		// Velmy mala pravdepodobnost pri DOUBLE
		if (o1.getPoint().getX() == o2.getPoint().getX()) {
			if (o1.getPoint().getY() > o2.getPoint().getY()) return 1;
			if (o1.getPoint().getY() < o2.getPoint().getY()) return -1;
		}
		return 0;
	}
}
