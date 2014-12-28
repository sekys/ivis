
package graph;

import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import graph.objects.Edge;
import graph.objects.VertexGroup;
import graph.objects.Vertex;
import graph.objects.VertexMetaData;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import quadtree.QuadTree;
import util.NotFound;
import nio.base.BufferAutoRegisteringFactory;
import nio.base.IBufferFactory;

/**
 * Trieda sa stara o ulozenie celeho grafu, respektive riadi
 * ulozenie Vrcholov a hran. Riadi cely virtualny priestor.
 * 
 * @author Lukas Sekerak
 */
public class GraphHolder
{
	private boolean							vertexLoaded;
	private IBufferFactory					virtualmemory;
	private Transformer<Vertex, Point2D>	RandomPointTransformer;
	private QuadTree<Vertex>				quad;
	private VerticesPool					vertices;
	private Edges							edges;

	private final static Logger				logger	= Logger.getLogger(GraphHolder.class.getName());

	/**
	 * File folder - do ktorej triedy sa budu ukladat pametovo namapovane subory.
	 * 
	 * @param folder
	 */
	public GraphHolder(File folder) {
		// Priprav virtualnu pamet, pomocne vlakna
		logger.info("GraphHolder start");
		folder.mkdirs();
		virtualmemory = new BufferAutoRegisteringFactory();
		vertices = new VerticesPool()
		{
			protected VertexGroup NewElement(String key, int index) {
				VertexGroup group = super.NewElement(key, index);
				group.autosort();
				return group;
			}
		};
		edges = new Edges(folder, virtualmemory);
		vertices.Initialize(folder, virtualmemory);
		logger.info("GraphHolder loaded");
	}

	/**
	 * Pridaj Vrchol zo suboru do grafu.
	 * 
	 * @param fkey
	 * @param fvalue
	 * @param fpriority
	 */
	public void addVertexFromFile(String fkey, String fvalue, int fpriority) {
		if (vertexLoaded) {
			throw new RuntimeException("Vrcholi a hrany v subore nemozu byt pomiesane. Najprv musia byt vrcholi, potom hrany.");
		}
		char[] value = fvalue.toCharArray();
		if (value.length > Character.MAX_VALUE) {
			throw new RuntimeException("Dlzka value je viacej ako "
					+ Integer.toString(Character.MAX_VALUE) + " znakov.");
		}
		/*if (fpriority > Character.MAX_VALUE) {
			throw new RuntimeException("Dlzka priority je viacej ako "
					+ Integer.toString(Character.MAX_VALUE) + ".");
		}*/

		VertexMetaData meta = new VertexMetaData();
		Vertex vertex = new Vertex();
		vertex.setKey((char) vertices.getByKey(fkey));
		vertex.setValueHash(fvalue.hashCode());
		meta.set(value, fpriority);
		vertices.add(vertex, meta);
	}

	/**
	 * Posli spravu po spracovani suboru, je potrebne poslat spravu
	 * aby graf sa preusporiadal a pod.
	 */
	public void ProcessOfReadingEnd() {
		logger.info("ProcessOfReadingEnd end");
		buildColorPalette();
		buildQuadTree();
		Vertices2QuadTreeTask();
	}

	/**
	 * Pridaj hranu zo suboru do grafu.
	 * 
	 * @param k1
	 * @param v1
	 * @param k2
	 * @param v2
	 * @param ohodnotenie
	 */
	public void addEdgeFromFile(String k1, String v1, String k2, String v2,
			int ohodnotenie) {
		try {
			Edge edge = new Edge();
			VertexGroup group;
			VertexMetaData meta = new VertexMetaData();
			Vertex hladany = new Vertex();
			int key, index;
			char[] bytes;
			String value;

			key = vertices.find(k1);
			if (key == -1) throw new NotFound();
			// edge.sourceKey = (char) index;
			group = vertices.get(key);
			value = v1;
			bytes = value.toCharArray();
			hladany.setSize((char) VertexMetaData.Size(bytes.length));
			hladany.setValueHash(value.hashCode());
			index = group.find(bytes, hladany, meta);
			if (index == -1) throw new NotFound();
			edge.setSource((char) key, index);

			key = vertices.find(k2);
			if (key == -1) throw new NotFound();
			group = vertices.get(key);
			value = v2;
			bytes = value.toCharArray();
			hladany.setSize((char) VertexMetaData.Size(bytes.length));
			hladany.setValueHash(value.hashCode());
			index = group.find(bytes, hladany, meta);
			if (index == -1) throw new NotFound();
			edge.setTarget((char) key, index);

			edge.setPriority(ohodnotenie);
			index = edges.add(edge);
			vertices.getSource(edge).AddOut(index);
			vertices.getTarget(edge).AddInc(index);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Vrat quad strom.
	 * 
	 * @return
	 */
	public QuadTree<Vertex> getQuad() {
		return quad;
	}

	/**
	 * Pozicny transformer
	 */
	public static Transformer<Vertex, Point2D> getPositionTransformer() {
		return new Transformer<Vertex, Point2D>()
		{
			public Point2D transform(Vertex arg0) {
				return arg0; // Spravime auto casting pre transformera :)
			}
		};
	}

	/**
	 * Label transformer
	 */
	public Transformer<Vertex, Float> getVolumeTransformer() {
		return new Transformer<Vertex, Float>()
		{
			@Override
			public Float transform(Vertex v) {
				// v LOG(1) je 0, treba aby do LOG isla aspon 2
				// Zakladny vertex s prioritou 1 sa zobrazi vacsinou ako 0.25 maly trojuholnik
				return new Float(Math.log((float) v.getFPriority() + 2.0f) / 4.0);
				// * 10 + v.getSusednychHran()
			}

		};
	}

	/**
	 * Transformer z hrany na vahu hrany.
	 * 
	 * @return
	 */
	public Transformer<Integer, Float> getWeightTransformer() {
		return new Transformer<Integer, Float>()
		{
			@Override
			public Float transform(Integer edge) {
				Edge hrana = Edge.LoadPriorityOnly();
				edges.get(edge, hrana);
				return new Float((float) hrana.getPriority());
			}
		};
	}

	/**
	 * Transformer z hrany (ktora je v pameti) na vahu hrany.
	 * 
	 * @return
	 */
	public Transformer<Edge, Float> getWeightTransformer2() {
		return new Transformer<Edge, Float>()
		{
			@Override
			public Float transform(Edge hrana) {
				return new Float((float) hrana.getPriority());
			}
		};
	}

	/**
	 * Transformer z vrchola na farbu vrchola.
	 * 
	 * @return
	 */
	public Transformer<Vertex, Color> getVertex2ColorTransformer() {
		return new Transformer<Vertex, Color>()
		{
			public Color transform(Vertex v) {
				return vertices.get(v.getKey()).getColor();
			}
		};
	}

	private void buildColorPalette() {
		int size = vertices.getGroups().size();
		Color randomColor;
		for (int i = 0; i < size; i++) {
			randomColor = Color.getHSBColor((float) i / (float) size, 0.85f, 1.0f);
			vertices.get(i).setColor(randomColor);
		}
	}

	private void Vertices2QuadTreeTask() {
		Point2D p;
		synchronized (quad) {
			for (VertexGroup group : vertices.getGroups()) {
				for (Vertex v : group.getVertices()) {
					p = RandomPointTransformer.transform(v);
					v.setLocationNoUpdate(p.getX(), p.getY());
					quad.insert(v, v.getPoint());
				}
			}
		}
	}

	private void buildQuadTree() {
		// Vytvor quad strom
		int VertexCount = vertices.size();
		if (VertexCount < 1000) VertexCount = 1000;
		quad = new QuadTree<Vertex>(VertexCount * 1.1);
		Point2D size = quad.getPreferredSize();
		Dimension dim = new Dimension((int) size.getX(), (int) size.getY()) ;
		RandomPointTransformer = new RandomLocationTransformer<Vertex>(dim);
		Vertex.setQuadTree(quad); // Nastav vlastnosti vertexov
		Vertex.setPool(vertices);
		logger.info("buildQuadTree success");
	}

	/**
	 * Vrat vrcholy v tomto grafe.
	 * 
	 * @return
	 */
	public VerticesPool getVertices() {
		return vertices;
	}

	/**
	 * Vrat hrany v tomto grafe.
	 * 
	 * @return
	 */
	public Edges getEdges() {
		return edges;
	}
}
