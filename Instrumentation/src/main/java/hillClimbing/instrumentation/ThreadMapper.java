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

    public static synchronized void updateLoadInstrCount(int _) {
        Long threadID = Thread.currentThread().getId();
        Long loadInstrCount = 0L;

        if (instructionsPerThread.containsKey(threadID)) {
            loadInstrCount = instructionsPerThread.get(threadID);
        }

        instructionsPerThread.put(threadID, loadInstrCount + 1);
    }

    public static synchronized void sendCountToDB() {

        Long threadID = Thread.currentThread().getId();
        Long loadInstrCount = 0L;
        if (instructionsPerThread.containsKey(threadID)) {
            loadInstrCount = instructionsPerThread.get(threadID);
        }
        try {
            ClimbRequestCostEntry entry = requestCostEntryMapPerThread.get(threadID);
            entry.setInstructions(loadInstrCount);
            Database.insert(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instructionsPerThread.put(threadID, 0L);
        requestCostEntryMapPerThread.put(threadID, null);
    }


}
