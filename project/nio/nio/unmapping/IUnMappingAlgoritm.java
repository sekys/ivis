
package nio.unmapping;

import java.util.List;

/**
 * Algoritmus najdenia obete :
 * Vymaz prvy mozny buffer odzadu
 * Vymaz prvy mozny buffer nahodne
 * Vymaz vsetky bufferi - tie nove sa nacitaju a kym prejde 60 bufferov tak to je chvila
 */
public interface IUnMappingAlgoritm<T>
{
	/**
	 * Unmapovacia akcia pre N bufferov.
	 * 
	 * @param data
	 * @return
	 */
	public boolean UnMappingAlgoritm(List<T> data);
}
