package ch.epfl.xblast.etape6.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.epfl.xblast.PlayerID;

public class EventSequence {
    private PlayerID pId;
    private boolean isOutput;
    private final List<Event> events;

    public EventSequence(PlayerID pId, int[][] events, boolean isOutput) {
        assert (pId != null);
        this.pId = pId;
        this.isOutput = isOutput;
        this.events = toEventList(events);

    }

    private List<Event> toEventList(int[][] events) {

        List<Event> eventList = new ArrayList<Event>(events.length);
        for (int[] event : events) {
            if (!isOutput)
                eventList.add(new PlayerEvent(event));
            else
                eventList.add(new PlayerState(event));
        }
        return Collections.unmodifiableList(eventList);

    }

    public int getNumEvents() {

        return events.size();
    }

    public Iterator<Event> getEventIterator() {

        return events.iterator();

    }

    public PlayerID getPlayerID() {

        return pId;
    }
}
