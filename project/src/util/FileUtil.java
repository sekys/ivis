
package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * Trieda dava dokopy funkcie pre subory.
 * Zadavaju sa tu hlavne cesty pre media subory.
 * 
 * @author Lukas Sekerak
 */
public class FileUtil
{
	/**
	 * Aktualna cesta spusteneho programu.
	 */
	public static final String		ROOT		= System.getProperty("user.dir")
														+ "/media/";

	/**
	 * Cesta k zlozke s media subormy.
	 */
	public static final String		IMAGES		= ROOT + "image/";

	/**
	 * Pomocka pre vypis velkosty.
	 */
	public static final String[]	Jednotky	= new String[]{"B", "KB", "MB",
			"GB", "TB"							};

	/**
	 * Metoda nacita cely subor a vrati obsah ako retazec.
	 * 
	 * @param pathname
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String loadFile(String pathname) throws FileNotFoundException {
		File file = new File(ROOT + pathname);
		Scanner scanner = new Scanner(file);
		StringBuilder fileContents = new StringBuilder((int) file.length());
		try {
			while (scanner.hasNextLine())
				fileContents.append(scanner.nextLine());
			return fileContents.toString();
		}
		finally {
			scanner.close();
		}
	}

	/**
	 * Zmeraj velkost suboru, vrati retazec.
	 * 
	 * @param file
	 * @return
	 */
	public static String fileSizeFormat(File file) {
		long velkost = file.length();
		if (velkost < 1) return new String("0");
		int sustava = (int) (Math.log10(velkost) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(velkost
				/ Math.pow(1024, sustava))
				+ " " + Jednotky[sustava];
	}
}
