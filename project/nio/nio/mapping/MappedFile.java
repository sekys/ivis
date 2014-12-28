
package nio.mapping;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import nio.base.Buffer;

/**
 * Vsetky buffre su spojene dokopy a vytvaraju tak pametovo namapovany subor.
 * 
 * @author Lukas Sekerak
 */
public class MappedFile extends BufferController
{
	private ArrayList<Buffer>		buffers;
	private final RandomAccessFile	raf;
	private long					MaxPosition;

	/**
	 * Vytvor pam. namapovany subor.
	 * @param file
	 * @throws IOException
	 */
	public MappedFile(File file) throws IOException {
		raf = new RandomAccessFile(file, "rw");
		buffers = new ArrayList<Buffer>(10);
		MaxPosition = 0;
	}
	
	protected void add(long pos, int size) {
		buffers.add(create(raf, pos, size));
		MaxPosition = Math.max(MaxPosition, pos + size);
	}
	
	protected void deleteLast() {
		Buffer buffer = buffers.remove(size() - 1);
		buffer.unmap();
		MaxPosition = MaxPosition - buffer.size();
		try {
			raf.getChannel().truncate(MaxPosition);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Zapis vsetko do suboru, ak sa to hned nezapisalo.
	 * @throws IOException
	 */
	public void flush() throws IOException {
		raf.getChannel().force(false);
	}
	
	/**
	 * Zatvor pracu so suborom.
	 */
	public void close() {
		try {
			raf.close();
		}
		catch (IOException e1) {
			throw new RuntimeException("Nepodarilo sa zatvorit subor.");
		}
	}
	
	protected void finalize() throws Throwable {
		try {
			close();
		}
		finally {
			super.finalize();
		}
	}
	
	public MappedByteBuffer getBuffer(int n) {
		return convert(buffers.get(n));
	}
	
	public int size() {
		return buffers.size();
	}
	
	public long getMaxPosition() {
		return MaxPosition;
	}
	
	public void setMaxPercentualBufferCount(int percent) {
		if (percent < 1 || percent > 100) {
			throw new RuntimeException("setMaxPercentualBufferCount ma byt v [1, 100]");
		}
		maxBuffers = percent * -1;
	}
	
	protected boolean checkMaxCount() {
		if (maxBuffers < 0) {
			int maxcount = (int) ((float) (buffers.size() * maxBuffers * -1) / 100.f);
			return (getFactory().getAllocated().size() > maxcount);
		}
		return super.checkMaxCount();
	}
}
