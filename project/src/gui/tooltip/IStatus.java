
package gui.tooltip;

/**
 * Interface pre nastavovanie statusu programu.
 * 
 * @author Lukas Sekerak 
 */
public interface IStatus
{
	/**
	 * Nastav status programu.
	 */
	public void setStatus(String status, int id);
	
	/**
	 * Aky je aktualny / posledny status ?
	 * @return
	 */
	public int getStatus();
}
