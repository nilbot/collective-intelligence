import java.util.List;

public class EvalResult {

  private final double rmse;
  private final long runtime;
  private final List<EvalObj> evalObjs;

  public EvalResult(double rmse, long runtime, List<EvalObj> evalObjs) {
    this.evalObjs = evalObjs;
    this.rmse = rmse;
    this.runtime = runtime;
  }

  public List<EvalObj> getEvalObjs() {
    return evalObjs;
  }

  public long getRuntime() {
    return runtime;
  }

  public double getRmse() {
    return rmse;
  }
}
