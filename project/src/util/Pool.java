
package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Trieda ktora vytvori len jednu instanciu pre jeden retazec. V gSemSearch
 * suboroch sa nachadza vela klucov a hodnot ktore sa opakuju. Tento mechanizmus
 * tak usetri mnoho pamete.
 * 
 * @author Lukas Sekerak
 */
public abstract class Pool<S, C extends Comparable<S>>
{
	// Hash mapa ConcurrentMap<String, KeyInfo> by bola mozno efektivnejsia
	// ale pametovo horsia pri velkom pocte prvkov
	protected ArrayList<C>	pool	= new ArrayList<C>(30);

	
	/**
	 * Vrat index pre skupinu pre unikatny kluc.
	 * @param key
	 * @return
	 */
	public int getByKey(S key) {
		int index = find(key);
		if (index == -1) {
			int size = pool.size();
			if (size == Character.MAX_VALUE) {
				throw new RuntimeException("Maximalne prijmem "
						+ Character.MAX_VALUE + " roznych klucov");
			}
			pool.add(NewElement(key, size));
			return size;
		}
		return index;
	}
	
	protected abstract C NewElement(S key, int index); // callback

	/**
	 * Najdi index skupiny pre unikatny kluc.
	 * @param key
	 * @return
	 */
	public int find(S key) {
		int index = pool.size() - 1;
		for (; index > -1; index--) {
			if (pool.get(index).compareTo(key) == 0) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * Vrat skupinu podla indexu.
	 * @param id
	 * @return
	 */
	public C get(int id) {
		return pool.get(id);
	}
	
	/**
	 * Vrat skupinu podla unikatneho kluca.
	 * @param id
	 * @return
	 */
	public C get(S key) {
		int id = find(key);
		if (id == -1) return null;
		return pool.get(id);
	}
	
	/**
	 * Vrat zoznam vsetkych skupin.
	 * @return
	 */
	public List<C> getGroups() {
		return pool;
	}
	
	/**
	 * Pomocny vypis vsetkych skupin a klucov.
	 */
	public void Dump() {
		System.out.println("Pool dump - Klucov: " + pool.size());
		for (C item : pool) {
			System.out.println(item.toString());
		}
	}
}
