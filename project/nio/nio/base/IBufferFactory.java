package nio.base;

import java.io.RandomAccessFile;
import java.util.List;
import nio.unmapping.IUnMappingAlgoritm;

/**
 * Trieda, ktora vytvara buffre a hned ich zaregistruje.
 * Tzv mame pristup k vsetkym buffrom.
 * 
 * @author Lukas Sekerak
 */
public interface IBufferFactory
{
	/**
	 * Vrat vsetky buffre.
	 */
	public List<Buffer> getAllocated();
	
	/**
	 * Vytvor buffer cez tovaren.
	 */
	public Buffer create(RandomAccessFile raf, long pos, int size);
	
	/**
	 * Defaultny odmapujuci algoritmus.
	 */
	public IUnMappingAlgoritm<Buffer> getDefaultUnmapping();
	
	/**
	 * Odmapuj buffre.
	 */
	public boolean unmap(IUnMappingAlgoritm<Buffer> unmap);
}
