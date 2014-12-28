
package graph.objects;

import java.awt.geom.Point2D;
import graph.VerticesPool;
import quadtree.INodeElement;
import quadtree.IQuadTree;
import quadtree.Node;

/**
 * Vertex alebo vrchol v grafe rozsireny o dalsie metody.
 * 
 * @author Lukas Sekerak
 */
public class Vertex extends BaseVertex implements INodeElement<Vertex>
{
	private static final long	serialVersionUID	= 8839621212009485941L;

	public void setFPriority(int fpriority) {
		VertexMetaData meta = VertexMetaData.PriorityOnly();
		pool.get(getKey()).metadata.write(this, meta);
	}
	public int getFPriority() {
		VertexMetaData meta = VertexMetaData.PriorityOnly();
		pool.get(getKey()).metadata.read(this, meta);
		return meta.priority;
	}
	public Point2D getPoint() {
		return this;
	}
	@Override
	public String toString() {
		return getKeyValuePriority();
	}

	public String getKeyValuePriority() {
		VertexMetaData meta = new VertexMetaData();
		pool.get(getKey()).metadata.read(this, meta);
		return pool.get(getKey()).m_keyname + "=>" + new String(meta.value)
				+ " (" + meta.priority + ")";
	}
	public String getKeyValue() {
		VertexMetaData meta = new VertexMetaData();
		pool.get(getKey()).metadata.read(this, meta);
		return pool.get(getKey()).m_keyname + "=>" + new String(meta.value);
		// return x + "   " + y;
	}
	public String getValue() {
		VertexMetaData meta = new VertexMetaData();
		pool.get(getKey()).metadata.read(this, meta);
		return new String(meta.value);
	}

	/**
	 * Ked porovnavam 2 vrcholy zaujima ma kde oni odkazuju
	 * na pamet v subore.
	 */
	@Override
	public int compareTo(Vertex b) {
		if (getKey() == b.getKey()) {
			if (pos < b.pos) {
				return -1;
			}
			if (pos > b.pos) {
				return 1;
			}
			return 0;
		}
		if (getKey() < b.getKey()) {
			return -1;
		}
		if (getKey() > b.getKey()) {
			return 1;
		}
		return 0;
	}
	/**
	 * Pretazena metoda setLocation z Point2D za ucelom neskorsieho
	 * kontaktovania quad stromu.
	 * 
	 * @Override
	 */
	public void setLocation(double x, double y) {
		synchronized (qt) {
			if (!qt.remove(this, this)) {
				// Nenaslo prvok, test spravnosti ci existuje
				Node<Vertex> node = qt.findSlow(this);
				if (node != null) {
					throw new RuntimeException("Vertex nevymazalo ale nachadza sa tam.");
				}
			}
			setLocationNoUpdate(x, y);
			qt.insert(this, this);
			// qt.changeLocation(this, new Point2D.Double(x, y));
		}
	}

	/**
	 * Interface of QuadTree. Vertex send messages to QuadTree about new
	 * location.
	 */
	private static IQuadTree<Vertex>	qt;
	private static VerticesPool			pool;

	/**
	 * Dalsie pomocne gettre a settre
	 * 
	 * @param qt
	 */
	public static void setQuadTree(IQuadTree<Vertex> qt) {
		Vertex.qt = qt;
	}

	public static void setPool(VerticesPool poool) {
		pool = poool;
	}
}
