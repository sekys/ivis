
package gui.control;

import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import graph.VerticesPool;
import graph.objects.Vertex;

/**
 * Trieda pre vyhladavanie vrcholov.
 * Zabezspecuje vratenie sa k vrcholu.
 * 
 * @author Lukas Sekerak
 */
public class SearchTool
{
	protected VerticesPool		pool;
	private Pattern				oldPattern;
	private Adresa				last;

	private final static Logger	logger	= Logger.getLogger(SearchTool.class.getName());

	protected class Adresa
	{
		public int	key;
		public int	index;

		public void reset() {
			key = -1;
			index = -1;
		}
		public String toString() {
			return new String("Key " + Integer.toString(key) + " Index "
					+ Integer.toString(index));
		}
	}

	public SearchTool(VerticesPool pool) {
		this.pool = pool;
		last = new Adresa();
		oldPattern = null;
	}

	private boolean test(Pattern txt) {
		// Uzivatel zadal prazny text, je to tiez zmena
		if (txt == null) {
			last.reset();
			return false;
		}

		// Stary pattern a novy pattern su odlisne...
		if (oldPattern == null || !oldPattern.pattern().equals(txt.pattern())) {
			// Teda zacni hladanie odznova
			last.reset();
		}

		oldPattern = txt;
		logger.info(last);
		return true;
	}

	/**
	 * Prehladavaj mnozinu od urciteho bodu k urcitemu limitnemu bodu a urci ktorym smerom.
	 * - Je potrebne argumenty posuvat ako referencie preto trieda
	 * - Po skonceni funkcie sa aktualny actual ulozi a ten sa neskor pouzije preto referencia
	 * - limit = horne / dolne ohranicenie
	 * 
	 * @return null - nenajdeny
	 */
	protected Vertex find(Pattern hladany, Adresa actual, Adresa limit,
			boolean inc) {
		/*
		 * Kedze vyhladavanie chceme mat super rychle nebudem sa abstrakne vysoko pytat na
		 * Vertex.toString() ale rovno z pamete suboru - bude sa menej krat alokovat pamet.
		 * 
		 * VertexMetaData meta = new VertexMetaData();
		 * pool.get(getKey()).metadata.read(this, meta);
		 */
		int sizeList;
		ArrayList<Vertex> list;
		Vertex vrchol;

		if (inc) {
			while (actual.key < limit.key) {
				list = pool.get(actual.key).getVertices();
				sizeList = list.size();
				while (actual.index < sizeList) {
					vrchol = list.get(actual.index);
					if (evaluateVertex(hladany, vrchol)) {
						// Nasli sme vrchol ktory splna podmienku
						return vrchol;
					}
					actual.index++;
				}
				actual.key++;
				actual.index = 0;
			}

			list = pool.get(limit.key).getVertices();
			while (actual.index < limit.index) {
				vrchol = list.get(actual.index);
				if (evaluateVertex(hladany, vrchol)) {
					// Nasli sme vrchol ktory splna podmienku
					return vrchol;
				}
				actual.index++;
			}

		} else {
			while (actual.key > limit.key) {
				list = pool.get(actual.key).getVertices();
				while (actual.index > -1) {
					vrchol = list.get(actual.index);
					if (evaluateVertex(hladany, vrchol)) {
						// Nasli sme vrchol ktory splna podmienku
						return vrchol;
					}
					actual.index--;
				}
				actual.key--;
				actual.index = pool.get(actual.key).getVertices().size() - 1;
			}

			list = pool.get(actual.key).getVertices();
			while (actual.index > limit.index) {
				vrchol = list.get(actual.index);
				if (evaluateVertex(hladany, vrchol)) {
					// Nasli sme vrchol ktory splna podmienku
					return vrchol;
				}
				actual.index--;
			}
		}

		// Nenajdene
		return null;
	}

	/**
	 * Zisti ci hladame tento vrchol.
	 * 
	 * @param hladany
	 * @param v
	 * @return
	 */
	protected boolean evaluateVertex(Pattern hladany, Vertex v) {
		return hladany.matcher(v.toString()).find();
	}

	/**
	 * Prikaz ktory poviem vyhladaj my dalsi vrchol...
	 * Ak dojde ku konci mnoziny, zacne hladat od zaciatku po dany bod.
	 * 
	 * @return false - nenajdeny
	 * @param txt
	 */
	public boolean next(Pattern txt) {
		if (!test(txt)) return true;
		Adresa actual = new Adresa();
		Adresa limit = new Adresa();
		Vertex vrchol;

		// Posun aktualny index na dalsiu cast
		last.index++;
		if (last.key == -1) last.key = 0;
		if (last.index >= pool.getGroups().get(last.key).getVertices().size()) {
			last.key++;
			if (last.key >= pool.getGroups().size()) {
				last.key = 0;
			}
			last.index = 0;
		}

		// Hladame od aktualneho umiestnenia do konca mnoziny
		limit.key = pool.getGroups().size() - 1;
		limit.index = pool.getGroups().get(limit.key).getVertices().size() - 1;
		actual.key = last.key;
		actual.index = last.index;
		vrchol = find(txt, actual, limit, true);
		if (vrchol != null) {
			successFind(vrchol, actual);
			return true;
		}
		// TODO: tieto 2 podmnoziny mnoziny su vzdy rozdielne a nezavisle
		// skvele paralizovatelne ! lockovanie nebude ziadne
		// Ak prvy proces nieco najde - zrusi proces 2 - inak prebiehaju rovnako
		// Teda ze prvok sa neneachadza v danej mnozine sa urci 2x rychlejsie !

		// Nevyslo hladanie - hladam od zaciatku mnoziny po dany bod
		limit.key = last.key;
		limit.index = actual.index;
		actual.key = 0;
		actual.index = 0;
		vrchol = find(txt, actual, limit, true);
		if (vrchol != null) {
			successFind(vrchol, actual);
			return true;
		}

		// Nenasiel som vobec nic
		notFound(txt);
		return false;
	}

	public boolean back(Pattern txt) {
		if (!test(txt)) return false;
		Adresa actual = new Adresa();
		Adresa limit = new Adresa();
		Vertex vrchol;

		// Posun aktualny index na dalsiu cast
		last.index--;
		if (last.index < 0) {
			last.key--;
			if (last.key < 0) {
				last.key = pool.getGroups().size() - 1;
			}
			last.index = pool.getGroups().get(last.key).getVertices().size() - 1;
		}

		// Hladame od aktualneho umiestnenia do zaciatku mnoziny
		limit.key = 0;
		limit.index = -1;
		actual.key = last.key;
		actual.index = last.index;
		vrchol = find(txt, actual, limit, false);
		if (vrchol != null) {
			successFind(vrchol, actual);
			return true;
		}

		// Nevyslo hladanie - hladam od konca mnoziny po dany bod
		limit.key = last.key;
		limit.index = last.index;
		actual.key = pool.getGroups().size() - 1;
		actual.index = pool.getGroups().get(actual.key).getVertices().size() - 1;
		vrchol = find(txt, actual, limit, false);
		if (vrchol != null) {
			successFind(vrchol, actual);
			return true;
		}

		// Nenasiel som vobec nic
		notFound(txt);
		return false;
	}

	/**
	 * Nasiel som vrchol...
	 * 
	 * @param v
	 */
	protected void successFind(Vertex v, Adresa actual) {
		logger.info(actual + " " + v);
		last = actual;
	}

	/**
	 * Takyto vrchol sa tam nenachadza
	 * 
	 * @param txt
	 */
	protected void notFound(Pattern txt) {}
}
