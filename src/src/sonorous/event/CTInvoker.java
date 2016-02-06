package src.sonorous.event;

import java.util.ArrayList;
import java.util.List;

public class CTInvoker {
    private List<CTInvoker> listeners = new ArrayList<CTInvoker>();

    public void addListener(CTInvoker toAdd) {
        listeners.add(toAdd);
    }

    public void triggerEvent() {
        for (CTInvoker hl : listeners)
            hl.triggerEvent();
    }
}