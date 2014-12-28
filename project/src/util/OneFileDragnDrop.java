package util;

import java.io.File;
import java.util.List;

/**
 * FileDragnDrop prerobeny len na podporu len jedneho suboru.
 * @author Lukas Sekerak
 *
 */
public abstract class OneFileDragnDrop extends FileDragnDrop
{

	/**
	 * Metoda sa spusti pri drag and drop.
	 * Riesi situaciu tak ze najde prvy subor ktory je mozne otvorit a otvori ho.
	 */
	@Override
	protected void catchFiles(
			@SuppressWarnings("rawtypes") List files) {
		File f;
		String cesta;
		for (int j = 0; j < files.size(); j++) {
			cesta = files.get(j).toString();
			f = new File(cesta);
			if( open(f) ) return;
		}

		// Ziadny subor sa nepodarilo otvorit...
		unsuccessful();
	}
	
	protected abstract boolean open(File f);
	protected abstract void unsuccessful();
}
