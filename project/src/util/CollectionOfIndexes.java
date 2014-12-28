
package util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Trieda vytvori abstrakne pole cisel - indexo na ine pole.
 * Taketo indexi sa potom lahsie spracuvaju.
 * 
 * @author Lukas Sekerak
 */
public abstract class CollectionOfIndexes implements Collection<Integer>
{
	@Override
	public abstract int size();
	public abstract void remove(Integer index);

	@Override
	public boolean add(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		return contains((Integer) o);
	}
	public boolean contains(Integer o) {
		return o > -1 && o < size();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	protected class IndexIterator implements Iterator<Integer>
	{
		protected Integer	actual	= new Integer(-1);

		@Override
		public boolean hasNext() {
			return actual < (size() - 1);
		}
		@Override
		public Integer next() {
			return ++actual;
		}
		@Override
		public void remove() {
			CollectionOfIndexes.this.remove(actual);
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		return new IndexIterator();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

}
