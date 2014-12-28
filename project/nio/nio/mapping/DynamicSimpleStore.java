
package nio.mapping;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import nio.base.IBufferFactory;
import nio.base.IDNio;
import nio.base.IPosInfo;

/**
 * DynamicArrayList predstavuje ArrayList ktory data namapuje do virtualnej pamete,
 * za pomoci nio package. Obsahuje len homogenne prvky, ktory velkost moze byt dynamicka.
 * 
 * Automaticke pridavanie prvkov, zvysovanie kapacity.
 * Subory uklada do jeden zlozky, kde vytvori subor pre index, data, metda udaje.
 * 
 * @author Lukas Sekerak
 */
public class DynamicSimpleStore<T extends IDNio>
{
	private static final long		serialVersionUID	= 7612448938912682942L;
	private StaticBlocksMappedFile	map;										// Mapa ktora mapuje
																				// udaje
	private long					fullposition;								// Pozicia v subore
																				// kde koncia
																				// vsetke udaje
	/**
	 * Metoda pre pripravu DynamicSimpleStore
	 * 
	 * @param controller
	 * @throws IOException
	 */
	public void Initialize(File mapfile, IBufferFactory factory, int blok)
			throws IOException {
		map = new StaticBlocksMappedFile(mapfile, blok);
		map.setFactory(factory);
		map.alloc(blok);
		fullposition = 1; // Zacina az prvym bytom
	}
	public synchronized void read(IPosInfo posinfo, T value) {
		value.nioRead(map.getByPosition(posinfo.getPos()));
	}
	public synchronized void write(IPosInfo posinfo, T value) {
		findPosition(posinfo, value.nioSize());
		MappedByteBuffer buffer = map.getByPosition(posinfo.getPos());
		value.nioWrite(buffer);
	}
	/**
	 * Ziadna pamet sa nenasla, je potrebne alokovat dalsie miesto.
	 * Skontroluj ci mame rezervovanu pamet, ak nie rezervuj novu.
	 */
	private boolean allocNewMemory(IPosInfo info) {
		// Skontroluj ci sa zmesti do bloku
		if (map.getBlockSize() < info.getSize()) {
			throw new RuntimeException("Blok pamete je velmy maly.");
		}

		int sizetoend = map.getSizeToEnd(fullposition);

		// Volna velkost v tomto bloku do konca bloku je velmy mala
		if (sizetoend < info.getSize()) {
			if (sizetoend > 0) { // Ak je aspon minimalna ...
				// Tak vytvor volny blok
				// PosInfo freeblock = db.new PosInfo(fullposition, sizetoend);
				// TODO: zaregistruj volny block
			}

			// Vypocitaj novu poziciu, na zaciatok dalsieho bloku...
			fullposition = map.getNextStartPosition(fullposition);
		}

		// Je potrebne zarezervovat volnu pamet ?
		if (fullposition >= map.getMaxPosition()) {
			// Zarezervuj novu pamet
			map.add();
		}

		info.setPos(fullposition);
		fullposition += info.getSize();
		return true;
	}

	private void findPosition(IPosInfo info, int need) {
		// Ide len o update udajov
		if(need == -1) return;
		info.setSize(need);
		if (allocNewMemory(info)) return;
		throw new OutOfMemoryError();
	}
}
