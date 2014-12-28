
package graph.util;

import graph.objects.Vertex;
import java.util.Comparator;

/**
 * Porovnaj 2 Vrcholi, ich velkost a potom hash.
 * 
 * @author Lukas Sekerak
 */
public class CompareVertexBySizeThenValueHash implements Comparator<Vertex>
{
	@Override
	public int compare(Vertex o1, Vertex o2) {
		if (o1.getSize() == o2.getSize()) {
			if (o1.getValueHash() > o2.getValueHash()) return 1;
			if (o1.getValueHash() < o2.getValueHash()) return -1;
			return 0;
		}
		if (o1.getSize() > o2.getSize()) return 1;
		if (o1.getSize() < o2.getSize()) return -1;
		return 0;
	}
}
