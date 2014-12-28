
package nio.base;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import nio.unmapping.DeleteAll;
import nio.unmapping.IUnMappingAlgoritm;

/**
 * Trieda, ktora vytvara buffre a hned ich zaregistruje.
 * Tzv mame pristup k vsetkym buffrom.
 * 
 * @author Lukas Sekerak
 */
public class BufferAutoRegisteringFactory implements IBufferFactory
{
	private List<Buffer>	allocated;

	public BufferAutoRegisteringFactory() {
		allocated = new LinkedList<Buffer>();
	}

	public List<Buffer> getAllocated() {
		return allocated;
	}

	private class CatchingBuffer extends Buffer
	{
		public CatchingBuffer(RandomAccessFile raf, long pos, int size) {
			super(raf, pos, size);
		}

		protected void map() throws IOException { // musi byt nejako zabezspecene
			super.map();
			allocated.add(this);
		}
		public void unmap() {
			super.unmap();
			allocated.remove(this);
		}
	}

	/**
	 * Vytvor buffer cez tovaren.
	 */
	public Buffer create(RandomAccessFile raf, long pos, int size) {
		// allbuffers.add(buffer);
		return new CatchingBuffer(raf, pos, size);
	}

	/**
	 * Defaultny odmapujuci algoritmus.
	 */
	public IUnMappingAlgoritm<Buffer> getDefaultUnmapping() {
		return new DeleteAll();
	}

	/**
	 * Odmapuj buffre.
	 */
	public boolean unmap(IUnMappingAlgoritm<Buffer> unmap) {
		return unmap.UnMappingAlgoritm(allocated);
	}
}
