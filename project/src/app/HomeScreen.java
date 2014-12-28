
package app;

import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.apache.log4j.Logger;
import util.FileUtil;
import util.OneFileDragnDrop;

/**
 * Uvodna obrazovka.
 * 
 * @author Lukas Sekerak
 */
public abstract class HomeScreen
{
	protected JFrame			frame;
	protected JMenuBar			menu;
	private JLabel				bgImage;
	private final static Logger	logger	= Logger.getLogger(HomeScreen.class.getName());

	public HomeScreen() {
		logger.info(null);
		frame = new JFrame("InteraktÌvna vizualiz·cia informaËnej siete");
		menu = new JMenuBar();
		frame.setJMenuBar(menu);

		buildWindow();
		buildMenu();
		customizeScreen();

		frame.setVisible(true);
	}

	private void buildWindow() {
		logger.info(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});
	}

	protected abstract void openFile(File file) throws Exception;
	protected abstract void successOpen(File file);

	private void buildFileMenu() {
		logger.info("start");

		// Nech vyberie zlozku alebo subor
		JFileChooser chooser = new JFileChooser(new File("."));
		int r = chooser.showOpenDialog(frame);
		if (r != JFileChooser.APPROVE_OPTION) return;
		File file = chooser.getSelectedFile();

		try {
			openFile(file);
		}
		catch (Exception e) {
			// Spracuj chybu a informuj cez popup
			String msg = e.getMessage();
			logger.info(msg);
			if (msg != null) {
				JOptionPane.showMessageDialog(frame, msg, "Spracuvavam udaje", 0);
			}
			return;
		}
		
		successOpen(file);
	}

	private void customizeScreen() {
		logger.info("");
		
		// Vytvor pozadie
		bgImage = new JLabel(new ImageIcon(FileUtil.IMAGES + "homebg.jpg"));
		frame.getContentPane().add(bgImage);
		new DropTarget(bgImage, new OneFileDragnDrop()
		{
			@Override
			public boolean open(File f) {
				// Toto je callback ktory sa vol pre kazdy jeden subor
				// exception sa preposiela dalej...
				logger.info("DragNDrop openFile");
				try {
					openFile(f);
					successOpen(f);
				}
				catch (Exception e) {
					// Ignoruj a pokracuj na dalsi subor
					return false;
				}
				return true;
			}

			@Override
			protected void unsuccessful() {
				// Callback pri neuspesnom otvori vsetkich suborov
				logger.info(null);
				JOptionPane.showMessageDialog(bgImage, "Ziadny zo suborov nieje kompatibilny.", "Otvor", 0);
			}
		});
	}

	private void buildMenu() {
		logger.info(null);

		// Vytvor menu
		JMenu otvorit = new JMenu("Otvoriù s˙bor");
		JMenu about = new JMenu("O programe");
		menu.add(otvorit);
		menu.add(Box.createHorizontalGlue());
		menu.add(about);

		// Vytvor eventy
		otvorit.addMenuListener(new MenuListener()
		{
			public void menuCanceled(MenuEvent e) {}
			public void menuDeselected(MenuEvent e) {}
			public void menuSelected(MenuEvent e) {
				Runnable akcia = new Runnable()
				{
					public void run() {
						buildFileMenu();
					}
				};
				Thread.UncaughtExceptionHandler exh = new Thread.UncaughtExceptionHandler()
				{
					public void uncaughtException(Thread th, Throwable ex) {
						logger.error(ex, ex);
						ex.printStackTrace();
					}
				};

				Thread vlakno = new Thread(akcia, "Otvor projekt");
				vlakno.setUncaughtExceptionHandler(exh);
				vlakno.start();
			}
		});
		about.addMenuListener(new MenuListener()
		{
			public void menuCanceled(MenuEvent e) {}
			public void menuDeselected(MenuEvent e) {}

			public void menuSelected(MenuEvent e) {
				logger.info("about");
				final String credits = "<html><h3>InteraktÌvna vizualiz·cia informaËnej siete</h3>Program v r·mci bakal·rskej pr·ce.<br>Autor: Luk·ö Seker·k</html>";
				JOptionPane.showMessageDialog(bgImage, credits, "O programe", 1);
			}
		});
	}

}
