package ch.epfl.xblast.etape6.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.etape6.events.Event;
import ch.epfl.xblast.etape6.events.EventSequence;

public class EventsGenerator {
    private final Map<PlayerID, Iterator<Event>> playersEvents;
    private Map<PlayerID, Event> current = new HashMap<PlayerID, Event>();

    public EventsGenerator(EventSequence[] playersEvents) {
        assert (playersEvents != null);
        this.playersEvents = createIterators(playersEvents);

    }

    /**
     * @return the current
     */
    public Map<PlayerID, Event> getCurrent() {
        return Collections.unmodifiableMap(current);
    }

    /**
     * @return the playersEvents
     */
    public Map<PlayerID, Iterator<Event>> getPlayersEvents() {
        return Collections.unmodifiableMap(playersEvents);
    }

    public Map<PlayerID, Event> next() {

        for (PlayerID pId : playersEvents.keySet()) {
            Iterator<Event> iter = playersEvents.get(pId);
            if (iter.hasNext()) {
                current.put(pId, iter.next());
            } else {

                current = new HashMap<PlayerID, Event>();
                break;
            }
        }
        return current;

    }

    private void checkAllPlayersEqualNumEvents(EventSequence[] playersEvents) {
        int eventsNum = playersEvents[0].getNumEvents();

        for (EventSequence pEvents : playersEvents) {
            if (pEvents.getNumEvents() != eventsNum) {

                throw new IllegalArgumentException("Epected " + eventsNum + " but found " + pEvents.getNumEvents()
                        + "for player " + pEvents.getPlayerID());
            }

        }

    }

    private Map<PlayerID, Iterator<Event>> createIterators(EventSequence[] playersEvents) {
        checkAllPlayersEqualNumEvents(playersEvents);
        Map<PlayerID, Iterator<Event>> iterators = new HashMap<PlayerID, Iterator<Event>>();
        for (EventSequence playerEvents : playersEvents) {

            iterators.put(playerEvents.getPlayerID(), playerEvents.getEventIterator());

        }
        return Collections.unmodifiableMap(iterators);
    }

}
