
package graph.objects;

import java.nio.MappedByteBuffer;
import nio.base.INio;

/**
 * Hrana ako objekt v pameti nie v namapovanom subore.
 * Cez INIO sa objekt nacita.
 * 
 * @author Lukas Sekerak
 */
public class Edge implements INio
{
	protected char	sourceKey;
	protected int	sourceIndex	= -1;
	protected char	targetKey;
	protected int	targetIndex	= -1;
	protected int	priority;

	@Override
	public void nioWrite(MappedByteBuffer buffer) {
		buffer.putChar(sourceKey);
		buffer.putInt(sourceIndex);
		buffer.putChar(targetKey);
		buffer.putInt(targetIndex);
		buffer.putInt(priority);
	}
	@Override
	public void nioRead(MappedByteBuffer buffer) {
		sourceKey = buffer.getChar();
		sourceIndex = buffer.getInt();
		targetKey = buffer.getChar();
		targetIndex = buffer.getInt();
		priority = buffer.getInt();
	}
	public String toString() {
		return sourceKey + "_" + sourceIndex + " => " + targetKey + "_"
				+ targetIndex + " (" + priority + ")";
	}

	public static Edge LoadPriorityOnly() {
		return new Edge()
		{
			@Override
			public void nioWrite(MappedByteBuffer buffer) {
				throw new UnsupportedOperationException();
			}
			@Override
			public void nioRead(MappedByteBuffer buffer) {
				buffer.position(buffer.position() + 12);
				priority = buffer.getInt();
			}
		};
	}

	public static Edge LoadSourceOnly() {
		return new Edge()
		{
			@Override
			public void nioWrite(MappedByteBuffer buffer) {
				throw new UnsupportedOperationException();
			}
			@Override
			public void nioRead(MappedByteBuffer buffer) {
				sourceKey = buffer.getChar();
				sourceIndex = buffer.getInt();
			}
		};
	}
	public static Edge LoadTargetOnly() {
		return new Edge()
		{
			@Override
			public void nioWrite(MappedByteBuffer buffer) {
				throw new UnsupportedOperationException();
			}
			@Override
			public void nioRead(MappedByteBuffer buffer) {
				buffer.position(buffer.position() + 6);
				targetKey = buffer.getChar();
				targetIndex = buffer.getInt();
			}
		};
	}
	public char getSourceKey() {
		return sourceKey;
	}
	public int getSourceIndex() {
		return sourceIndex;
	}
	public char getTargetKey() {
		return targetKey;
	}
	public int getTargetIndex() {
		return targetIndex;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public void setSource(char key, int index) {
		sourceKey = key;
		sourceIndex = index;
	}
	public void setTarget(char key, int index) {
		targetKey = key;
		targetIndex = index;
	}
}
