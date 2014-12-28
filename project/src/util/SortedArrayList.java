package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class SortedArrayList<E> extends ArrayList<E> implements Serializable
{
	private static final long		serialVersionUID	= -4672036942962369067L;
	protected final Comparator<E>	comparator;

	@SuppressWarnings("rawtypes")
	public class DefaultComparator implements Comparator
	{
		@SuppressWarnings({"unchecked"})
		public int compare(Object obj1, Object obj2) {
			return ((Comparable) obj1).compareTo(obj2);
		}
	}
	public class HashComparator implements Comparator<E>
	{
		public int compare(E obj1, E obj2) {
			int a = obj1.hashCode();
			int b = obj2.hashCode();
			if (a > b) return 1;
			if (a < b) return -1;
			if (a == b && obj1.equals(obj2)) return 0;
			return 1;
		}
	}

	@SuppressWarnings("unchecked")
	public SortedArrayList(int initialCapacity) {
		super(initialCapacity);
		comparator = new DefaultComparator();
	}
	@SuppressWarnings("unchecked")
	public SortedArrayList() {
		super();
		comparator = new DefaultComparator();
	}
	public SortedArrayList(ArrayList<E> vertices, Comparator<E> com) {
		super(vertices);
		comparator = com;
	}
	@SuppressWarnings("unchecked")
	public SortedArrayList(ArrayList<E> vertices) {
		super(vertices);
		comparator = new DefaultComparator();
	}

	private int binarneHladania(E elemHash) {
		int lavy, pravy, stredny, compare;
		pravy = size() - 1;
		lavy = 0;
		while (lavy <= pravy) {
			stredny = (lavy + pravy) >> 1;
			compare = comparator.compare(elemHash, get(stredny));
			if (compare == 0) return stredny;
			if (compare == -1) pravy = stredny - 1;
			else lavy = stredny + 1;
		}
		return -(lavy + 1);
	}

	protected int indexOf2(E elem) {
		int hashIndex = binarneHladania(elem);
		if (hashIndex < 0) return -1;
		int index = hashIndex;
		
		while (index >= 0) {
			if (comparator.compare(get(index), elem) == 0) {
				while (index > 0 && comparator.compare(get(index - 1), elem) == 0)
					index--;
				return index;
			}
			index--;
		}

		index = hashIndex + 1;
		int velkost = size();
		while (index < velkost) {
			if (comparator.compare(get(index), elem) == 0) return index;
			index++;
		}
		return -1;
	}
	protected int lastIndexOf2(E elem) {
		int hashIndex = binarneHladania((E) elem);
		if (hashIndex < 0) return -1;

		int index = hashIndex;
		int velkost = size();
		while (index < velkost) {
			if (comparator.compare(get(index), elem) == 0) {
				while (index < (velkost - 1)
						&& comparator.compare(get(index + 1), elem) == 0)
					index++;
				return index;
			}
			index++;
		}

		index = hashIndex - 1;
		while (index >= 0) {
			if (comparator.compare(get(index), elem) == 0) return index;
			index--;
		}
		return -1;
	}

	public boolean add(E o) {
		int velkost = size();
		if (velkost == 0) {
			super.add(o);
			return true;
		}
		if (comparator.compare(o, get(velkost - 1)) >= 0) {
			super.add(o);
			return true;
		}

		int index = binarneHladania(o);
		if (index < 0) {
			super.add(-(index + 1), o);
			return true;
		}

		// Add after the last item with matching hashCodes
		while (index < (velkost - 1) && comparator.compare(get(index + 1), o) == 0)
			index++;
		super.add(index + 1, o);
		return true;
	}

	@SuppressWarnings("unchecked")
	public int indexOf(Object elem) {
		return indexOf2((E) elem);
	}
	public boolean contains(Object elem) {
		return indexOf(elem) > -1;
	}
	@SuppressWarnings("unchecked")
	public int lastIndexOf(Object elem) {
		return lastIndexOf2((E) elem);
	}
	public boolean remove(Object o) {
		int index = lastIndexOf(o);
		if (index == -1) return false;
		remove(index);
		return true;
	}
	public boolean addAll(Collection<? extends E> c) {
		Iterator<? extends E> iter = c.iterator();
		while (iter.hasNext())
			add(iter.next());
		return true;
	}
	
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}
}
