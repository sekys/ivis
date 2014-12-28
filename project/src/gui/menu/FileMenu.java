
package gui.menu;

import graph.DirectedCorpusGraph;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import util.FileUtil;
import app.App;

/**
 * FileMenu vytvara menu v hornej casti obrazovky.
 * Obsahuje vsetky informaice o otvorenom subore.
 * 
 * @author Lukas Sekerak
 */
public class FileMenu extends JMenu
{
	private static final long	serialVersionUID	= 38660478867557919L;
	protected JMenuItem			zatvorit;

	public FileMenu(File file, DirectedCorpusGraph graph) {
		// Nazov menu
		super("O s˙bore");
		this.setToolTipText("Inform·cie o s˙bore.");

		// Priprav informacie o subore ktore sa nachadzau o Subore
		add("Vrcholov: " + graph.getVertexCount());
		add("Hran: " + graph.getEdgeCount());
		add("Skupin: " + graph.getHolder().getVertices().getGroups().size());
		add("Velkost: " + FileUtil.fileSizeFormat(file));

		// Pridaj ukoncenie suboru
		addSeparator();
		zatvorit = new JMenuItem("Zatvoriù");
		add(zatvorit);

		zatvorit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				App.getInstance().Zatvorit();
			}
		});
	}
}
