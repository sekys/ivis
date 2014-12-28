
package nio.base;

import java.nio.MappedByteBuffer;

/**
 * Interface pre staticky nio objekt.
 * Ten ma vzdy rovnaku velkost.
 * 
 * @author Lukas Sekerak
 */
public interface INio
{
	/**
	 * Zapis udaje do suboru
	 * 
	 * @param buffer
	 */
	public void nioWrite(MappedByteBuffer buffer);

	/**
	 * Naictaj udaje zo suboru
	 * 
	 * @param buffer
	 */
	public void nioRead(MappedByteBuffer buffer);
}
