
package nio.mapping;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import nio.base.BufferAutoRegisteringFactory;
import nio.base.IBufferFactory;
import nio.base.INio;

/**
 * Pametovo namapovany arraylist pre jeden objekt, ktory ma
 * konstantnu velkost.
 * 
 * @author Lukas Sekerak
 * @param <T>
 */
public class StaticArrayList<T extends INio> extends SimpleArrayList<T>
{

	public StaticArrayList(File file,
			int elementsize,
			int DelimPoPrvkov,
			IBufferFactory factory) throws IOException {
		super(file, elementsize, DelimPoPrvkov);
		map.setFactory(factory);
	}
	
	public StaticArrayList(File file,
			int elementsize,
			int DelimPoPrvkov,
			IBufferFactory factory,
			int initialSize) throws IOException {
		this(file, elementsize, DelimPoPrvkov, factory);
		Initialize(initialSize);
	}

	public boolean add(T v) {
		add();
		set(size() - 1, v);
		return true;
	}
	public synchronized int add2(T v) {
		add();
		int size = size() - 1;
		set(size, v);
		return size;
	}
	public synchronized void get(int index, T value) {
		value.nioRead(getBuffer(index));
	}
	
	public T set(int index, T value) {
		value.nioWrite(getBuffer(index));
		return value;
	}

	@Override
	public void add(int arg0, T arg1) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public T get(int index) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean contains(Object arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public int indexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}
	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException();
	}
	@Override
	public ListIterator<T> listIterator(int arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}
	@Override
	public T remove(int index) {
		super.delete(index);
		return null;
	}
	@Override
	public List<T> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) {
		class Cislo implements INio
		{
			public int	a;

			@Override
			public void nioWrite(MappedByteBuffer buffer) {
				buffer.putInt(a);
			}
			@Override
			public void nioRead(MappedByteBuffer buffer) {
				a = buffer.getInt();
			}
			public Cislo(int v) {
				a = v;
			}
		}
		StaticArrayList<Cislo> test = null;
		IBufferFactory factory = new BufferAutoRegisteringFactory();

		try {
			test = new StaticArrayList<Cislo>(new File("test.db"), 100000, 1000000, factory);
			for (int i = 0; i < test.size(); i++) {
				test.set(i, new Cislo(i));
			}
			Cislo cislo = new Cislo(5);
			for (int i = 0; i < test.size(); i++) {
				test.get(i, cislo);
			}
			System.out.println();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
