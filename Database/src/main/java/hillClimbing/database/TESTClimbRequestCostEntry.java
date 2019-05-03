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

    private Integer xStartPoint;
    private Integer yStartPoint;
    private Integer xUpperLeftPoint;
    private Integer yUpperLeftPoint;
    private Integer xLowerRightPoint;
    private Integer yLowerRightPoint;

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

    @DynamoDBAttribute(attributeName = "xStartPoint")
    public Integer getxStartPoint() {
        return xStartPoint;
    }

    public void setxStartPoint(Integer xStartPoint) {
        this.xStartPoint = xStartPoint;
    }

    @DynamoDBAttribute(attributeName = "yStartPoint")
    public Integer getyStartPoint() {
        return yStartPoint;
    }

    public void setyStartPoint(Integer yStartPoint) {
        this.yStartPoint = yStartPoint;
    }

    @DynamoDBAttribute(attributeName = "xUpperLeftPoint")
    public Integer getxUpperLeftPoint() {
        return xUpperLeftPoint;
    }

    public void setxUpperLeftPoint(Integer xUpperLeftPoint) {
        this.xUpperLeftPoint = xUpperLeftPoint;
    }

    @DynamoDBAttribute(attributeName = "yUpperLeftPoint")
    public Integer getyUpperLeftPoint() {
        return yUpperLeftPoint;
    }

    public void setyUpperLeftPoint(Integer yUpperLeftPoint) {
        this.yUpperLeftPoint = yUpperLeftPoint;
    }

    @DynamoDBAttribute(attributeName = "xLowerRightPoint")
    public Integer getxLowerRightPoint() {
        return xLowerRightPoint;
    }

    public void setxLowerRightPoint(Integer xLowerRightPoint) {
        this.xLowerRightPoint = xLowerRightPoint;
    }

    @DynamoDBAttribute(attributeName = "yLowerRightPoint")
    public Integer getyLowerRightPoint() {
        return yLowerRightPoint;
    }

    public void setyLowerRightPoint(Integer yLowerRightPoint) {
        this.yLowerRightPoint = yLowerRightPoint;
    }

    @Override
    public String toString() {
        return "TESTClimbRequestCostEntry{" +
                "key='" + key + '\'' +
                ", xStartPoint=" + xStartPoint +
                ", yStartPoint=" + yStartPoint +
                ", xUpperLeftPoint=" + xUpperLeftPoint +
                ", yUpperLeftPoint=" + yUpperLeftPoint +
                ", xLowerRightPoint=" + xLowerRightPoint +
                ", yLowerRightPoint=" + yLowerRightPoint +
                '}';
    }

    public void setNOPInstruction(Long value) {

    }

    public void setConstantInstruction(Long value) {
    }

    public void setLoadInstruction(Long value) {
    }

    public void setStoreInstruction(Long value) {
    }

    public void setStackInstruction(Long value) {
    }

    public void setArithmeticInstruction(Long value) {
    }

    public void setLogicalInstruction(Long value) {
    }

    public void setConversionInstruction(Long value) {
    }

    public void setComparisonInstruction(Long value) {
    }

    public void setConditionalInstruction(Long value) {
    }

    public void setUnconditionalInstruction(Long value) {
    }

    public void setClassInstruction(Long value) {
    }

    public void setObjectInstruction(Long value) {
    }

    public void setExceptionInstruction(Long value) {
    }

    public void setInstructionCheckInstruction(Long value) {
    }

    public void setMonitorInstruction(Long value) {
    }

    public Long getNOP_INSTRUCTION() {
        return NOP_INSTRUCTION;
    }

    public Long getCONSTANT_INSTRUCTION() {
        return CONSTANT_INSTRUCTION;
    }

    public Long getLOAD_INSTRUCTION() {
        return LOAD_INSTRUCTION;
    }

    public Long getSTORE_INSTRUCTION() {
        return STORE_INSTRUCTION;
    }

    public Long getSTACK_INSTRUCTION() {
        return STACK_INSTRUCTION;
    }

    public Long getARITHMETIC_INSTRUCTION() {
        return ARITHMETIC_INSTRUCTION;
    }

    public Long getLOGICAL_INSTRUCTION() {
        return LOGICAL_INSTRUCTION;
    }

    public Long getCONVERSION_INSTRUCTION() {
        return CONVERSION_INSTRUCTION;
    }

    public Long getCOMPARISON_INSTRUCTION() {
        return COMPARISON_INSTRUCTION;
    }

    public Long getCONDITIONAL_INSTRUCTION() {
        return CONDITIONAL_INSTRUCTION;
    }

    public Long getUNCONDITIONAL_INSTRUCTION() {
        return UNCONDITIONAL_INSTRUCTION;
    }

    public Long getCLASS_INSTRUCTION() {
        return CLASS_INSTRUCTION;
    }

    public Long getOBJECT_INSTRUCTION() {
        return OBJECT_INSTRUCTION;
    }

    public Long getEXCEPTION_INSTRUCTION() {
        return EXCEPTION_INSTRUCTION;
    }

    public Long getINSTRUCTIONCHECK_INSTRUCTION() {
        return INSTRUCTIONCHECK_INSTRUCTION;
    }

    public Long getMONITOR_INSTRUCTION() {
        return MONITOR_INSTRUCTION;
    }
}
