package soot.jimple.infoflow.android.nu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StaticValueService {

	private String[] UIEventActions = {
			"OnClick", "onLongClick", "onTouch", "onFocusChange", "onKey", "onTouch", "onKeyDown", "onKeyDown", "onKeyUp", "onTrackballEvent", "onTouchEvent", "onFocusChanged"
	};
	public StaticValueService() {}
	
	public HashSet<String> getUIEventActionsSet() {
		return new HashSet<String>(Arrays.asList(UIEventActions));
	}
}
