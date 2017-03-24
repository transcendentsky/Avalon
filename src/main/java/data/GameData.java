package data;

import util.GEvent;
import util.GPath;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Eldath Ray on 2017/3/21.
 *
 * @author Eldath Ray
 */
public class GameData {
    public static class Map {
        private static List<GPath> map = new LinkedList<>();
        private static Stream<GPath> allExit;

        public static boolean addPath(GPath exit) {
            if (map == null) throw new UnsupportedOperationException("list is solid now.");
            return map.add(exit);
        }

        public static Stream<GPath> getGameMap() {
            if (allExit == null) throw new UnsupportedOperationException("please solid list first.");
            return allExit;
        }

        public static void solidify() {
            allExit = map.stream();
            map = null;
        }
    }

    public static class Event {
        private static List<GEvent> events = new LinkedList<>();
        private static Stream<GEvent> allEvents;

        public static boolean addEvent(GEvent exit) {
            if (events == null) throw new UnsupportedOperationException("list is solid now.");
            return events.add(exit);
        }

        public static Stream<GEvent> getEvents() {
            if (allEvents == null) throw new UnsupportedOperationException("please solid list first.");
            return allEvents;
        }

        public static void solidify() {
            allEvents = events.stream();
            events = null;
        }
    }
}