
package nio.mapping;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;

/**
 * Pametovo namapovany subor, ktory je rozdeleny do statickych casti.
 * Staticke casti maju rovnaku dlzku a vzdy konstantnu.
 * 
 * @author Lukas Sekerak
 */
public class StaticBlocksMappedFile extends MappedFile
{
	private static final long	serialVersionUID	= 4052054378431729920L;
	private final int			blokSize;									// kolko je v bloku

	/**
	 * Vytvor StaticBlocksMappedFile
	 * @param file
	 * @param block
	 * @throws IOException
	 */
	public StaticBlocksMappedFile(File file, int block) throws IOException {
		super(file);
		setMaxPercentualBufferCount(20);
		this.blokSize = block;
	}
	
	/**
	 * Alokuj tolko a tolko bytov.
	 * @param alokujpamete
	 */
	public void alloc(long alokujpamete) {
		long lBlock = (long) blokSize;
		alokujpamete = Math.max(alokujpamete, lBlock);

		for (long offset = 0; offset < alokujpamete; offset += lBlock) {
			add(offset, blokSize);
		}
	}
	
	/**
	 * Kolko bytov do konca aktualneho suboru nam ostava ?
	 * @param position
	 * @return
	 */
	public int getSizeToEnd(long position) {
		return blokSize - getNormalizedPosition(position);
	}
	
	/**
	 * Zober urcitu poziciu a posun ju na zaciatok toho bloku.
	 * @param position
	 * @return
	 */
	public int getNormalizedPosition(long position) {
		return (int) (position % getBlockSize());
	}
	protected int getIndex(long position) {
		return (int) (position / getBlockSize());
	}
	
	private long getStartPosition(int index) {
		return index * getBlockSize();
	}
	
	/**
	 * Kde zacina dalsi blok.
	 * @param position
	 * @return
	 */
	public long getNextStartPosition(long position) {
		return getStartPosition(getIndex(position) + 1);
	}
	
	/**
	 * Velkost statickeho bloku.
	 * @return
	 */
	public int getBlockSize() {
		return blokSize;
	}
	
	/**
	 * Pridaj dalsi blok.
	 */
	public void add() {
		add(getMaxPosition(), blokSize);
	}

	/**
	 * Posun N bytov do lava.
	 * 
	 * @param pos
	 * @param howmany
	 */
	public void paddingToLeft(long pos, int howmany) {
		byte[] memory = new byte[blokSize];
		int index, index2, okopirovane, normalizovanaPozicia;
		long poziciaBezMedzery;
		MappedByteBuffer buffer, bufferNasledujuci;

		while (true) {
			// bufferNasledujuci a buffer sa mozu rovnat ...
			poziciaBezMedzery = pos + howmany;
			index = getIndex(pos);
			index2 = getIndex(poziciaBezMedzery);
			normalizovanaPozicia = getNormalizedPosition(pos);

			// Sme v poslednom bufferi, velkost ktora je v tomto bufferi
			// je mala takze vymazeme buffer
			if (normalizovanaPozicia == 0 && index2 == size()) {
				super.deleteLast();
				return;
			}
			// Sme na konci buffera, dalsi buffer neexistuje
			if (index2 > size()) return;

			// Nacitaj Buffers
			buffer = super.getBuffer(index);
			bufferNasledujuci = super.getBuffer(index2);

			// Zober bufferNasledujuci okopiruj co sa da ...
			bufferNasledujuci.position(getNormalizedPosition(poziciaBezMedzery));
			okopirovane = getSizeToEnd(poziciaBezMedzery);
			bufferNasledujuci.get(memory, 0, okopirovane);

			// Stary bufer nastav na poziciu a okopiruj data
			buffer.position(normalizovanaPozicia);
			buffer.put(memory, 0, okopirovane);

			// Data sme prekopirovali teraz sa treba posunut na dalsiu poziciu
			pos += okopirovane;
		}
	}
	/**
	 * Do buffer uloz buffer ktory patri indexu a nastav aj poziciu ktora patri indexu.
	 * 
	 * @param buf
	 * @param index
	 * @return
	 */
	public MappedByteBuffer getByPosition(long position) {
		MappedByteBuffer buffer = super.getBuffer(getIndex(position));
		buffer.position(getNormalizedPosition(position));
		return buffer;
	}
}
