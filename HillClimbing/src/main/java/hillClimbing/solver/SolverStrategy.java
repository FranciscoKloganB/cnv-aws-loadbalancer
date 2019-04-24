package hillClimbing.solver;

public interface SolverStrategy {

    void solve(final Solver sol);

    @Override
    String toString();
}
