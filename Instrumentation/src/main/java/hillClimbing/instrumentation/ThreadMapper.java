package hillClimbing.instrumentation;

import hillClimbing.database.ClimbRequestCostEntry;
import hillClimbing.database.Database;
import hillClimbing.database.TESTClimbRequestCostEntry;

import java.util.HashMap;
import java.util.Map;

public class ThreadMapper {

    private static Map<Long, Map<Integer, Long>> TESTInstructionsPerThread = new HashMap<>();
    private static Map<Long, Long> instructionsPerThread = new HashMap<>();
    private static Map<Long, ClimbRequestCostEntry> requestCostEntryMapPerThread = new HashMap<>();

    public static synchronized void updateEntry(ClimbRequestCostEntry entry) {
        Long threadID = Thread.currentThread().getId();
        requestCostEntryMapPerThread.put(threadID, entry);
    }

    public static synchronized void TESTUpdateInstrCount(int instructionType) {
        Long threadID = Thread.currentThread().getId();
        Long instrCount = 0L;

        if (!TESTInstructionsPerThread.containsKey(threadID)) {
            TESTInstructionsPerThread.put(threadID, new HashMap<Integer, Long>());
        }

        if (TESTInstructionsPerThread.get(threadID).containsKey(instructionType)) {
            instrCount = TESTInstructionsPerThread.get(threadID).get(instructionType);
        }

        TESTInstructionsPerThread.get(threadID).put(instructionType, instrCount + 1);
    }

    public static synchronized void TESTSendCountToDB() {

        Long threadID = Thread.currentThread().getId();
        Map<Integer, Long> instructionTypeCounters = new HashMap<>();
        if (TESTInstructionsPerThread.containsKey(threadID)) {
            instructionTypeCounters = TESTInstructionsPerThread.get(threadID);
        }
        try {
            TESTClimbRequestCostEntry entry = new TESTClimbRequestCostEntry(requestCostEntryMapPerThread.get(threadID).getKey());

            for (Map.Entry<Integer, Long> counter : instructionTypeCounters.entrySet()) {
                switch (counter.getKey()) {
                    case 0:
                        entry.setNOPInstruction(counter.getValue());
                        break;
                    case 1:
                        entry.setConstantInstruction(counter.getValue());
                        break;
                    case 2:
                        entry.setLoadInstruction(counter.getValue());
                        break;
                    case 3:
                        entry.setStoreInstruction(counter.getValue());
                        break;
                    case 4:
                        entry.setStackInstruction(counter.getValue());
                        break;
                    case 5:
                        entry.setArithmeticInstruction(counter.getValue());
                        break;
                    case 6:
                        entry.setLogicalInstruction(counter.getValue());
                        break;
                    case 7:
                        entry.setConversionInstruction(counter.getValue());
                        break;
                    case 8:
                        entry.setComparisonInstruction(counter.getValue());
                        break;
                    case 9:
                        entry.setConditionalInstruction(counter.getValue());
                        break;
                    case 10:
                        entry.setUnconditionalInstruction(counter.getValue());
                        break;
                    case 11:
                        entry.setClassInstruction(counter.getValue());
                        break;
                    case 12:
                        entry.setObjectInstruction(counter.getValue());
                        break;
                    case 13:
                        entry.setExceptionInstruction(counter.getValue());
                        break;
                    case 14:
                        entry.setInstructionCheckInstruction(counter.getValue());
                        break;
                    case 15:
                        entry.setMonitorInstruction(counter.getValue());
                        break;
                }
            }

            Database.TESTInsert(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TESTInstructionsPerThread.put(threadID, new HashMap<Integer, Long>());
        requestCostEntryMapPerThread.put(threadID, null);
    }

    public static synchronized void TESTSendTime(long timeDiff) {
        Long threadID = Thread.currentThread().getId();
        Long loadInstrCount = 0L;
        try {
            ClimbRequestCostEntry entry = requestCostEntryMapPerThread.get(threadID);
            entry.setInstructions(loadInstrCount);
            Database.TESTUpdate(entry.getKey(), timeDiff);
        } catch (Exception e) {
            e.printStackTrace();
        }

        instructionsPerThread.put(threadID, 0L);
        requestCostEntryMapPerThread.put(threadID, null);
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
