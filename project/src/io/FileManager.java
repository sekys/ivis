
package io;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import edu.uci.ics.jung.graph.DirectedGraph;
import graph.GraphHolder;
import graph.DirectedCorpusGraph;
import graph.objects.Vertex;

/**
 * Manager pre riadenie otvorenia a spracovania suboru.
 * 
 * @author Lukas Sekerak
 */
public class FileManager
{
	/**
	 * Ktore koncovky suborov su programom podporovane
	 */
	private static final String[]			SUPPORTED_EXTENSIONS	= {"graph",
			"graphml"												};
	/**
	 * Rodicovsky dialog
	 */
	private Component						parent;

	/**
	 * Typ suboru ktory je otvoreny 0, 1, 2 vychadza z SUPPORT_EXTENSIONS
	 */
	private int								type;

	/**
	 * Aktualne otvoreny graf
	 */
	private DirectedGraph<Vertex, Integer>	graph;

	/**
	 * Vrat graf vytvoreny zo suboru.
	 * 
	 * @return
	 */
	public DirectedGraph<Vertex, Integer> getGraph() {
		return graph;
	}

	public FileManager(Component com) {
		parent = com;
	}

	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void open(File file) throws Exception {
		// Skontroluj subor ci nam vyhovuje
		if (!file.isFile()) throw new Exception("Subor neexistuje.");
		if (!file.canRead()) throw new Exception("Subor nemozem otvorit.");

		// Pomocou citacky nacitacij subor
		final IReadable citacka = getFileType(file.getName());
		final Reader buffer;
		try {
			buffer = new ProgressMonitorReader(parent, "Loading", file);
			parent.repaint();
		}
		catch (UnsupportedEncodingException e1) {
			throw new Exception("Subor nie je vo formate UTF8.");
		}
		catch (FileNotFoundException e1) {
			throw new Exception("Subor neexistuje.");
		}

		File helpdirectory = new File(file.getParent() + "/" + file.getName()
				+ "_ivis/");
		GraphHolder build = new GraphHolder(helpdirectory);
		graph = new DirectedCorpusGraph(build);

		// SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
		// protected Void doInBackground() throws Exception {
		try {
			citacka.fileload(buffer, graph);
		}
		catch (OutOfMemoryError e1) {
			e1.printStackTrace();
			graph = null;
			System.gc();
			// Vymaz vsetko z grafu, uvolni pamet
			throw new Exception("Nedostatok pamete pre spracovanie suboru.");
		}
		catch (ProgressMonitorReader.CancelException e2) {
			// TODO: Toto potom zakomentovat
			e2.printStackTrace();
			graph = null;
			System.gc();
			throw new Exception();
		}
		catch (Exception e3) {
			graph = null;
			e3.printStackTrace();
			System.gc();
			throw new Exception("Doslo k chybe pri citani suboru.");
		}
		// return null;
		// }
		// };
		// worker.execute();
	}

	/**
	 * Skontrolujeme ci tento typ suboru podporujeme a vratime interface pre
	 * citanie. Inak vrat null, ked subor nieje podporovany.
	 * 
	 * @return
	 * @throws Exception
	 */
	private IReadable getFileType(String name) throws Exception {
		if (name.endsWith(SUPPORTED_EXTENSIONS[0])) {
			type = 0;
			return new Reader_Graph(null, null);
		} else if (name.endsWith(SUPPORTED_EXTENSIONS[1])) {
			try {
				type = 1;
				return new Reader_GraphML(null, null);
			}
			catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			catch (SAXException e) {
				e.printStackTrace();
			}
		} /*else if (name.endsWith(SUPPORTED_EXTENSIONS[2])) {
			type = 2;
			return new Reader_NET(null);
		}*/
		throw new Exception("Tento format nieje podporovany.");
	}

	/**
	 * @return typ spracovaneho suboru
	 */
	public int getType() {
		return type;
	}
}
