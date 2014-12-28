
package util;

import java.util.Iterator;

/**
 * Trieda dokaze abstrakne spravit z dvojdimenzionalneho pola len jednodimenzionalnu.
 * 
 * @author Lukas Sekerak
 */
public abstract class IteratorBiDimensionalArray<R, C> implements Iterator<C>
{
	private Iterator<C>	stlpecIterator	= null;
	private Iterator<R>	radIterator;

	public IteratorBiDimensionalArray() {
		radIterator = getRowIterator();
	}
	
	public boolean hasNext() {
		if (stlpecIterator != null && stlpecIterator.hasNext()) return true;

		while (radIterator.hasNext()) {
			stlpecIterator = RowToIterator(radIterator.next());
			if (stlpecIterator.hasNext()) return true;
		}
		return false;
	}

	@Override
	public C next() {
		if (stlpecIterator != null && stlpecIterator.hasNext()) return stlpecIterator.next();
		while (radIterator.hasNext()) {
			stlpecIterator = RowToIterator(radIterator.next());
			if (stlpecIterator.hasNext()) return stlpecIterator.next();
		}
		return null;
	}

	@Override
	public void remove() {
		if (stlpecIterator != null && stlpecIterator.hasNext()) stlpecIterator.remove();
		while (radIterator.hasNext()) {
			stlpecIterator = RowToIterator(radIterator.next());
			if (stlpecIterator.hasNext()) {
				stlpecIterator.remove();
				return;
			}
		}

	}
	
	protected abstract Iterator<R> getRowIterator();
	protected abstract Iterator<C> RowToIterator(R row);

}
