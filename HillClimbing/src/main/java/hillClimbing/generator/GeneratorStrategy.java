package hillClimbing.generator;

public interface GeneratorStrategy {

    void generate(Generator gen);

    @Override
    String toString();
}
