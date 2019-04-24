package hillClimbing.solver;

public abstract class AbstractSolverStrategy implements SolverStrategy {
    public abstract void solve(final Solver sol);

    protected String name;
    @Override
    public String toString() {
        return this.name;
    }
}
