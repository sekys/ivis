package nio.unmapping;

import java.util.List;
import nio.base.Buffer;

/**
 * Vymaz vsetke
 * 
 * @author Lukas Sekerak
 */
public class DeleteAll implements IUnMappingAlgoritm<Buffer>
{
	public boolean UnMappingAlgoritm(List<Buffer> data) {
		boolean once = false;		
		while(data.size() > 0) {
			Buffer b = data.get(0);
			if (!b.isLoaded()) {
				throw new RuntimeException("Buffer je v zozname alokovanych ale je odmapovany.");
			}
			b.unmap();
			once = true;
		}
		return once;	
	}
}
