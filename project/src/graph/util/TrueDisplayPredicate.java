
package graph.util;

import org.apache.commons.collections15.Predicate;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import graph.objects.Vertex;

/**
 * Pomocna trieda, vsetko zobrazuj.
 * 
 * @author Lukas Sekerak 
 * @param <A>
 */
public class TrueDisplayPredicate<A>
		implements
		Predicate<Context<Graph<Vertex, Integer>, A>>
{
	@Override
	public boolean evaluate(Context<Graph<Vertex, Integer>, A> arg0) {
		return true;
	}
}
