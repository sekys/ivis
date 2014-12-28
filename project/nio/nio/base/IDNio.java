
package nio.base;

/**
 * Interface pre dynamic nio objekt.
 * 
 * @author Lukas Sekerak
 */
public interface IDNio extends INio
{
	/**
	 * Urcuje kolko bytov sa ma ist zapisat do suboru.
	 * -1 znamena ze sa velkost od poslednej zmeny nezmenila.
	 * Teda ide len o update nie o zvacsenie dat.
	 * 
	 * @return
	 */
	public int nioSize();
}
