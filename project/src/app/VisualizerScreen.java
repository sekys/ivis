
package app;

import graph.DirectedCorpusGraph;
import graph.GraphHolder;
import graph.MyLayout;
import graph.objects.Vertex;
import graph.util.TranslatingSreen;
import graph.util.TrueDisplayPredicate;
import gui.control.HelpButton;
import gui.control.MouseManager;
import gui.control.QuadTreeShapePickSupport;
import gui.control.ScreenPrint;
import gui.control.FilterPanel;
import gui.control.TranslatingSearchTool;
import gui.control.ZoomPanel;
import gui.menu.EdgeMenu;
import gui.menu.FileMenu;
import gui.menu.FilterKeysMenu;
import gui.menu.VerticesMenu;
import gui.tooltip.ToolTips;
import io.FileManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.log4j.Logger;
import render.MyVertexFillPaintFunction;
import render.RenderThroughtQuadTree;
import render.MyVertexDrawPaintFunction;
import render.DisplayControl;
import render.RenderWithStats;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.NumberFormattingTransformer;
import edu.uci.ics.jung.visualization.renderers.*;

/**
 * Vizualizacna obrazovka.
 * 
 * @author Lukas Sekerak
 */
public class VisualizerScreen extends HomeScreen
{
	protected VisualizationViewer<Vertex, Integer>	vv;
	protected FileManager							fm;
	protected DisplayControl						visualControl;
	protected ToolTips								status;
	protected TranslatingSreen						ts;
	private final static Logger						logger	= Logger.getLogger(VisualizerScreen.class.getName());

	public VisualizerScreen() {
		super();
		fm = new FileManager(frame);
		status = new ToolTips();
	}
	
	protected void openFile(File file) throws Exception {
		logger.info("openFile " + file);
		fm.open(file);
	}
	
	private void buildToolBar(GraphHolder holder) {
		// Pridaj control panel dole
		logger.info("start");
		ZoomPanel zoom = new ZoomPanel(vv);
		MouseManager graphMouse = new MouseManager(vv, holder, status, visualControl, ts);
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		graphMouse.setMode(ModalGraphMouse.Mode.EDITING);

		// Panel nastrojov
		JPanel controls = new JPanel();
		zoom.registerButtons(controls);
		controls.add(new ScreenPrint(vv));
		controls.add(graphMouse.getModeComboBox());
		controls.add(new HelpButton());

		TranslatingSearchTool search = new TranslatingSearchTool(vv, holder.getVertices(), ts, visualControl, status);
		controls.add(new FilterPanel(vv, holder.getVertices(), visualControl, search));
		frame.getContentPane().add(controls, BorderLayout.SOUTH);
		logger.info("end");
	}

	private void buildMenu(File file, DirectedCorpusGraph graph) {
		// Pridaj polocky do menu
		logger.info("start");
		menu.add(new FileMenu(file, graph));
		menu.add(new VerticesMenu(vv, graph, graph.getHolder().getVolumeTransformer(), graph.getHolder(), visualControl));
		FilterKeysMenu filtermenu = new FilterKeysMenu(vv);
		filtermenu.build(graph.getHolder().getQuad(), graph.getHolder().getVertices());
		menu.add(filtermenu);
		menu.add(new EdgeMenu(vv, graph.getHolder().getWeightTransformer(), visualControl));
		menu.add(Box.createHorizontalGlue());
		menu.add(status.getComponent());
		// status.start();
		logger.info("end");
	}

	@Override
	protected void successOpen(File file) {
		// Vymaz stare casti
		logger.info("successOpen");
		frame.getContentPane().removeAll();
		menu.removeAll();
		logger.info("removeAll end");
		System.out.println("removeAll");

		// Postav nove casti
		DirectedCorpusGraph graph = (DirectedCorpusGraph) fm.getGraph();
		visualScreen(graph);
		buildMenu(file, graph);
		frame.getContentPane().add(new GraphZoomScrollPane(vv));
		buildToolBar(graph.getHolder());
		status.setStatus("...súbor úspešne naèitaný.", 1);
		logger.info("builded end");

		// Okno este uprav
		frame.pack();
		frame.setSize(800, 600);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setTitle(file.getName());
		frame.repaint();
		logger.info("reset frame end");
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void visualScreen(DirectedCorpusGraph graph) {
		logger.info(null);
		GraphHolder holder = graph.getHolder();

		// Nastav vlastnosti vrstvy
		ISOMLayout<Vertex, Integer> layout = new MyLayout(graph);
		Point2D size = holder.getQuad().getPreferredSize();
		Dimension dim = new Dimension((int) size.getX(), (int) size.getY()) ;
		layout.setSize(new Dimension(dim));
		logger.info("set layout end");

		// Vytvor render predicate zobrazovania
		visualControl = new DisplayControl(graph, holder.getVolumeTransformer(), holder.getWeightTransformer2());
		RenderThroughtQuadTree render = new RenderWithStats(holder, status);
		render.setControl(visualControl);
		logger.info("set render end");

		// Vlastnosti vizualatora
		vv = new VisualizationViewer<Vertex, Integer>(layout, new Dimension(600, 600));
		vv.setPickSupport(new QuadTreeShapePickSupport(vv, holder.getQuad(), visualControl));
		visualControl.setVisualizationViewer(vv);
		vv.setIgnoreRepaint(true); // Pre lepsiu performance
		vv.setRenderer(render);

		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex, Integer>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.NW);
		vv.getRenderContext().setVertexDrawPaintTransformer(new MyVertexDrawPaintFunction(holder.getVertex2ColorTransformer()));
		vv.getRenderContext().setVertexFillPaintTransformer(new MyVertexFillPaintFunction(vv.getPickedVertexState()));
		vv.getRenderContext().setVertexIncludePredicate(new TrueDisplayPredicate<Vertex>());
		vv.getRenderContext().setEdgeIncludePredicate(new TrueDisplayPredicate<Integer>());

		// hrany, ich labely
		vv.getRenderContext().setEdgeLabelTransformer(new NumberFormattingTransformer<Integer>(holder.getWeightTransformer()));
		vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.lightGray));
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
		vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer(Color.lightGray));
		logger.info("set VisualizationViewer");

		JPanel comp = new JPanel();
		comp.setSize(500, 500);
		comp.setAlignmentX(1.0f);
		comp.setAlignmentY(0.5f);
		JLabel tooltip = new JLabel("Hladaj");
		tooltip.setBackground(Color.RED);
		tooltip.setSize(100, 100);
		comp.add(tooltip);
		comp.setBackground(Color.GRAY);
		logger.info("added control");

		/* Vertex mapa je vypnuta.
		 * 
		 * VertexMap map = new VertexMap(new Dimension(100, 80), holder.getQuad().getSize(),
		 * holder.vertices);
		 * vv.add(map.getPanel());
		 * logger.info("added map");
		 */
		ts = new TranslatingSreen(vv);

	}
}
