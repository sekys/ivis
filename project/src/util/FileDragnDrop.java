
package util;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.List;

/**
 * Trieda zabaluje podporu nad DragnDrop pretahovanim suborov
 * 
 * @author Lukas Sekerak
 */
public abstract class FileDragnDrop implements DropTargetListener
{

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {}

	@Override
	public void dragExit(DropTargetEvent dte) {}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {}

	protected abstract void catchFiles(@SuppressWarnings("rawtypes") List files);

	@Override
	public void drop(DropTargetDropEvent target) {
		try {
			Transferable data = target.getTransferable();
			DataFlavor[] files = data.getTransferDataFlavors();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFlavorJavaFileListType()) {
					target.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					@SuppressWarnings("rawtypes")
					List list = (List) data.getTransferData(files[i]);
					catchFiles(list);
					target.dropComplete(true);
					return;
				}
			}
			target.rejectDrop();
		}
		catch (Exception e) {
			e.printStackTrace();
			target.rejectDrop();
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {}
}
