package nio.base;

import java.nio.MappedByteBuffer;

/**
 * Trieda, ktora uklada informacie o pozicii v subore.
 * Max velkost namapovaneho priestoru je int
 * max velkost pre jeden objekt je char
 * 
 * @author Lukas Sekerak
 */
public class PosInfo implements INio
{	
	public int pos; // 4
	public char	size; // 2

	@Override
	public void nioWrite(MappedByteBuffer buffer) {
		buffer.putInt(pos);
		buffer.putChar(size);
	}
	@Override
	public void nioRead(MappedByteBuffer buffer) {
		pos = buffer.getInt();
		size = buffer.getChar();
	}

	public void setPos(long a) {
		pos = (int) a;
		long test = (int) a;
		if(test != a) {
			throw new RuntimeException();
		}
	}
	public void setSize(int b) {
		size = (char) b;
		int test = (char) b;
		if(test != b) {
			throw new RuntimeException();
		}
	}
}
