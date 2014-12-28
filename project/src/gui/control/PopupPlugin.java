
package gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import quadtree.IQuadTree;
import render.DisplayControl;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import graph.GraphHolder;
import graph.VerticesPool;
import graph.objects.Vertex;
import graph.util.CEGV;
import graph.util.TranslatingSreen;
import gui.tooltip.IStatus;

/**
 * Menu po kliknuti pravym tlacitkom na vrchol.
 * 
 * @author Lukas Sekerak
 * 
 */
public class PopupPlugin extends AbstractPopupGraphMousePlugin
{
	protected JPopupMenu							popup	= new JPopupMenu();
	protected IQuadTree<Vertex>						quad;
	protected VisualizationViewer<Vertex, Integer>	m_vv;
	protected Transformer<Integer, Float>			edgeWeightTransformer;										;
	protected IStatus								status;
	protected VerticesPool							vertices;
	protected DisplayControl						dp;
	protected TranslatingSreen						screenTranslating;

	private final static Logger						logger	= Logger.getLogger(PopupPlugin.class.getName());

	public PopupPlugin(VisualizationViewer<Vertex, Integer> vv,
			GraphHolder holder,
			IStatus status,
			DisplayControl dp,
			TranslatingSreen ts) {
		this.edgeWeightTransformer = holder.getWeightTransformer();
		this.quad = holder.getQuad();
		this.m_vv = vv;
		this.status = status;
		this.vertices = holder.getVertices();
		this.dp = dp;
		screenTranslating = ts;
		logger.info(null);
	}

	private void najkratsiaCesta(Graph<Vertex, Integer> graph, Vertex source,
			Vertex other) {
		DijkstraShortestPath<Vertex, Integer> dijkstra = new DijkstraShortestPath<Vertex, Integer>(graph, edgeWeightTransformer, false);
		Number vzdialenost = dijkstra.getDistance(source, other);
		if (vzdialenost != null) {
			status.setStatus("...najkratöia cesta m· dÂûku " + vzdialenost
					+ ".", 7);
		} else {
			status.setStatus("...cesta medzi bodmi neexistuje.", 8);
		}
	}
	@SuppressWarnings("serial")
	private void Zvyraznenie(final VisualizationViewer<Vertex, Integer> vv,
			final Vertex source, final Set<Vertex> picked) {
		JMenu zvyrazniHlavne = new JMenu("Zv˝razni vrcholy");
		JMenu zrusHlavne = new JMenu("Zruö zv˝raznenie");
		popup.add(zvyrazniHlavne);
		popup.add(zrusHlavne);

		// Ak sa nachadzame na nejakom vrchole
		if (source != null) {
			zvyrazniHlavne.add(new AbstractAction("seba")
			{
				public void actionPerformed(ActionEvent e) {
					source.setZvyrazneny(true);
					vv.repaint();
				}
			});
			zrusHlavne.add(new AbstractAction("seba")
			{
				public void actionPerformed(ActionEvent e) {
					source.setZvyrazneny(false);
					vv.repaint();
				}
			});

			zvyrazniHlavne.add(new AbstractAction("susedov")
			{
				public void actionPerformed(ActionEvent e) {
					Collection<Vertex> susedia = vv.getGraphLayout().getGraph().getNeighbors(source);
					for (Vertex other : susedia) {
						other.setZvyrazneny(true);
					}
					vv.repaint();
				}
			});
			zrusHlavne.add(new AbstractAction("susedov")
			{
				public void actionPerformed(ActionEvent e) {
					Collection<Vertex> susedia = vv.getGraphLayout().getGraph().getNeighbors(source);
					for (Vertex other : susedia) {
						other.setZvyrazneny(false);
					}
					vv.repaint();
				}
			});
		}

		// Ak mame oznacenych viac ako jeden vrcholov
		if (picked.size() > 1) {
			zvyrazniHlavne.add(new AbstractAction("oznaËenÈ")
			{
				public void actionPerformed(ActionEvent e) {
					for (Vertex other : picked) {
						other.setZvyrazneny(true);
						logger.info(other.toString() + " zvyrazneny");
					}
					vv.repaint();
				}
			});
			zrusHlavne.add(new AbstractAction("oznaËenÈ")
			{
				public void actionPerformed(ActionEvent e) {
					for (Vertex other : picked) {
						other.setZvyrazneny(false);
					}
					vv.repaint();
				}
			});
		}
	}

	/**
	 * Callback pre klik na Vrchol
	 * 
	 */
	@SuppressWarnings("serial")
	private void myHandlePopup(final VisualizationViewer<Vertex, Integer> vv,
			final Vertex source, final Set<Vertex> picked) {
		// Kasli na to ak nic nemame
		if (picked.size() == 0 && source == null) return;

		if (source != null) {
			popup.add("Zvolen˝ vrchol:  " + source);

			Collection<Vertex> susedia = vv.getGraphLayout().getGraph().getNeighbors(source);
			if (susedia.size() > 0) {
				JMenu prejstKuSusedovi = new JMenu("Prejsù k susedovi");
				popup.add(prejstKuSusedovi);
				for (final Vertex other : susedia) {
					if (source == other) continue;
					prejstKuSusedovi.add(new AbstractAction(other.toString())
					{
						public void actionPerformed(ActionEvent e) {
							screenTranslating.goTo(other);
							vv.repaint();
						}
					});
				}
			}

			popup.add(new AbstractAction("Rozbaæ")
			{
				public void actionPerformed(ActionEvent e) {
					CEGV zabalova = new CEGV(vv.getGraphLayout().getGraph());
					zabalova.setMaxTreshold(dp.getMaxTreshold());
					zabalova.unpack(source);
					vv.repaint();
				}
			});
		}

		// Ide o mnozinu vrcholov
		if (picked.size() > 1) {
			// Prejdenie ku vrcholu + Najkratsia cesta
			JMenu prejstKuVrcholu = new JMenu("Prejsù k oznaËenÈmu");
			popup.add(prejstKuVrcholu);
			JMenu shortestPathMenu = null;

			if (source != null) {
				shortestPathMenu = new JMenu("Najkratöia cesta");
				popup.add(shortestPathMenu);
			}
			for (final Vertex other : picked) {
				if (source == other) continue;
				prejstKuVrcholu.add(new AbstractAction(other.toString())
				{
					public void actionPerformed(ActionEvent e) {
						screenTranslating.goTo(other);
						vv.repaint();
					}
				});
				if (shortestPathMenu != null) {
					shortestPathMenu.add(new AbstractAction(other.toString())
					{
						public void actionPerformed(ActionEvent e) {
							najkratsiaCesta(vv.getGraphLayout().getGraph(), source, other);
							vv.repaint();
						}
					});
				}
			}

			// Najkratsie cesty - many to many
			popup.add(new AbstractAction("Najkratöia cesta - Many to Many")
			{
				public void actionPerformed(ActionEvent e) {
					CEGV zabalova = new CEGV(vv.getGraphLayout().getGraph());
					zabalova.setMaxTreshold(dp.getMaxTreshold());
					zabalova.compute(picked, edgeWeightTransformer, vertices);
					vv.repaint();
				}
			});
		}

		Zvyraznenie(vv, source, picked);
	}

	/**
	 * Callback pre klik na objekt v grafe
	 */
	protected void handlePopup(MouseEvent e) {
		popup.removeAll();
		@SuppressWarnings("unchecked")
		VisualizationViewer<Vertex, Integer> vv = (VisualizationViewer<Vertex, Integer>) e.getSource();
		GraphElementAccessor<Vertex, Integer> pickSupport = vv.getPickSupport();

		if (pickSupport != null) {
			Vertex source = pickSupport.getVertex(vv.getGraphLayout(), e.getX(), e.getY());
			// if (source != null) {
			myHandlePopup(vv, source, vv.getPickedVertexState().getPicked());
			// }
			if (popup.getComponentCount() > 0) {
				popup.show(vv, e.getX(), e.getY());
			}
		}
	}
}
