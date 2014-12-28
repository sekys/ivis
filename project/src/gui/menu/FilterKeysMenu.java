
package gui.menu;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.VerticesPool;
import graph.objects.Vertex;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JMenu;
import org.apache.log4j.Logger;
import quadtree.IQuadTree;

/**
 * FilterKeysMenu vytvara menu v hornej casti obrazovky.
 * Stara sa zobrazenie / skrytie jednotlivych skupin vrcholov.
 * 
 * @author Lukas Sekerak
 */
public class FilterKeysMenu extends JMenu implements ActionListener
{
	private static final long			serialVersionUID	= 4715066417090437237L;
	protected IQuadTree<Vertex>			quad;
	protected VerticesPool				pool;
	protected VisualizationViewer<?, ?>	vv;

	private final static Logger			logger				= Logger.getLogger(FilterKeysMenu.class.getName());

	public FilterKeysMenu(VisualizationViewer<?, ?> vizual) {
		super("Skupiny vrcholov");
		this.setMnemonic(KeyEvent.VK_K);
		this.vv = vizual;
		this.setToolTipText("Tu je moûnosù pre zapnutie alebo vypnutie zobrazovania skupÌn.");
		MenuScroller.setScrollerFor(this, 9, 125, 0, 0);
	}

	public void build(IQuadTree<Vertex> quad, VerticesPool pool) {
		this.quad = quad;
		this.pool = pool;
		rebuild();
	}

	@SuppressWarnings("serial")
	protected class MenuItem extends JCheckBoxMenuItemDontClose
	{
		protected char	key;

		public char getKey() {
			return key;
		}
		public MenuItem(char key) {
			super();
			this.setSelected(true);
			this.key = key;
		}
		public String getText() {
			return pool.get(key) + " (" + pool.get(key).getVertices().size()
					+ ")";
		}
		public Color getForeground() {
			return pool.get(key).getColor();
		}
	}

	public void rebuild() {
		this.removeAll();
		int size = pool.getGroups().size();
		for (int i = 0; i < size; i++) {
			MenuItem item = new MenuItem((char) i);
			item.addActionListener(this);
			this.add(item);
		}
	}
	public void actionPerformed(ActionEvent e) {
		MenuItem item = (MenuItem) e.getSource();
		char key = item.getKey();
		String name = pool.get(key).toString();
		logger.info("start " + name);
		// synchronized (quad) {
		if (item.isSelected()) {
			// Pridaj vsetke prvky zo skupin do QD stromu
			for (Vertex v : pool.get(key).getVertices()) {
				// quad.insert(v, v.getPoint());
				v.setVisible(true);
			}
		} else {
			// quad.removeAll(new VertexDeleterByKey(key));
			for (Vertex v : pool.get(key).getVertices()) {
				v.setVisible(false);
			}
		}
		// }

		logger.info("end " + name);
		vv.repaint();
	}

	/*
	 * @SuppressWarnings("serial")
	 * 
	 * private class VertexDeleterByKey
	 * {
	 * protected char key;
	 * 
	 * public VertexDeleterByKey(char id) {
	 * key = id;
	 * }
	 * public boolean equals(Object b) {
	 * return key == ((Vertex) b).getKey();
	 * }
	 * }
	 */
}
