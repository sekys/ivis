
package io;

import java.io.IOException;
import java.io.Reader;
import edu.uci.ics.jung.graph.DirectedGraph;
import graph.objects.Vertex;

/*
 * Interface pre nacitanie grafu z roznych typov suborov.
 */
public interface IReadable
{
	public void fileload(Reader reader, DirectedGraph<Vertex, Integer> g)
			throws IOException;
}
