package codemirror.eclipse.swt.builder.addon.hyperlink;

import codemirror.eclipse.swt.builder.BaseOptions;
import codemirror.eclipse.swt.builder.CMBuilder;
import codemirror.eclipse.swt.builder.Function;

public class HyperlinkOption extends BaseOptions {

	public HyperlinkOption(CMBuilder builder) {
		super(builder);
	}

	public void setOpen(String open) {
		super.addOption("open", new Function(open));
	}
	
	@Override
	protected boolean isOneOption() {
		return false;
	}

}
