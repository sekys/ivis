
package io;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.swing.ProgressMonitor;

/**
 * ProgressMonitorReader
 * 
 * @author Lukas Sekerak
 * 
 *         Progress Monitor ktory spolupracuje s BufferedReader, je to nahrada
 *         za ProgressMonitorInputStream ktory Reader nepodporuje. Meria aktualny
 *         proces spracovania suboru.
 * 
 *         ProgressMonitor berie INT my vsak mame data ako long. Preto sa spravi
 *         zobrazenie z [0, dlzkasuboru] na hodnotu [0, MAX]
 */
public class ProgressMonitorReader extends BufferedReader
{
	private ProgressMonitor		monitor;
	private final static int	MAX	= 100;	// Percentualna podrobnost

	/**
	 * Nacitavanie suboru bolo zatvorene pouzivatelom.
	 * 
	 * @author Lukas Sekerak
	 */
	public class CancelException extends RuntimeException
	{
		private static final long	serialVersionUID	= 208725614477266073L;
	}

	/**
	 * Velkost suboru v bytoch
	 */
	private long	size;

	/**
	 * Aktualna pozicia v subore a aktualna na intervale
	 */
	private long	flocation	= 0;
	private int		oldpozicia	= 0;
	private String	msg;

	/**
	 * Vytvor monitor nad nacitanim suboru.
	 * 
	 * @param parent
	 * @param msg
	 * @param reader
	 * @param fs
	 */
	public ProgressMonitorReader(Component parent,
			String message,
			Reader reader,
			long fs) {
		super(reader);
		msg = message;
		this.size = fs;
		monitor = new ProgressMonitor(parent, null, msg, 0, MAX);
	}

	/**
	 * Vytvor monitor nad nacitanim suboru.
	 * 
	 * @param parent
	 * @param msg
	 * @param file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public ProgressMonitorReader(Component parent, Object msg, File file) throws FileNotFoundException,
			UnsupportedEncodingException {
		this(parent, "Reading " + file.getName(), new InputStreamReader(new FileInputStream(file), "UTF-8"),
		// new FileReader(file), Nepodporuje UTF8
		file.length() + 1);
	}

	/**
	 * Getter pre monitor
	 * 
	 * @return monitor
	 */
	public ProgressMonitor getProgressMonitor() {
		return monitor;
	}

	/**
	 * Zatvor stream aj okno monitora.
	 */
	public void close() throws IOException {
		super.close();
		monitor.close();
	}

	/**
	 * Spocitaj aktualne precitane byty z celkovych.
	 */
	protected void updateSatus() {
		// Citam po 100000 bytoch, nechcem aby sa monitor pretazovalo...
		if (oldpozicia > 100000) {
			if (monitor.isCanceled()) throw new CancelException();

			flocation += oldpozicia;
			float pomer = ((float) (flocation * MAX)) / ((float) size);
			monitor.setProgress((int) pomer);
			monitor.setNote(msg + String.format(" %.2f%%", pomer));
			oldpozicia = 0;
		}
	}

	@Override
	/**
	 * Zavola sa pri precitani dalsich bytov, tie potom pocitame.
	 */
	public int read(char[] cbuf, int off, int len) throws IOException {
		int precital = super.read(cbuf, off, len);
		oldpozicia += precital;
		updateSatus();
		return precital;
	}
}
