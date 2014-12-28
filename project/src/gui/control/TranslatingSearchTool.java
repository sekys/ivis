
package gui.control;

import java.util.regex.Pattern;
import render.IRenderEvaluator;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.VerticesPool;
import graph.objects.Vertex;
import graph.util.TranslatingSreen;
import gui.tooltip.IStatus;

/**
 * Trieda pre vyhladavanie vrcholov rozsirena o postuny prechod k nim.
 * 
 * @author Lukas Sekerak
 */
public class TranslatingSearchTool extends SearchTool
{
	protected TranslatingSreen	screenTranslating;
	protected IRenderEvaluator	renderEvaluator;
	protected IStatus			status;

	public TranslatingSearchTool(VisualizationViewer<Vertex, Integer> vv,
			VerticesPool pool,
			TranslatingSreen ts,
			IRenderEvaluator renderEvaluator,
			IStatus status) {
		super(pool);
		this.status = status;
		this.renderEvaluator = renderEvaluator;
		screenTranslating = ts;
	}

	/**
	 * Prepiseme succesFind, obrazovku posleme na najdeny vrchol.
	 */
	protected void successFind(Vertex v, Adresa actual) {
		super.successFind(v, actual);
		screenTranslating.goTo(v);

		renderEvaluator.evaluateRenderVertices(); // evaluator toto vyuziva ako cache, obnov ju pre
													// spravny vysledok
		if (!renderEvaluator.evaluateRenderVertex(v)) {
			// Dany vrchol naslo ale nezobrazzuje sa, je schovany....
			status.setStatus("...vrchol som nasiel ale je schovany.", 6);
			status.setStatus("...nastav filtre a odkry vrchol.", 9);
		}
	}

	/**
	 * Vrchol sme nenasli, pozri sa na konfiguraciu
	 */
	protected void notFound(Pattern txt) {
		status.setStatus("...ziadny vrchol nenajdeny.", 5);
	}
}
