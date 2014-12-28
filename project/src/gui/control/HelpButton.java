
package gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import util.FileUtil;

/**
 * Trieda pre tlacitko Help v dolnom toolbare.
 * 
 * @author Lukas Sekerak
 */
public class HelpButton extends JButton implements ActionListener
{
	private static final long	serialVersionUID	= -7449288318282605150L;
	private final static Logger	logger				= Logger.getLogger(HelpButton.class.getName());

	private final String		helptext;

	public HelpButton() {
		super("?");
		this.setToolTipText("Po stlaèení sa zobrazí krátky návod k aplikácii.");

		try {
			helptext = FileUtil.loadFile("help.html");
		}
		catch (FileNotFoundException e) {
			logger.error(e, e);
			throw new RuntimeException("Pomocny Subor help.html nenajdeny.");
		}
		addActionListener(this);
		logger.info(null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JOptionPane.showMessageDialog(null, helptext);
	}

}
