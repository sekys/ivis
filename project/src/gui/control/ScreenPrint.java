
package gui.control;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * Tlacitko P na sfotenei aktualnej pracovnej plochy.
 * 
 * @author Lukas Sekerak
 */
public class ScreenPrint extends JButton implements ActionListener
{
	private static final long			serialVersionUID	= -3615024686669540434L;
	private final static Logger			logger				= Logger.getLogger(ScreenPrint.class.getName());

	protected VisualizationViewer<?, ?>	vv;

	public ScreenPrint(VisualizationViewer<?, ?> vv) {
		super("P");
		this.setToolTipText("Po stlaËenÌ sa sfotÌ aktu·lna obrozovka.");
		this.vv = vv;
		addActionListener(this);
		logger.info(null);
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				makeScreenShoot(chooser.getSelectedFile());
			}
			catch (IOException e1) {
				JOptionPane.showMessageDialog(vv, "Obr·zok sa nepodarilo uloûiù.");
			}
		}
	}

	protected void makeScreenShoot(File selected) throws IOException {
		File file = new File(selected + ".jpeg");
		BufferedImage image = new BufferedImage(vv.getWidth(), vv.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D gpaint = image.createGraphics();
		vv.paint(gpaint);
		gpaint.dispose();
		ImageIO.write(image, "jpeg", file);
	}
}
