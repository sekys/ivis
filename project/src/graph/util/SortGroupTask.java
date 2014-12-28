
package graph.util;

import graph.objects.VertexGroup;

/**
 * Zorad skupiny vrcholov. Uloha pre vlakno.
 * 
 * @author Lukas Sekerak
 */
public class SortGroupTask implements Runnable
{
	protected VertexGroup	group;

	public SortGroupTask(VertexGroup group) {
		this.group = group;
	}
	public void run() {
		try {
			// Vymaz hned vsetko s pamete a zosortuj pole vertices
			group.getVertices().trimToSize();

			// Vymen buffer kontroleri
			// IBufferFactory actual = group.metadata.getMap().getFactory();
			// actual.unmap(new DeleteAllNotLast());
			// virtualmemory.getAllocated().addAll(actual.getAllocated());
			// group.metadata.getMap().setFactory(virtualmemory);
			group.sort();
			group.autosort();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
