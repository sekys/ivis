
package gui.control;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.log4j.Logger;
import render.ISearchText;
import util.FileUtil;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.VerticesPool;
import graph.objects.Vertex;

/**
 * Vyhladavaci panel v dolnom toolbare.
 * 
 * @author Lukas Sekerak
 */
public class FilterPanel extends JPanel
{
	private static final long						serialVersionUID	= -2855861227238298986L;

	protected ISearchText							filter;
	protected VisualizationViewer<Vertex, Integer>	vv;
	protected SearchTool							search;

	protected JTextField							textarea;
	protected JLabel								filterButton;
	// protected JLabel zeleneButton;
	protected JLabel								nextButton;
	protected JLabel								backButton;

	private final static Logger						logger				= Logger.getLogger(FilterPanel.class.getName());

	public FilterPanel(VisualizationViewer<Vertex, Integer> vv,
			VerticesPool pool,
			ISearchText filter,
			SearchTool search) {
		super();
		this.vv = vv;
		this.filter = filter;
		this.search = search;
		textarea = new JTextField(10);
		setToolTipText("Zadaj text pod¾a ktorého sa vyfiltrujú vrcholy.");

		filterButton = new JLabel(new ImageIcon(FileUtil.IMAGES + "detail.gif", "Hladaj"));
		filterButton.setToolTipText("Zadaj text pod¾a ktorého sa vyfiltrujú vrcholy.");
		filterButton.setCursor(Cursor.getDefaultCursor());

		// zeleneButton = new JLabel("G");
		// zeleneButton.setToolTipText("Zadaj text pod¾a ktorého sa vyfiltrujú vrcholy.");
		// zeleneButton.setCursor(Cursor.getDefaultCursor());

		nextButton = new JLabel(">>");
		nextButton.setToolTipText("Zadaj text pod¾a ktorého sa vyfiltrujú vrcholy.");
		nextButton.setCursor(Cursor.getDefaultCursor());

		backButton = new JLabel("<<");
		backButton.setToolTipText("Zadaj text pod¾a ktorého sa vyfiltrujú vrcholy.");
		backButton.setCursor(Cursor.getDefaultCursor());

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(backButton);
		add(textarea);
		textarea.setLayout(new BorderLayout());
		textarea.add(filterButton, BorderLayout.LINE_END);
		add(nextButton);

		setCallbacks();
		logger.info(null);
	}

	private abstract class MyMouseListener implements MouseListener
	{
		public void mouseReleased(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}

		public void mouseClicked(MouseEvent arg0) {
			String txt;
			try {
				txt = textarea.getText();
				logger.info(txt);
				if (txt == null || txt.length() == 0) {
					setText(null);
				} else {
					setText(Pattern.compile(txt));
				}
			}
			catch (PatternSyntaxException exc) {
				JOptionPane.showMessageDialog(vv, "Zadany text nie je regularny vyraz.", "Hladaj", 0);;
			}
			catch (Exception e2) {
				logger.error(e2, e2);
			}
		}
		protected abstract void setText(Pattern txt);
	}

	private void setCallbacks() {
		// Filtrovanie
		filterButton.addMouseListener(new MyMouseListener()
		{
			protected void setText(Pattern vzor) {
				filter.setHladanyretazec(vzor);
				vv.repaint();
			}
		});

		// Hladanie vpred
		nextButton.addMouseListener(new MyMouseListener()
		{
			protected void setText(Pattern txt) {
				search.next(txt);
			}
		});

		// Hladanie vzad
		backButton.addMouseListener(new MyMouseListener()
		{
			protected void setText(Pattern txt) {
				search.back(txt);
			}
		});
	}
}
