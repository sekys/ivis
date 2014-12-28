
package io;

import java.io.IOException;
import java.io.Reader;
import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.PajekNetReader;
import graph.objects.Vertex;

/**
 * Reader pre nacitavanie NET suborov.
 * 
 * @author Lukas Sekerak
 */
public class Reader_NET
		extends
			PajekNetReader<Graph<Vertex, Integer>, Vertex, Integer>
		implements
		IReadable
{

	public Reader_NET(Factory<Integer> edge_factory) {
		super(edge_factory);
	}

	@Override
	public void fileload(Reader r, DirectedGraph<Vertex, Integer> g)
			throws IOException {
		super.load(r, g);
	}
}
