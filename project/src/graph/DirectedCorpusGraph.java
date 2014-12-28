
package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import graph.objects.Edge;
import graph.objects.Vertex;

/**
 * Implementacia directed grafu urcena pre corpus, velmy velky graf.
 * Tento graf vyuziva rozne predpoklady, ak nebudu splnena moze to
 * viest k nespravnemu spravaniu. Predpoklady vyuziva k rychlejsiemu
 * spracovaniu.
 * 
 * @author Lukas Sekerak
 */
@SuppressWarnings("serial")
public class DirectedCorpusGraph extends AbstractTypedGraph<Vertex, Integer>
		implements
		DirectedGraph<Vertex, Integer>
{
	protected GraphHolder	builder;

	/**
	 * Vrati holder grafu.
	 * 
	 * @return vrati holder grafu
	 */
	public GraphHolder getHolder() {
		return builder;
	}

	/**
	 * Vytvor DirectedCorpusGraph z GraphHolder
	 * @param build
	 */
	public DirectedCorpusGraph(GraphHolder build) {
		super(EdgeType.DIRECTED);
		builder = build;
	}

	/**
	 * Najdi hranu medzi vrcholmy.
	 */
	@Override
	public Integer findEdge(Vertex v1, Vertex v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) return null;
		if (v1.getOutcoming() != null && v2.getIncoming() != null) {
			return findEdgeHalf(v1, v2);
		} else if (v1.getIncoming() != null && v2.getOutcoming() != null) {
			return findEdgeHalf(v2, v1);
		}
		return null;
	}

	private Integer findEdgeHalf(Vertex v1, Vertex v2) {
		for (int hladamhranu : v1.getOutcoming()) {
			for (int moznahrana : v2.getIncoming()) {
				if (hladamhranu == moznahrana) {
					return new Integer(moznahrana);
				}
			}
		}
		return null;
	}

	/**
	 * Vrat vsetky hrany medzi vrcholmy.
	 */
	@Override
	public Collection<Integer> findEdgeSet(Vertex v1, Vertex v2) {
		if (!containsVertex(v1) || !containsVertex(v2)) return null;
		ArrayList<Integer> edge_collection = new ArrayList<Integer>(1);
		Integer Integer = findEdge(v1, v2);
		if (Integer == null) return edge_collection;
		edge_collection.add(Integer);
		return edge_collection;
	}

	protected Collection<Integer> getIncoming_internal(Vertex vertex) {
		return vertex.getIncomingC();
	}

	protected Collection<Integer> getOutgoing_internal(Vertex vertex) {
		return vertex.getOutcomingC();
	}

	protected Collection<Vertex> getPreds_internal(Vertex vertex) {

		int[] hrany = vertex.getIncoming();
		if (hrany == null) return new ArrayList<Vertex>(0);
		ArrayList<Vertex> vertexi = new ArrayList<Vertex>(hrany.length);
		Edge hrana = Edge.LoadSourceOnly();
		for (int hranaindex : hrany) {
			builder.getEdges().get(hranaindex, hrana);
			try {
				vertexi.add(builder.getVertices().getSource(hrana));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return vertexi;
	}

	protected Collection<Vertex> getSuccs_internal(Vertex vertex) {

		int[] hrany = vertex.getOutcoming();
		if (hrany == null) return new ArrayList<Vertex>(0);
		ArrayList<Vertex> vertexi = new ArrayList<Vertex>(hrany.length);
		Edge hrana = Edge.LoadTargetOnly();
		for (int hranaindex : hrany) {
			builder.getEdges().get(hranaindex, hrana);
			try {
				vertexi.add(builder.getVertices().getTarget(hrana));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return vertexi;
	}

	/**
	 * Ziskaj vsetky hrany ktore vstupuju do vrchola.
	 */
	public Collection<Integer> getInEdges(Vertex vertex) {
		if (!containsVertex(vertex)) return null;
		return Collections.unmodifiableCollection(getIncoming_internal(vertex));
	}

	/**
	 * Ziskaj vsetky hrany ktore vystupuju z vrchola.
	 */
	public Collection<Integer> getOutEdges(Vertex vertex) {
		if (!containsVertex(vertex)) return null;
		return Collections.unmodifiableCollection(getOutgoing_internal(vertex));
	}

	/**
	 * Ziskaj vsetkych predchodcov vrchola.
	 */
	public Collection<Vertex> getPredecessors(Vertex vertex) {
		if (!containsVertex(vertex)) return null;
		return Collections.unmodifiableCollection(getPreds_internal(vertex));
	}

	/**
	 * Ziskaj vsetkych nasledovnikov vrchola.
	 */
	public Collection<Vertex> getSuccessors(Vertex vertex) {
		if (!containsVertex(vertex)) return null;
		return Collections.unmodifiableCollection(getSuccs_internal(vertex));
	}

	/**
	 * Ziskaj vrcholy ako pair pre hranu.
	 */
	public Pair<Vertex> getEndpoints(Integer edge) {
		if (!containsEdge(edge)) return null;
		Edge hrana = new Edge();
		builder.getEdges().get(edge, hrana);
		try {
			return new Pair<Vertex>(builder.getVertices().getSource(hrana), builder.getVertices().getTarget(hrana));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Vrchol kde hrana zacina.
	 */
	public Vertex getSource(Integer directed_edge) {
		if (!containsEdge(directed_edge)) return null;
		Edge hrana = new Edge();
		builder.getEdges().get(directed_edge, hrana);
		return builder.getVertices().getSource(hrana);
	}

	/**
	 * Vrchol kde hrana konci.
	 */
	public Vertex getDest(Integer directed_edge) {
		if (!containsEdge(directed_edge)) return null;
		Edge hrana = new Edge();
		builder.getEdges().get(directed_edge, hrana);
		return builder.getVertices().getTarget(hrana);
	}

	/**
	 * Je vrchol ten kde dana hrana zacina.
	 */
	public boolean isSource(Vertex vertex, Integer edge) {
		if (!containsEdge(edge) || !containsVertex(vertex)) return false;
		return vertex.equals(this.getEndpoints(edge).getFirst());
	}

	/**
	 * Je vrchol ten kde dana hrana konci.
	 */
	public boolean isDest(Vertex vertex, Integer edge) {
		if (!containsEdge(edge) || !containsVertex(vertex)) return false;
		return vertex.equals(this.getEndpoints(edge).getSecond());
	}

	/**
	 * Vrat vsetky hrany.
	 */
	public Collection<Integer> getEdges() {
		return Collections.unmodifiableCollection(builder.getEdges());
	}

	/**
	 * Vrat vsetky vrcholy.
	 */
	public Collection<Vertex> getVertices() {
		return Collections.unmodifiableCollection(builder.getVertices());
	}

	/**
	 * Je tento vrchol medzi znamymi vrcholmy.
	 */
	public boolean containsVertex(Vertex vertex) {
		return builder.getVertices().contains(vertex);
	}

	/**
	 * Existuje takato hrana ?
	 */
	public boolean containsEdge(Integer edge) {
		return builder.getEdges().contains(edge);
	}

	/**
	 * Vrat pocet vsetkych hran.
	 */
	public int getEdgeCount() {
		return builder.getEdges().size();
	}

	/**
	 * Vrat pocet vsetkych vrcholov.
	 */
	public int getVertexCount() {
		return builder.getVertices().size();
	}

	/**
	 * Vrat vsetkych susedov vrchola.
	 */
	public Collection<Vertex> getNeighbors(Vertex vertex) {
		try {
			if (!containsVertex(vertex)) return null;

			Collection<Vertex> neighbors = new ArrayList<Vertex>();
			neighbors.addAll(getPreds_internal(vertex));
			neighbors.addAll(getSuccs_internal(vertex));
			return Collections.unmodifiableCollection(neighbors);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void getIncidentEdgesCopy(Collection<Integer> incident_edges,
			int[] hrany) {
		if (hrany != null) {
			for (int i = 0; i < hrany.length; i++)
				incident_edges.add(new Integer(hrany[i]));
		}
	}

	/**
	 * Vrat vsetky susedne hrany.
	 */
	public Collection<Integer> getIncidentEdges(Vertex vertex) {
		if (!containsVertex(vertex)) return null;
		Collection<Integer> edges = new HashSet<Integer>(vertex.getSusednychHran());
		getIncidentEdgesCopy(edges, vertex.getIncoming());
		getIncidentEdgesCopy(edges, vertex.getOutcoming());
		return Collections.unmodifiableCollection(edges);
	}

	/**
	 * Vrat pocet susedov.
	 */
	public int degree(Vertex vertex) {
		if (!containsVertex(vertex)) throw new IllegalArgumentException(vertex
				+ " is not a vertex in this graph");

		return vertex.getSusednychHran();
	}

	/**
	 * Vrat pocet susedov.
	 */
	public int getNeighborCount(Vertex vertex) {
		if (!containsVertex(vertex)) throw new IllegalArgumentException(vertex
				+ " is not a vertex in this graph");
		return vertex.getSusednychHran();
	}

	/**
	 * Pridaj vrchol do grafu.
	 * 
	 * @param key
	 * @param value
	 * @param priority
	 */
	public void addVertexFromFile(String key, String value, int priority) {
		builder.addVertexFromFile(key, value, priority);
	}

	/**
	 * Vymaz vrchol z grafu.
	 */
	public boolean removeVertex(Vertex vertex) {
		if (!containsVertex(vertex)) return false;
		ArrayList<Integer> incident = new ArrayList<Integer>(getIncoming_internal(vertex));
		incident.addAll(getOutgoing_internal(vertex));

		for (Integer edge : incident)
			removeEdge(edge);

		builder.getVertices().remove(vertex);
		return true;
	}

	/**
	 * Vymaz hranu z grafu.
	 */
	public boolean removeEdge(Integer edge) {
		if (!containsEdge(edge)) return false; // TODO: nemusi byt

		Pair<Vertex> endpoints = this.getEndpoints(edge);
		Vertex source = endpoints.getFirst();
		Vertex dest = endpoints.getSecond();

		// remove vertices from each others' adjacency maps
		source.removeTarget(edge);
		dest.removeSource(edge);

		builder.getEdges().remove(edge);
		return true;
	}

	/**
	 * Posli spravu po spracovani suboru, je potrebne poslat spravu
	 * aby graf sa preusporiadal a pod.
	 */
	public void ProcessOfReadingEnd() {
		builder.ProcessOfReadingEnd();
	}

	/**
	 * Pridaj hranu zo suboru.
	 * 
	 * @param k1
	 * @param v1
	 * @param k2
	 * @param v2
	 * @param p
	 */
	public void addEdgeFromFile(String k1, String v1, String k2, String v2,
			int p) {
		builder.addEdgeFromFile(k1, v1, k2, v2, p);
	}
	
	/**
	 * Nepodporovane.
	 */
	public boolean addVertex(Vertex vertex) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Nepodporovane.
	 */
	@Override
	public boolean addEdge(Integer edge, Pair<? extends Vertex> endpoints,
			EdgeType edgeType) {
		throw new UnsupportedOperationException();
	}
}
