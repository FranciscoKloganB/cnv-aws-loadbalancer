package hillClimbing.instrumentation;

import hillClimbing.database.ClimbRequestCostEntry;
import hillClimbing.database.Database;

import java.util.HashMap;
import java.util.Map;

public class ThreadMapper {

    private static Map<Long, Long> instructionsPerThread = new HashMap<>();
    private static Map<Long, ClimbRequestCostEntry> requestCostEntryMapPerThread = new HashMap<>();

    public static synchronized void updateEntry(ClimbRequestCostEntry entry) {
        Long threadID = Thread.currentThread().getId();
        requestCostEntryMapPerThread.put(threadID, entry);
    }

    public static synchronized void updateConstInstrCount(Object _) {
        Long threadID = Thread.currentThread().getId();
        Long constInstrCount = 0L;

        if (instructionsPerThread.containsKey(threadID)) {
            constInstrCount = instructionsPerThread.get(threadID);
        }

        instructionsPerThread.put(threadID, constInstrCount + 1);
    }

    public static synchronized void sendCountToDB(Object _) {
        Long threadID = Thread.currentThread().getId();
        Long constInstrCount = 0L;
        if (instructionsPerThread.containsKey(threadID)) {
            constInstrCount = instructionsPerThread.get(threadID);
        }
        try {
            ClimbRequestCostEntry entry = requestCostEntryMapPerThread.get(threadID);
            entry.setInstructions(constInstrCount);
            Database.getDatabase().insert(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instructionsPerThread.put(threadID, 0L);
        requestCostEntryMapPerThread.put(threadID, null);
    }


}
