
package graph;

import graph.objects.Edge;
import graph.objects.VertexGroup;
import graph.objects.Vertex;
import graph.objects.VertexMetaData;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import util.IteratorBiDimensionalArray;
import util.Pool;
import nio.base.IBufferFactory;

/**
 * Tato trieda sa stara o ulozenie vsetkych vrcholov do pametovo namapovanych suborov.
 * Vrcholy su ulozene najprv do skupin podla ich kluca.
 * 
 * @author Lukas Sekerak
 */
public class VerticesPool extends Pool<String, VertexGroup>
		implements
		Collection<Vertex>
{
	protected File				m_folder;
	protected IBufferFactory	m_vm;

	/**
	 * Vytvor pametovo namapovanu zlozku pre skupiny.
	 * 
	 * @param folder
	 * @param virtualmemory
	 */
	public void Initialize(File folder, IBufferFactory virtualmemory) {
		m_folder = folder;
		m_vm = virtualmemory;
	}
	/*
	 * Nedokoncene, je to uz nepotrebne.
	 * 
	 * public void Serialize() throws IOException {
	 * SerializeUtil.Serialize(new File(m_folder + "vertex.db"), pool);
	 * }
	 * 
	 * @SuppressWarnings("unchecked")
	 * public void deSerialize() throws IOException, ClassNotFoundException {
	 * pool = (ArrayList<VertexGroup>) SerializeUtil.deSerialize(new File(m_folder
	 * + "vertex.db"));
	 * }
	 */

	/**
	 * Vrat pametovo namapovanu zlozku.
	 */
	public File getFolder() {
		return m_folder;
	}

	/**
	 * Vrat pod zlozku pre urcitu skupinu ID
	 * 
	 * @param id
	 * @return
	 */
	public File getFolder(int id) {
		return new File(m_folder + "/Vertexs/Key_ID" + id + "/");
	}

	/**
	 * Vrat vrchol podla skupiny a indexu v skupine.
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public Vertex get(char key, int index) {
		return pool.get(key).getVertices().get(index);
	}

	/**
	 * Vrat pociatocny vrchol pre hranu.
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	public Vertex getSource(Edge hrana) {
		return get(hrana.getSourceKey(), hrana.getSourceIndex());
	}

	/**
	 * Vrat koncovy vrchol pre hranu.
	 * 
	 * @param hrana
	 * @return
	 */
	public Vertex getTarget(Edge hrana) {
		return get(hrana.getTargetKey(), hrana.getTargetIndex());
	}

	/**
	 * Pridaj vrchol aj jeho metadata.
	 * 
	 * @param v
	 * @param m
	 */
	public void add(Vertex v, VertexMetaData m) {
		get(v.getKey()).add(v, m);
	}

	/**
	 * Pocet vsetkych vrcholov.
	 */
	@Override
	public int size() {
		int size = 0;
		for (VertexGroup g : pool) {
			size += g.getVertices().size();
		}
		return size;
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public boolean add(Vertex e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public boolean addAll(Collection<? extends Vertex> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Zisti ci sa tento vrchol nenachadza v skupine.
	 */
	@Override
	public boolean contains(Object o) {
		return contains((Vertex) o);
	}

	/**
	 * Zisti ci sa tento vrchol nenachadza v skupine.
	 */
	public boolean contains(Vertex v) {
		if (v.getKey() > -1 && v.getKey() < pool.size()) {
			// TODO: namiesto find pouzit nieco rychlejsie - priamo pristupit k prvku a spytat sa ze
			// ci hashe su rovnake
			return this.get(v.getKey()).find(v) > -1;
		}
		return false;
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Je VerticesPol prazdny ?
	 */
	@Override
	public boolean isEmpty() {
		for (VertexGroup g : pool) {
			if (g.getVertices().size() > 0) return false;
		}
		return true;
	}

	/**
	 * Vrat iterator pre vsetky vrcholy.
	 * IteratorBiDimensionalArray premiena nase vsetky skupiny do
	 * abstrakneho jedneho pola, pricom sa nepridava na pameti.
	 */
	@Override
	public Iterator<Vertex> iterator() {
		return new IteratorBiDimensionalArray<VertexGroup, Vertex>()
		{
			@Override
			protected Iterator<VertexGroup> getRowIterator() {
				return pool.iterator();
			}

			@Override
			protected Iterator<Vertex> RowToIterator(VertexGroup row) {
				return row.getVertices().iterator();
			}

		};
	}

	/**
	 * Vymaz vrchol.
	 */
	@Override
	public boolean remove(Object o) {
		return remove(o);
	}

	/**
	 * Vymaz vrchol.
	 */
	public boolean remove(Vertex v) {
		if (v.getKey() > -1 && v.getKey() < pool.size()) {
			return this.get(v.getKey()).getVertices().remove(v);
		}
		return false;
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Nepodporovane.
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Metoda pri zachyteni noveho typu elemntu, teda novej skupiny
	 * vytvori novu skupinu pre vrcholy.
	 */
	protected VertexGroup NewElement(String key, int index) {
		return new VertexGroup(key, getFolder(index), m_vm);
	}
	
	/* Uz nepotrebujem.
	 * 
	 * public void SortAndSerialize() {
	 * // Zorad vsetke skupiny a serializuj
	 * int size = getGroups().size();
	 * MyThreadPoolExecutor workers = new MyThreadPoolExecutor(1, size);
	 * for (int i = 0; i < size; i++) {
	 * workers.submit(new SortSerializeGroupTask(get(i)));
	 * }
	 * 
	 * // Cakaj kym vsetci skoncia
	 * workers.shutdown();
	 * try {
	 * workers.awaitTermination(1, TimeUnit.HOURS);
	 * }
	 * catch (InterruptedException e) {
	 * e.printStackTrace();
	 * }
	 * }
	 */
}
