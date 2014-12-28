
package graph.objects;

import graph.util.CompareVertexBySizeThenValueHash;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import util.SortedArrayList;
import nio.base.BufferAutoRegisteringFactory;
import nio.base.IBufferFactory;
import nio.base.IDNio;
import nio.mapping.DynamicSimpleStore;

/**
 * Vsetky vertexi su ulozene vzdy v nejakej skupine (VertexGroup).
 * Vrchol je zaradeny podla jeho kluca.
 * 
 * @author Lukas Sekerak
 * 
 */
public class VertexGroup implements Comparable<String>
{
	private static final long						serialVersionUID	= -1408029093367841391L;
	protected String								m_keyname;
	protected Color									m_color;
	protected transient DynamicSimpleStore<IDNio>	metadata;
	protected transient ArrayList<Vertex>			vertices;

	public VertexGroup(String key, File keyfolder, IBufferFactory vm) {
		m_keyname = key;
		m_color = Color.red;
		vertices = new ArrayList<Vertex>(1000000);
		keyfolder.mkdirs();

		try {
			metadata = new DynamicSimpleStore<IDNio>();;
			metadata.Initialize(new File(keyfolder + "/metadata.db"), new BufferAutoRegisteringFactory(), 100 * 1024);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return m_keyname;
	}

	public boolean equals(Object obj) {
		return m_keyname == ((VertexGroup) obj).m_keyname;
	}

	public int compareTo(String anotherkey) {
		return m_keyname.compareTo(anotherkey);
	}

	public int find(char[] bytes, Vertex hladany, VertexMetaData meta) {
		int firstindex = vertices.indexOf(hladany);
		if (firstindex == -1) return -1;

		for (int i = firstindex; i < vertices.size(); i++) {
			metadata.read(vertices.get(i), meta);
			if (Arrays.equals(meta.value, bytes)) {
				return i;
			}
			if (vertices.get(i).getSize() != hladany.getSize()) break;
			if (vertices.get(i).getValueHash() != hladany.getValueHash()) break;
		}
		return -1;
	}

	public int find(Vertex hladany) {
		int firstindex = vertices.indexOf(hladany);
		if (firstindex == -1) return -1;
		Vertex v;

		for (int i = firstindex; i < vertices.size(); i++) {
			v = vertices.get(i);
			if (v.size != hladany.size) break;
			if (v.getValueHash() != hladany.getValueHash()) break;
			if (hladany.equals(v)) {
				return i;
			}
		}
		return -1;
	}

	public void sort() {
		Collections.sort(vertices, new CompareVertexBySizeThenValueHash());
		vertices = new SortedArrayList<Vertex>(vertices, new CompareVertexBySizeThenValueHash());
	}
	public void autosort() {
		vertices = new SortedArrayList<Vertex>(vertices, new CompareVertexBySizeThenValueHash());
	}
	/*
	 * Uz nepotrebujem.
	 * 
	 * public void SerializeNextParts(File folder) throws IOException {
	 * ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(getGroupFile(folder)));
	 * try {
	 * os.writeObject(vertices);
	 * metadata.Serialize(os);
	 * os.flush();
	 * }
	 * catch (IOException e) {
	 * throw e;
	 * }
	 * finally {
	 * os.close();
	 * }
	 * }
	 */
	public void add(Vertex v, VertexMetaData m) {
		metadata.write(v, m);
		vertices.add(v);
	}

	protected static File getGroupFile(File folder) {
		return new File(folder + "/groupinfo.db");
	}
	/*
	 * Uz nepotrebujem.
	 * 
	 * @SuppressWarnings("unchecked")
	 * public void deSerializeNextParts(File folder, IBufferFactory factory)
	 * throws FileNotFoundException, IOException, ClassNotFoundException {
	 * File groupinfo = new File(folder + "/groupinfo.db");
	 * ObjectInputStream ois = new ObjectInputStream(new FileInputStream(groupinfo));
	 * vertices = (ArrayList<Vertex>) ois.readObject();
	 * metadata.deSerialize(new File(folder + "/metadata.db"), ois, factory);
	 * }
	 */
	public Color getColor() {
		return m_color;
	}
	public void setColor(Color color) {
		m_color = color;
	}
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Restartni viditelnost prvkov
	 */
	public void resetVisibility() {
		for (Vertex v : vertices) {
			v.setZvyrazneny(false);
			v.setVisible(true);
			v.setPacked(false);
		}
	}
}
