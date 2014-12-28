
package util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Trieda sa stara o vytvorenie skupiny vlakien. Tieto vlakna spracuvava.
 * 
 * @author Lukas Sekerak
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor
{

	/**
	 * Vytvor skupiny vlakien - robotnikov.
	 * 
	 * @param CPUScaleFactor
	 * @param BufferCapacity
	 * @param scale
	 */
	public MyThreadPoolExecutor(int CPUScaleFactor,
			int BufferCapacity,
			boolean scale) {
		super(scale ? (MaxThreads() * CPUScaleFactor) : CPUScaleFactor, // core thread pool // size
		scale ? (MaxThreads() * CPUScaleFactor) : CPUScaleFactor, // maximum thread pool size
		1, // time to wait before resizing pool
		TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(BufferCapacity, false), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * Vytvor skupiny vlakien - robotnikov.
	 * 
	 * @param CPUScaleFactor
	 * @param BufferCapacity
	 */
	public MyThreadPoolExecutor(int CPUScaleFactor, int BufferCapacity) {
		this(CPUScaleFactor, BufferCapacity, true);
	}

	/**
	 * Kolko mame threadov na CPU ?
	 * 
	 * @return
	 */
	protected static int MaxThreads() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * Pomocna metoda pre ukocenie skupiny vlakien.
	 * 
	 * @return
	 */
	public boolean waitForAll() {
		shutdown(); // Cakaj kym vsetci skoncia
		try {
			// Casovy interval do konca
			if (!awaitTermination(5, TimeUnit.MINUTES)) {
				return false;
			}
		}
		catch (InterruptedException ex) {
			shutdownNow();
			throw new RuntimeException("Vlakno sa zaseklo.");
		}
		return true;
	}
}
