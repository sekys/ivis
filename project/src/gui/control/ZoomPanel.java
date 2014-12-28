
package gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;

/**
 * Panel +/- na zoomovanie po pracovnej ploche.
 * 
 * @author Lukas Sekerak
 */
public class ZoomPanel extends CrossoverScalingControl
{
	protected JButton			plus	= new JButton("+");
	protected JButton			minus	= new JButton("-");

	private final static Logger	logger	= Logger.getLogger(ZoomPanel.class.getName());

	public ZoomPanel(final VisualizationServer<?, ?> vv) {
		// V uvode si to priblizime
		// scale(vv, 11f, vv.getCenter());
		plus.setToolTipText("Po stlaèení sa plocha priblíži.");
		minus.setToolTipText("Po stlaèení sa plocha odïiali.");
		plus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				scale(vv, 1.1f, vv.getCenter());
			}
		});
		minus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});
		logger.info(null);
	}

	/**
	 * Zaregistruj tlacitka do okna.
	 * 
	 * @param panel
	 */
	public void registerButtons(JPanel panel) {
		panel.add(plus);
		panel.add(minus);
	}
}
