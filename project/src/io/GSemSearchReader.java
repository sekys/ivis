
package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections15.Factory;
import util.Debug;
import edu.uci.ics.jung.graph.Graph;
import graph.DirectedCorpusGraph;
import graph.objects.Vertex;

/**
 * gSemSearchReader
 * 
 * V suboroch su dva typy dat vertexy a edge a zacinaju 1 Integer: alebo 2 Edge:
 * po 1 Integer: ide Integer v tvare key=>value a nakonci cislo ktore je
 * vekost/vyznamnost uzla. Pri edge je to nasledovne: 2 Edge:
 * (key=>value)=>(key=value) cislo Kde su vlasne definovane dva vertexy medzi
 * ktorymi je hrana a na konci hruka/sila prepojenia.
 * 
 * @author Lukas Sekerak
 * 
 * @param <G>
 * @param <Integer>
 * @param <E>
 */
public class GSemSearchReader<G extends Graph<Vertex, Integer>, E>
{
	public static final String		FILE_TYPE	= ".graph";

	protected static Pattern		vRegex		= Pattern.compile("^1 Vertex: (.+)=>([^\t]+)(\\s[0-9]+)??$");
	protected static Pattern		hRegex		= Pattern.compile("^2 Edge: \\((.+)=>(.+)\\)=>\\((.+)=>(.+)\\)(\\s[0-9]+)?$");

	protected int					VertexCount	= 0;
	protected int					hranCounter	= 0;
	protected DirectedCorpusGraph	current_graph;

	/**
	 * Constructor, set factories
	 * 
	 * @param vertex_factory
	 *            the factory to use to create Integer objects
	 * @param edge_factory
	 *            the factory to use to create edge objects
	 */
	public GSemSearchReader(Factory<Vertex> vertex_factory,
			Factory<E> edge_factory) {
		current_graph = null;
	}

	/**
	 * Nacitaj subor...
	 * 
	 * @param filename
	 * @param graph_factory
	 * @return
	 * @throws IOException
	 */
	public G load(String filename, Factory<? extends G> graph_factory)
			throws IOException {
		return load(new FileReader(filename), graph_factory.create());
	}

	/**
	 * Nacitaj subor...
	 * 
	 * @param reader
	 * @param graph_factory
	 * @return
	 * @throws IOException
	 */
	public G load(Reader reader, Factory<? extends G> graph_factory)
			throws IOException {
		return load(reader, graph_factory.create());
	}

	/**
	 * Nacitaj subor...
	 * 
	 * @param filename
	 * @param g
	 * @return
	 * @throws IOException
	 */
	public G load(String filename, G g) throws IOException {
		if (g == null) throw new IllegalArgumentException("Graf je nulovy.");
		return load(new FileReader(filename), g);
	}

	/**
	 * Nacitaj subor...
	 * 
	 * @param reader
	 * @param g
	 * @return
	 * @throws IOException
	 */
	public G load(Reader reader, G g) throws IOException {
		BufferedReader input = new BufferedReader(reader);
		current_graph = (DirectedCorpusGraph) g;
		Debug time = new Debug();

		try {
			parseFile(input);
			time.End("Nacital som " + current_graph.getVertexCount()
					+ "vrcholov " + current_graph.getEdgeCount() + "hran");
		}
		catch (IOException e) {
			// TODO zmazat neskor
			e.printStackTrace();
			throw e;
		}
		finally {
			input.close();
			reader.close();
		}
		return g;
	}

	/**
	 * Odparsuj subor
	 * 
	 * @param BufferedReader
	 *            input
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void parseFile(BufferedReader input) throws IOException {
		String riadok = null;
		Matcher m;
		while ((riadok = input.readLine()) != null) {
			if (riadok.charAt(0) == '1') {
				m = vRegex.matcher(riadok);
				if (m.find()) {
					parseVertex(m);
					continue;
				}
			} else if (riadok.charAt(0) == '2') {
				m = hRegex.matcher(riadok);
				if (m.find()) {
					parseEdge(m);
					continue;
				}
			}
			System.out.println("Riadok neobsahuje spravny format: " + riadok);
		}
		current_graph.ProcessOfReadingEnd();
	}

	private void parseVertex(Matcher m) {
		int ohodnotenie = 0;
		if (m.group(3) != null) {
			String x = m.group(3).trim();
			if (x.length() != 0) {
				ohodnotenie = Integer.valueOf(x);
			}
		}
		current_graph.addVertexFromFile(m.group(1), m.group(2), ohodnotenie);
		VertexCount++;
		if ((VertexCount % 100000) == 0) System.out.println("Vertexov: "
				+ VertexCount);
	}
	
	private void parseEdge(Matcher m) {
		int ohodnotenie = 0;
		if (m.group(5) != null) {
			String x = m.group(5).trim();
			if (x.length() != 0) {
				ohodnotenie = Integer.valueOf(x.trim());
			}
		}
		current_graph.addEdgeFromFile(m.group(1), m.group(2), m.group(3), m.group(4), ohodnotenie);
		hranCounter++;
		if ((hranCounter % 100000) == 0) System.out.println("Hran: "
				+ hranCounter);
	}
}
