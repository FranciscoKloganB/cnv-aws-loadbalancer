package hillClimbing.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import hillClimbing.solver.SolverArgumentParser;

@DynamoDBTable(tableName = "CNVT17HillClimbDatabase")
public class ClimbRequestCostEntry {

    private String key;
    private Long instructions;
    private String hill;
    private String strategy;
    private Integer xStartPoint;
    private Integer yStartPoint;
    private Integer xUpperLeftPoint;
    private Integer yUpperLeftPoint;
    private Integer xLowerRightPoint;
    private Integer yLowerRightPoint;

    public ClimbRequestCostEntry(SolverArgumentParser ap) {
        hill = ap.getInputImage();
        strategy = ap.getSolverStrategy().toString();
        xStartPoint = ap.getStartX();
        yStartPoint = ap.getStartY();
        xUpperLeftPoint = ap.getX0();
        yUpperLeftPoint = ap.getY0();
        xLowerRightPoint = ap.getX1();
        yLowerRightPoint = ap.getY1();
        key = hill + "_" +
                strategy + "_" +
                xStartPoint + "_" +
                yStartPoint + "_" +
                xUpperLeftPoint + "_" +
                yUpperLeftPoint + "_" +
                xLowerRightPoint + "_" +
                yLowerRightPoint;
    }

    @DynamoDBHashKey(attributeName = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @DynamoDBAttribute(attributeName = "instructions")
    public Long getInstructions() {
        return instructions;
    }

    public void setInstructions(Long instructions) {
        this.instructions = instructions;
    }

    @DynamoDBAttribute(attributeName = "hill")
    public String getHill() {
        return hill;
    }

    public void setHill(String hill) {
        this.hill = hill;
    }

    @DynamoDBAttribute(attributeName = "strategy")
    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
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
        return "ClimbRequestCostEntry{" +
                "instructions=" + instructions +
                ", hill='" + hill + '\'' +
                ", strategy='" + strategy + '\'' +
                ", xStartPoint=" + xStartPoint +
                ", yStartPoint=" + yStartPoint +
                ", xUpperLeftPoint=" + xUpperLeftPoint +
                ", yUpperLeftPoint=" + yUpperLeftPoint +
                ", xLowerRightPoint=" + xLowerRightPoint +
                ", yLowerRightPoint=" + yLowerRightPoint +
                '}';
    }
}
