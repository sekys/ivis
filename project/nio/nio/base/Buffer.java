
package nio.base;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Trieda predtavujuca Buffer pouzity pre NIO mapovanie,
 * uchovava dolezite informacie k MappedByteBuffer. Viac menej ho len
 * zabaluje.
 * 
 * @author Lukas Sekerak
 */
public class Buffer
{
	/**
	 * @uml.property name="buffer"
	 */
	private MappedByteBuffer		buffer;
	/**
	 * @uml.property name="position"
	 */
	private final long				position;
	/**
	 * @uml.property name="length"
	 */
	private final int				length;
	/**
	 * @uml.property name="raf"
	 */
	private final RandomAccessFile	raf;

	public Buffer(RandomAccessFile raf, long pos, int size) {
		this.raf = raf;
		position = pos;
		length = size;
		buffer = null;
	}
	/**
	 * @return
	 * @throws IOException
	 * @uml.property name="buffer"
	 */
	public MappedByteBuffer getBuffer() throws IOException { // musi byt nejako zabezspecene
		if (buffer == null) {
			// Buffer sme odlozili / vymazali obnov ho
			map();
		}
		return buffer;
	}

	protected void map() throws IOException {
		buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, position, length);
	}

	/**
	 * Odmapuj buffer.
	 */
	@SuppressWarnings("restriction")
	public void unmap() {
		if (buffer == null) return;
		if (buffer instanceof sun.nio.ch.DirectBuffer) {
			sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
			cleaner.clean();
			buffer = null;
			TestUnmap();
		}
	}

	protected void finalize() throws Throwable {
		try {
			unmap();
		}
		finally {
			super.finalize();
		}
	}
	/**
	 * Je buffer alokovany ?
	 * @return
	 */
	public boolean isLoaded() {
		return buffer != null;
	}
	
	/**
	 * Velkost bufferu.
	 * @return
	 */
	public long size() {
		return position + length;
	}
	
	/**
	 * Kapacita bufferu.
	 * @return
	 */
	public int capacity() {
		return length;
	}
	
	private void TestUnmap() {
		// Otestuj ci sa buffer odmapoval ..
		FileLock fl = null;
		for (int i = 0; i < 10; i++) {
			try {
				fl = raf.getChannel().tryLock(position, 1, false);
				try {
					fl.release();
				}
				catch (IOException e2) {
					// Nedokazal som uvolnit lock
					e2.printStackTrace();
				}
				return;
			}
			catch (IOException e) { // Neodmapoval ..
				System.out.println("Unmapping " + i);
				e.printStackTrace();
				System.gc();
				System.runFinalization();
				try {
					Thread.sleep(10);
				}
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		throw new RuntimeException("Nedokazal som odmapovat sektor pamete po 10 pokusoch, vzdavam sa.");
	}
}
