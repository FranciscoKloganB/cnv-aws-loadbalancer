package hillClimbing.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import hillClimbing.solver.SolverArgumentParser;

@DynamoDBTable(tableName = "TESTCNVT17HillClimbDatabase")
public class TESTClimbRequestCostEntry {

    private String key;
    private Long NOP_INSTRUCTION;
    private Long CONSTANT_INSTRUCTION;
    private Long LOAD_INSTRUCTION;
    private Long STORE_INSTRUCTION;
    private Long STACK_INSTRUCTION;
    private Long ARITHMETIC_INSTRUCTION;
    private Long LOGICAL_INSTRUCTION;
    private Long CONVERSION_INSTRUCTION;
    private Long COMPARISON_INSTRUCTION;
    private Long CONDITIONAL_INSTRUCTION;
    private Long UNCONDITIONAL_INSTRUCTION;
    private Long CLASS_INSTRUCTION;
    private Long OBJECT_INSTRUCTION;
    private Long EXCEPTION_INSTRUCTION;
    private Long INSTRUCTIONCHECK_INSTRUCTION;
    private Long MONITOR_INSTRUCTION;

    public TESTClimbRequestCostEntry(String key) {
        this.key = key;
    }

    @DynamoDBHashKey(attributeName = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @DynamoDBAttribute(attributeName = "NOP_INSTRUCTION")
    public Long getNOP_INSTRUCTION() {
        return NOP_INSTRUCTION;
    }

    public void setNOPInstruction(Long value) {
        this.NOP_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "CONSTANT_INSTRUCTION")
    public Long getCONSTANT_INSTRUCTION() {
        return CONSTANT_INSTRUCTION;
    }

    public void setConstantInstruction(Long value) {
        this.CONSTANT_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "LOAD_INSTRUCTION")
    public Long getLOAD_INSTRUCTION() {
        return LOAD_INSTRUCTION;
    }

    public void setLoadInstruction(Long value) {
        this.LOAD_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "STORE_INSTRUCTION")
    public Long getSTORE_INSTRUCTION() {
        return STORE_INSTRUCTION;
    }

    public void setStoreInstruction(Long value) {
        this.STORE_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "STACK_INSTRUCTION")
    public Long getSTACK_INSTRUCTION() {
        return STACK_INSTRUCTION;
    }

    public void setStackInstruction(Long value) {
        this.STACK_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "ARITHMETIC_INSTRUCTION")
    public Long getARITHMETIC_INSTRUCTION() {
        return ARITHMETIC_INSTRUCTION;
    }

    public void setArithmeticInstruction(Long value) {
        this.ARITHMETIC_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "LOGICAL_INSTRUCTION")
    public Long getLOGICAL_INSTRUCTION() {
        return LOGICAL_INSTRUCTION;
    }

    public void setLogicalInstruction(Long value) {
        this.LOGICAL_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "CONVERSION_INSTRUCTION")
    public Long getCONVERSION_INSTRUCTION() {
        return CONVERSION_INSTRUCTION;
    }

    public void setConversionInstruction(Long value) {
        this.CONVERSION_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "COMPARISON_INSTRUCTION")
    public Long getCOMPARISON_INSTRUCTION() {
        return COMPARISON_INSTRUCTION;
    }

    public void setComparisonInstruction(Long value) {
        this.COMPARISON_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "CONDITIONAL_INSTRUCTION")
    public Long getCONDITIONAL_INSTRUCTION() {
        return CONDITIONAL_INSTRUCTION;
    }

    public void setConditionalInstruction(Long value) {
        this.CONDITIONAL_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "UNCONDITIONAL_INSTRUCTION")
    public Long getUNCONDITIONAL_INSTRUCTION() {
        return UNCONDITIONAL_INSTRUCTION;
    }

    public void setUnconditionalInstruction(Long value) {
        this.UNCONDITIONAL_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "CLASS_INSTRUCTION")
    public Long getCLASS_INSTRUCTION() {
        return CLASS_INSTRUCTION;
    }

    public void setClassInstruction(Long value) {
        this.CLASS_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "OBJECT_INSTRUCTION")
    public Long getOBJECT_INSTRUCTION() {
        return OBJECT_INSTRUCTION;
    }

    public void setObjectInstruction(Long value) {
        this.OBJECT_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "EXCEPTION_INSTRUCTION")
    public Long getEXCEPTION_INSTRUCTION() {
        return EXCEPTION_INSTRUCTION;
    }

    public void setExceptionInstruction(Long value) {
        this.EXCEPTION_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "INSTRUCTIONCHECK_INSTRUCTION")
    public Long getINSTRUCTIONCHECK_INSTRUCTION() {
        return INSTRUCTIONCHECK_INSTRUCTION;
    }
    public void setInstructionCheckInstruction(Long value) {
        this.INSTRUCTIONCHECK_INSTRUCTION = value;
    }

    @DynamoDBAttribute(attributeName = "MONITOR_INSTRUCTION")
    public Long getMONITOR_INSTRUCTION() {
        return MONITOR_INSTRUCTION;
    }

    public void setMonitorInstruction(Long value) {
        this.MONITOR_INSTRUCTION = value;
    }

    @Override
    public String toString() {
        return "TESTClimbRequestCostEntry{" +
                "key='" + key + '\'' +
                ", NOP_INSTRUCTION=" + NOP_INSTRUCTION +
                ", CONSTANT_INSTRUCTION=" + CONSTANT_INSTRUCTION +
                ", LOAD_INSTRUCTION=" + LOAD_INSTRUCTION +
                ", STORE_INSTRUCTION=" + STORE_INSTRUCTION +
                ", STACK_INSTRUCTION=" + STACK_INSTRUCTION +
                ", ARITHMETIC_INSTRUCTION=" + ARITHMETIC_INSTRUCTION +
                ", LOGICAL_INSTRUCTION=" + LOGICAL_INSTRUCTION +
                ", CONVERSION_INSTRUCTION=" + CONVERSION_INSTRUCTION +
                ", COMPARISON_INSTRUCTION=" + COMPARISON_INSTRUCTION +
                ", CONDITIONAL_INSTRUCTION=" + CONDITIONAL_INSTRUCTION +
                ", UNCONDITIONAL_INSTRUCTION=" + UNCONDITIONAL_INSTRUCTION +
                ", CLASS_INSTRUCTION=" + CLASS_INSTRUCTION +
                ", OBJECT_INSTRUCTION=" + OBJECT_INSTRUCTION +
                ", EXCEPTION_INSTRUCTION=" + EXCEPTION_INSTRUCTION +
                ", INSTRUCTIONCHECK_INSTRUCTION=" + INSTRUCTIONCHECK_INSTRUCTION +
                ", MONITOR_INSTRUCTION=" + MONITOR_INSTRUCTION +
                '}';
    }
}
