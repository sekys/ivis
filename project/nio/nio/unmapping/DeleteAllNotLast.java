package nio.unmapping;

import java.util.List;
import nio.base.Buffer;

/**
 * Vymaz vsetke okrem posledneho. - ten je dobry pri loadingu
 * 
 * @author Lukas Sekerak
 */
public class DeleteAllNotLast implements IUnMappingAlgoritm<Buffer>
{
	public boolean UnMappingAlgoritm(List<Buffer> data) {
		boolean once = false;		
		while(data.size() > 1) {
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
