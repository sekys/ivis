
package nio.mapping;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import nio.base.Buffer;
import nio.base.IBufferFactory;
import nio.unmapping.IUnMappingAlgoritm;

/**
 * Trieda kontroluje vytvaranie MappedByteBuffer z Buffer, ich alokovany
 * pocet, velkost, zaroven stare MappedByteBuffer buffre maze.
 * 
 * @author Lukas Sekerak
 * 
 */
public class BufferController
{
	private IUnMappingAlgoritm<Buffer>	unmapalg;
	private IBufferFactory				factory;

	// Kladne je konstatny max pocet
	// Zaporne je percento vyjadrujuce max pocet bufferov
	// 0 je limit vypnuty
	protected int						maxBuffers	= 0;

	/**
	 * Konstruktor vhodny pre globalny controller, kde kazdy sbor ma vlastnu factory
	 */
	public BufferController() {}
	public BufferController(IBufferFactory fac) {
		setFactory(fac);
	}
	public BufferController(IBufferFactory fac,
			IUnMappingAlgoritm<Buffer> unmapping) {
		factory = fac;
		unmapalg = unmapping;
	}
	public IBufferFactory getFactory() {
		return factory;
	}
	public void setFactory(IBufferFactory factory) {
		this.factory = factory;
		unmapalg = factory.getDefaultUnmapping();
	}
	
	public void setUnmapAlgoritm(IUnMappingAlgoritm<Buffer> unmap) {
		this.unmapalg = unmap;
	}
	
	public int getMaxBufferCount() {
		return Math.abs(maxBuffers);
	}
	
	public void disambleMaxBufferCount() {
		this.maxBuffers = 0;
	}
	
	public void setMaxBufferCount(int maxBuffers) {
		if (maxBuffers < 1) {
			throw new RuntimeException("setMaxBuffers ma byt vacsie ako 1");
		}
		this.maxBuffers = maxBuffers;
	}
	/**
	 * Metoda sa spusta pri kazdom konvertovani Buffer na MappedByteBuffer
	 * cize pri kazdom pouziti.
	 * 
	 * @param mybuffer
	 * @return
	 */
	public MappedByteBuffer convert(Buffer mybuffer) {
		if (mybuffer.isLoaded()) {
			try {
				return mybuffer.getBuffer();
			}
			catch (IOException e) {}
		}

		// Buffer sa ide alokovat s exception ze je nedostatok pamete alebo bez
		try {
			if (checkMaxCount()) factory.unmap(unmapalg);
			return mybuffer.getBuffer();
		}
		catch (IOException e) {
			// Buffer musime alokovat - nedostatok pamete
			return alloc(mybuffer);
		}
	}

	protected boolean checkMaxCount() {
		return (maxBuffers > 0 && maxBuffers >= factory.getAllocated().size());
	}

	/**
	 * Buffer musime alokovat
	 *
	 */
	private synchronized MappedByteBuffer alloc(Buffer mybuffer) {
		// Skus vyriesit problem - Tu sa moze pouzit rozny algoritmus
		if (!factory.unmap(unmapalg)) {
			throw new RuntimeException("Nepodarilo sa uvolnit ziadny buffer.");
		}
		// Uvolnil sa nejaky buffer, skus povodny zase vytvorit...
		try {
			return mybuffer.getBuffer();
		}
		catch (IOException e2) {
			throw new RuntimeException("Nedostatok virtualne pamete.", e2);
		}
	}

	public synchronized Buffer create(RandomAccessFile raf, long pos, int size) {
		Buffer novy = factory.create(raf, pos, size);
		// Ak vytvorim novy objekt , je vzdy potrebne ihned vytvorit oblast cez map
		// medzi tymy oblastami nemoze dojst k unmapovaniu, je to kriticka oblast
		// Preto synchronized
		convert(novy);
		return novy;
	}
	// Pri unmapingu zorad podla:
	// Podla velkosti bloku - nema zmysel pri statickych
	// Podla poslednych pouziti
	// Podla poslednych vytvorenych
	// .. najdlhsie nepouzivane je v zasobniku posledne
	// .. prvky mozem zoskupit podla RAF suborov
	// Inkrementuj pocet pouziti
}
