
package io;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.DirectedGraph;
import graph.objects.Vertex;

/**
 * Reader pre nacitavanie Graph suborov.
 * 
 * @author Lukas Sekerak
 */
public class Reader_Graph
		extends
			GSemSearchReader<DirectedGraph<Vertex, Integer>, Integer>
		implements
		IReadable
{
	public Reader_Graph(Factory<Vertex> vf, Factory<Integer> ef) {
		super(vf, ef);
	}

	@Override
	public void fileload(Reader r, DirectedGraph<Vertex, Integer> g)
			throws IOException {
		super.load(r, g);
	}
}
