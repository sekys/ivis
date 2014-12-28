
package nio.mapping;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;

/**
 * Pametovo namapovany arraylist pre jeden objekt, ktory ma
 * konstantnu velkost.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
public abstract class SimpleArrayList<T> implements List<T>
{
	private int							size;			// celkovy pocet prvkov ktore mame
	protected StaticBlocksMappedFile	map;
	private final int					elementSize;

	public int getElementSize() {
		return elementSize;
	}
	/**
	 * Vytvori ArrayList ktory sa alokuje po blokoch, udaje sa ukladaju cez NIO
	 * do suboru zadanom vo file. Elementy v ttejto array musia mat pevnu dlzku.
	 * 
	 * 
	 * @param file Nazov suboru s ktorym sa bude pracovat.
	 * @param elementsize Velkost elementu v bytoch.
	 * @param DelimPoPrvkov Velkost bloku.
	 * @param InitialSize Nacitaj dopredu alokacne bloky.
	 * @throws IOException
	 */
	public SimpleArrayList(File file, int elementsize, int DelimPoPrvkov) throws IOException {
		elementSize = elementsize;
		map = new StaticBlocksMappedFile(file, elementSize * DelimPoPrvkov);
	}

	/**
	 * Ma sa volat hned po konstruktori, inak nie
	 */
	protected void Initialize(int InitialSize) throws IOException {
		map.alloc(elementSize * InitialSize);
		size = 0;
	}

	/**
	 * Fyzicka velkost...
	 */
	protected int fsize() {
		return (map.getBlockSize() / elementSize) * map.size();
	}

	public int size() {
		return size;
	}

	public void add() {
		if (size == fsize()) {
			map.add();
		}
		size++;
	}
	public void delete(int index) {
		map.paddingToLeft(getElementSize() * index, getElementSize());
		size--;
	}

	protected MappedByteBuffer getBuffer(int index) {
		// public double get(int x, int y) {
		if (index < 0 && index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return map.getByPosition(index * elementSize);
	}
}
