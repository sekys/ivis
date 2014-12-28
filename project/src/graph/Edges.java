
package graph;

import graph.objects.Edge;
import java.io.File;
import java.io.IOException;
import org.apache.commons.collections15.Factory;
import util.CollectionOfIndexes;
import nio.base.IBufferFactory;
import nio.mapping.StaticArrayList;

/**
 * Trieda sa stara o ulozenie vsetkych hran zo suboru.
 * Uklada ich do pametovo namapovaneho suboru.
 * 
 * @author Lukas Sekerak
 */
public class Edges extends CollectionOfIndexes implements Factory<Integer>
{
	private StaticArrayList<Edge>	metadata;
	
	public Edges(File folder, IBufferFactory factory) { 
		try {
			metadata = new StaticArrayList<Edge>(new File(folder + "/edge.db"), 16, 100000, factory, 1000000);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public int size() {
		return metadata.size();
	}
	@Override
	public Integer create() {
		return new Integer(-1);
	}
	@Override
	public void remove(Integer index) {
		metadata.remove(index);
	}
	public int add(Edge v) {
		return metadata.add2(v);
	}
	public void get(Integer index, Edge hrana) {
		metadata.get(index, hrana);
	}
}
