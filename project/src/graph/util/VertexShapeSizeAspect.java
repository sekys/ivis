/*
 * Copyright (c) 2004, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 *
 * Created on Nov 7, 2004
 */

package graph.util;

import java.awt.Shape;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;

/**
 * Controls the shape, size, and aspect ratio for each vertex.
 * 
 * @author Joshua O'Madadhain
 */
public class VertexShapeSizeAspect<V, E>
		extends
			AbstractVertexShapeTransformer<V> implements Transformer<V, Shape>
{

	protected boolean				stretch			= false;
	protected boolean				scale			= false;
	protected boolean				funny_shapes	= false;
	protected Transformer<V, Float>	voltages;
	protected Graph<V, E>			graph;

	public VertexShapeSizeAspect(Graph<V, E> graphIn,
			Transformer<V, Float> voltagesIn) {
		this.graph = graphIn;
		this.voltages = voltagesIn;
		setSizeTransformer(new Transformer<V, Integer>()
		{

			public Integer transform(V v) {
				if (scale) return (int) (voltages.transform(v) * 30) + 20;
				else return 20;

			}
		});
		setAspectRatioTransformer(new Transformer<V, Float>()
		{

			public Float transform(V v) {
				if (stretch) {
					return (float) (graph.inDegree(v) + 1)
							/ (graph.outDegree(v) + 1);
				} else {
					return 1.0f;
				}
			}
		});
	}

	public void setStretching(boolean stretch) {
		this.stretch = stretch;
	}

	public void setScaling(boolean scale) {
		this.scale = scale;
	}

	public void useFunnyShapes(boolean use) {
		this.funny_shapes = use;
	}

	public Shape transform(V v) {
		if (funny_shapes) {
			if (graph.degree(v) < 5) {
				int sides = Math.max(graph.degree(v), 3);
				return factory.getRegularPolygon(v, sides);
			} else return factory.getRegularStar(v, graph.degree(v));
		} else return factory.getEllipse(v);
	}
}
