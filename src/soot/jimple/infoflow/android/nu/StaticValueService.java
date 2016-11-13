package soot.jimple.infoflow.android.nu;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StaticValueService {

	private String[] UIEventActions = {
			"onClick", "onLongClick", "onTouch", "onFocusChange", "onKey", "onTouch", "onKeyDown", "onKeyDown", "onKeyUp", "onTrackballEvent", "onTouchEvent", "onFocusChanged"
	};
	
	private String[] UIEventListenerMethods = {
			"setOnDragListener", "setOnClickListener", "setOnApplyWindowInsetsListener", "setOnCreateContextMenuListener", "setOnEditorActionListener", "setOnFocusChangeListener",	"setOnGenericMotionListener", "setOnHoverListener", "setOnKeyListener",	"setOnLongClickListener", "setOnSystemUiVisibilityChangeListener", "setOnTouchListener"
	};
	
	public StaticValueService() {}
	
	public HashSet<String> getUIEventActionsSet() {
		return new HashSet<String>(Arrays.asList(UIEventActions));
	}
	
	public HashSet<String> getUIEventListenerMethodsSet() {
		return new HashSet<String>(Arrays.asList(UIEventListenerMethods));
	}
}
