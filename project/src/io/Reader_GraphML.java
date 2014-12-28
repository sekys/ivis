
package io;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections15.Factory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.io.GraphMLReader;
import graph.DirectedCorpusGraph;
import graph.objects.Vertex;

/**
 * Reader pre nacitavanie GraphML suborov.
 * 
 * @author Lukas Sekerak
 */
public class Reader_GraphML
		extends
			GraphMLReader<DirectedGraph<Vertex, Integer>, Vertex, Integer>
		implements
		IReadable
{

	public Reader_GraphML(Factory<Vertex> vf, Factory<Integer> ef) throws ParserConfigurationException,
			SAXException {
		super(vf, ef);
	}

	@Override
	public void fileload(Reader r, DirectedGraph<Vertex, Integer> g)
			throws IOException {
		super.load(r, g);
		DirectedCorpusGraph b = (DirectedCorpusGraph) g;
		b.ProcessOfReadingEnd();
	}

	protected void createVertex(Attributes atts)
			throws SAXNotSupportedException {
		Map<String, String> vertex_atts = getAttributeMap(atts);
		String id = vertex_atts.get("id");
		DirectedCorpusGraph g = (DirectedCorpusGraph) current_graph;
		String[] pole = id.split("=>");
		if (pole.length != 2) throw new SAXNotSupportedException("Bad format.");
		g.addVertexFromFile(pole[0], pole[1], 0);
	}

	protected void createEdge(Attributes atts, TagState state)
			throws SAXNotSupportedException {
		Map<String, String> edge_atts = getAttributeMap(atts);
		String source = edge_atts.get("source");
		String[] sourcep = source.split("=>");
		if (sourcep.length != 2) throw new SAXNotSupportedException("Bad format.");
		String target = edge_atts.get("target");
		String[] targetp = target.split("=>");
		if (targetp.length != 2) throw new SAXNotSupportedException("Bad format.");
		
		DirectedCorpusGraph g = (DirectedCorpusGraph) current_graph;
		g.addEdgeFromFile(sourcep[0], sourcep[1], targetp[0], targetp[1], 0);
	}

	/**
	 * @autor GraphMLReader a Seky
	 */
	public void startElement(String uri, String name, String qName,
			Attributes atts) throws SAXNotSupportedException {
		String tag = qName.toLowerCase();
		TagState state = tag_state.get(tag);
		if (state == null) state = TagState.OTHER;

		switch (state) {
			case GRAPHML :
				break;

			case VERTEX :
				if (this.current_graph == null) throw new SAXNotSupportedException("Graph must be defined prior to elements");
				createVertex(atts);
				break;

			case ENDPOINT :
				throw new SAXNotSupportedException("Unsupported");

			case EDGE :
			case HYPEREDGE :
				if (this.current_graph == null) throw new SAXNotSupportedException("Graph must be defined prior to elements");
				createEdge(atts, state);
				break;

			case GRAPH :
				break;

			case DATA :
				throw new SAXNotSupportedException("Unsupported");


			case KEY :
				throw new SAXNotSupportedException("Unsupported");

			default :
				break;
		}

		current_states.addFirst(state);
	}
}
