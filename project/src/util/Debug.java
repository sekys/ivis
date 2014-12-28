
package util;

/**
 * Pomocna trieda pre rychle debugovanie.
 * Meria cas a spotrebovanu pamet medzi 2 bodmy.
 * 
 * @author Lukas Sekerak
 */
public class Debug
{
	protected long		pamet;
	protected double	casovyInterval;

	/**
	 * Zacni stopovat cas.
	 */
	public Debug() {
		pamet = Runtime.getRuntime().freeMemory();
		casovyInterval = System.currentTimeMillis();
	}

	/**
	 * Zastav stopky a vypis kontrolnu hlasku.
	 */
	public void End(String name) {
		casovyInterval = (System.currentTimeMillis() - casovyInterval) / 1000.0;
		pamet = (pamet - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
		System.out.println(name + " za " + casovyInterval + "sec " + pamet
				+ "mb");
	}
}
