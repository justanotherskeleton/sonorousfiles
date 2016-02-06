package src.sonorous.event;

import java.util.ArrayList;
import java.util.List;

public class CTInvoker {
	private List<CTListener> listeners = new ArrayList<CTListener>();

    public void addListener(CTListener toAdd) {
        listeners.add(toAdd);
    }

    public void triggerEvent(int id) {
        for (CTListener hl : listeners)
            hl.segmentReceived(id);
    }
}