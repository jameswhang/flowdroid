package soot.jimple.infoflow.android.nu;
import soot.Value;


public class ConstantDefResult {
	public int id;
	public boolean isConstant;

	public ConstantDefResult(int id, boolean isConstant) {
		this.id = id;
		this.isConstant = isConstant;
	}
}
