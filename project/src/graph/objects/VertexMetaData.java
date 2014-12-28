
package graph.objects;

import java.nio.MappedByteBuffer;
import nio.base.IDNio;

/**
 * Vrchol ako objket, ktory sa nachadza v pameti
 * a nie v namapovanom subore. Takto si mozem opytat
 * vicero vrcholov zo suboru. Pomocou garbace collectoru
 * viem ze ked sa objekt nevyuzije uvolni sa. Ziskam
 * tak viacej pameti.
 * 
 * @author Lukas Sekerak
 */
public class VertexMetaData implements IDNio
{
	protected char[]	value;
	protected int		priority;

	public static int Size(int valuelength) {
		return 4 + // priorita
		2 + 2 * valuelength; // Value - dynamicke nech ide posledne
	}
	@Override
	public int nioSize() {
		return Size(value.length);
	}

	public void set(char[] value, int priority) {
		this.value = value;
		this.priority = priority;
	}

	@Override
	public void nioWrite(MappedByteBuffer buffer) {
		buffer.putInt(priority);

		// Uloz do bufferua
		buffer.putChar((char) value.length);
		for (int i = 0; i < value.length; i++)
			buffer.putChar(value[i]);
	}

	@Override
	public void nioRead(MappedByteBuffer buffer) {
		int size;
		priority = buffer.getInt();

		size = buffer.getChar();
		value = new char[size];
		buffer.asCharBuffer().get(value, 0, size);
		/*
		 * for (int i = 0; i < size; i++)
		 * value[i] = buffer.getChar();
		 */
	}

	public static VertexMetaData PriorityOnly() {
		return new VertexMetaData()
		{
			@Override
			public void nioWrite(MappedByteBuffer buffer) {
				buffer.putInt(priority);
			}
			@Override
			public void nioRead(MappedByteBuffer buffer) {
				priority = buffer.getInt();
			}
			@Override
			public int nioSize() {
				return -1;
			}
		};
	}
}
