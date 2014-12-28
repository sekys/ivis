
package graph.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import util.Debug;
import util.MyThreadPoolExecutor;
import edu.uci.ics.jung.graph.Graph;
import graph.VerticesPool;
import graph.objects.Vertex;
import graph.objects.VertexGroup;

/**
 * Metoda zabaluj a vybaluj vrcholy.
 * Viac popisane je v bak. praci.
 * 
 * @author Lukas Sekerak
 */
public class CEGV
{
	protected HashSet<Vertex>			vrcholiNaCeste;
	protected Integer					maxTreshold;
	protected Graph<Vertex, Integer>	graph;
	protected MyThreadPoolExecutor		workers;
	protected AtomicInteger				actualCount;

	private final static Logger			logger	= Logger.getLogger(CEGV.class.getName());

	public CEGV(Graph<Vertex, Integer> graph) {
		this.graph = graph;
	}

	protected class PackVerticesTask implements Runnable
	{
		private Vertex	main;

		public PackVerticesTask(Vertex vertex) {
			main = vertex;
		}
		@Override
		public void run() {
			main.setZvyrazneny(true);
			main.setVisible(true);
			packVertex(main, 1);
			int id = actualCount.incrementAndGet();
			logger.info("end " + id);
		}
	}

	public void compute(Set<Vertex> vybrane,
			Transformer<Integer, Float> edgeWeightTransformer,
			VerticesPool vertices) {
		// Ziskaj vrcholy ktore su sucastou ciest
		vrcholiNaCeste = new HashSet<Vertex>(vybrane);
		najkratsieCestyMM(edgeWeightTransformer, vybrane);

		// Vsetke vrcholy nastav na normalne hodnoty
		// - len pre debugovanie
		// TODO: hladanie najkratsej cesty a resetnutie visibility moze ist vo vlaknach
		resetVerticesVisibility(vertices);

		// Teraz musime prechadza jednotlive vrcholy a ako na strom ich zabalovat
		// zabalovat okrem nasich vrcholov a do urcitej hlbky
		int size = vrcholiNaCeste.size();
		actualCount = new AtomicInteger(0);
		workers = new MyThreadPoolExecutor(2, size, false);
		logger.info("prepare workers for " + size + " items");
		Debug perftest = new Debug();
		for (Vertex vertex : vrcholiNaCeste) {
			workers.submit(new PackVerticesTask(vertex));
		}
		if (!workers.waitForAll()) {
			throw new RuntimeException("Vlakno sa zaseklo, privela vertexov ?");
		}
		workers = null;
		perftest.End("zabalene treshold:" + maxTreshold);
	}

	/**
	 * Zober vsetky susedny vrcholi, ak nepatria do nasho zoznamu zabal ich (schovaj).
	 * 
	 * @param v
	 * @param treshold
	 */
	protected void packVertex(Vertex v, int treshold) {
		if (treshold > maxTreshold) return;
		treshold++;
		Collection<Vertex> susedia = graph.getNeighbors(v);
		if (susedia.size() > 100) {
			logger.info("velky vrchol " + v);
		}

		for (Vertex vertex : susedia) {
			if (vrcholiNaCeste.contains(vertex)) continue; // Je to vrchol na ceste nechaj ho tak
			// vertex.setVisible(false);
			vertex.setPacked(true);
			vertex.setVisible(true);
			packVertex(vertex, treshold);
		}
	}

	/**
	 * Ideme opacne, po susedov a odkryva prvky
	 * 
	 * @param v
	 */
	public void unpack(Vertex v) {
		// if(v.isPacked() == false) return;
		unpackVertex(v, 1);
	}

	protected void unpackVertex(Vertex v, int treshold) {
		if (treshold > maxTreshold) return;
		treshold++;
		Collection<Vertex> susedia = graph.getNeighbors(v);
		for (Vertex vertex : susedia) {
			vertex.setPacked(false);
			vertex.setVisible(true);
			unpackVertex(vertex, treshold);
		}
	}

	protected void resetVerticesVisibility(VerticesPool vertices) {
		// Debug perftest = new Debug();
		for (VertexGroup group : vertices.getGroups()) {
			for (Vertex vertex : group.getVertices()) {
				vertex.setPacked(false);
				vertex.setZvyrazneny(false);
				vertex.setVisible(false);
			}
		}
		// perftest.End("defaultVertices");
	}

	protected void najkratsieCestyMM(
			Transformer<Integer, Float> edgeWeightTransformer,
			Set<Vertex> vybrane) {
		// Vrchol hladame cestu kazdy z kazdym...
		// Kedze hrany su jednosmerne, cesta z A=>B nieje ale B=>A moze byt
		// Skoda no, mohlo to byt rychlejsie keby to bolo obojsmerne
		// Debug perftest = new Debug();
		MyDijkstraShortestPath<Vertex, Integer> dijk;
		for (Vertex source : vybrane) {
			dijk = new MyDijkstraShortestPath<Vertex, Integer>(graph, edgeWeightTransformer, true);
			for (Vertex target : vybrane) {
				if (target == source) continue;
				dijk.getPathVertices(source, target, vrcholiNaCeste);
			}
			// Tu by bodlo vymazanie docasnych dat pre cesty
		}
	}

	public Integer getMaxTreshold() {
		return maxTreshold;
	}

	public void setMaxTreshold(Integer maxTreshold) {
		this.maxTreshold = maxTreshold;
	}
}

// Mame zoznam povolenych vrcholov, ostatne treba zmazat
// a) Bud prejdeme vsetke a vymazeme tie co nepasuju
// b) Alebo vymazeme rychlo vsetke a pridame len povolene

// b)
/*
 * synchronized (quad) {
 * quad.clear();
 * try {
 * for (V vertex : lenPovolene) {
 * //System.out.println(vertex);
 * vertex.setZvyrazneny(true);
 * quad.insert(vertex, vertex.getPoint());
 * }
 * }
 * catch (Exception e) {
 * e.printStackTrace();
 * }
 * }
 */
