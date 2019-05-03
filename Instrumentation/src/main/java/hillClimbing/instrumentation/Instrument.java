package hillClimbing.instrumentation;

import BIT.highBIT.ClassInfo;
import BIT.highBIT.Instruction;
import BIT.highBIT.InstructionTable;
import BIT.highBIT.Routine;

import java.io.File;
import java.util.Enumeration;

public class Instrument {

    public static void main(String[] argv) {

        try {
            File in_dir = new File(argv[0]);
            File out_dir = new File(argv[1]);

            if (in_dir.isDirectory() && out_dir.isDirectory()) {
                doCheckConstantInstruction(in_dir, out_dir);
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static void doCheckConstantInstruction(File in_dir, File out_dir) {
        String filelist[] = in_dir.list();

        for (String filename : filelist) {
            if (filename.endsWith(".class")) {
                String in_filename = in_dir.getAbsolutePath() + System.getProperty("file.separator") + filename;
                String out_filename = out_dir.getAbsolutePath() + System.getProperty("file.separator") + filename;
                ClassInfo ci = new ClassInfo(in_filename);
                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {
                    Routine routine = (Routine) e.nextElement();
                    for (Enumeration instructions = (routine.getInstructionArray()).elements(); instructions.hasMoreElements(); ) {
                        Instruction instr = (Instruction) instructions.nextElement();
                        int opcode = instr.getOpcode();
                        int instr_type = InstructionTable.InstructionTypeTable[opcode];
                        instr.addBefore("hillClimbing/instrumentation/ThreadMapper", "TESTUpdateInstrCount", instr_type);
//                        if (instr_type == InstructionTable.LOAD_INSTRUCTION) {
//                            instr.addBefore("hillClimbing/instrumentation/ThreadMapper", "updateLoadInstrCount", 0);
//                        }
                    }
                }
                ci.write(out_filename);
            }
        }
    }
}
