package codemirror.eclipse.ui.preferences;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;

import codemirror.eclipse.swt.browser.BrowserFactory;
import codemirror.eclipse.swt.browser.WebBrowserType;
import codemirror.eclipse.swt.builder.CMBuilder;
import codemirror.eclipse.swt.builder.CMBuilderRegistry;
import codemirror.eclipse.swt.builder.Mode;
import codemirror.eclipse.swt.builder.Theme;
import codemirror.eclipse.swt.builder.addon.fold.FoldType;
import codemirror.eclipse.swt.builder.addon.search.MatchHighlighterOption;
import codemirror.eclipse.swt.builder.addon.search.ShowTokenType;
import codemirror.eclipse.swt.utils.StringUtils;

public class PreferenceHelper {

	public static final String THEME_PREFERENCE_NAME = "theme";
	public static final String HOVER_ENABLED_PREFERENCE_NAME = "hoverEnabled";
	public static final String HOVER_DELAY_PREFERENCE_NAME = "hoverDelay";
	public static final String BROWSER_PREFERENCE_NAME = "browser";

	public static void initialize(Mode mode, IPreferenceStore store) {
		CMBuilder builder = CMBuilderRegistry.getInstance().getBuilder(mode);
		initialize(builder, store);
	}

	public static void initialize(CMBuilder builder, IPreferenceStore store) {
		// Initialize the theme builder from the store
		updateTheme(builder, store);
		// Initialize the fold builder from the store
		updateFold(builder, store);
		// Initialize the hover builder from the store
		updateHover(builder, store);
		// Initialize the Mark Occurrences builder from the store
		updateMarkOccurrences(builder, store);
		// Initialize browser type
		updateDefaultBrowserType(store);
	}

	// ------------------------ Theme

	/**
	 * Set the given theme as default in the store.
	 * 
	 * @param store
	 *            the preference store to update.
	 * @param theme
	 *            the default theme to use.
	 */
	public static void setDefaultTheme(IPreferenceStore store, Theme theme) {
		store.setDefault(THEME_PREFERENCE_NAME, theme.getName());
	}

	/**
	 * Get the theme from the preference store.
	 * 
	 * @param store
	 *            the preference store
	 * @return
	 */
	public static Theme getTheme(IPreferenceStore store) {
		String name = store.getString(THEME_PREFERENCE_NAME);
		if (name != null) {
			return Theme.getTheme(name);
		}
		return null;
	}

	/**
	 * Update the builder with the theme defined in the given store.
	 * 
	 * @param builder
	 * @param store
	 */
	public static void updateTheme(CMBuilder builder, IPreferenceStore store) {
		builder.getOptions().setTheme(getTheme(store));
	}

	// ------------------------ Folding

	/**
	 * 
	 * @param store
	 * @param enabled
	 */
	public static void setDefaultFoldType(IPreferenceStore store,
			FoldType foldType, boolean enabled) {
		store.setDefault(foldType.getName(), enabled);
	}

	public static void updateFold(CMBuilder builder, IPreferenceStore store) {
		Collection<FoldType> types = new ArrayList<FoldType>();
		FoldType[] supportedFoldTypes = builder.getSupportedFoldTypes();
		if (supportedFoldTypes != null) {
			for (FoldType foldType : supportedFoldTypes) {
				if (store.getBoolean(foldType.getName())) {
					types.add(foldType);
				}
			}
		}
		builder.getOptions().getFoldGutter()
				.setRangeFinder(types.toArray(FoldType.EMPTY));
	}

	// ------------------------ Hover

	/**
	 * 
	 * @param store
	 * @param enabled
	 */
	public static void setDefaultHoverEnabled(IPreferenceStore store,
			boolean enabled) {
		store.setDefault(PreferenceHelper.HOVER_ENABLED_PREFERENCE_NAME,
				enabled);
	}

	public static void setDefaultHoverDelay(IPreferenceStore store,
			Integer delay) {
		store.setDefault(PreferenceHelper.HOVER_DELAY_PREFERENCE_NAME, delay);
	}

	public static void updateHover(CMBuilder builder, IPreferenceStore store) {
		// enabled?
		boolean textHover = store
				.getBoolean(PreferenceHelper.HOVER_ENABLED_PREFERENCE_NAME);
		builder.getOptions().getTextHover(null).setTextHover(textHover);
		if (textHover) {
			// delay?
			int delay = store
					.getInt(PreferenceHelper.HOVER_DELAY_PREFERENCE_NAME);
			builder.getOptions().getTextHover(null)
					.setDelay(delay == 0 ? null : delay);
		}
		else {
			builder.getOptions().getTextHover(null).setTextHover(false);
			builder.getOptions().getTextHover(null).setDelay(null);
		}
	}

	// ------------------------ Web Browser type

	public static void updateDefaultBrowserType(IPreferenceStore store) {
		WebBrowserType browser = getWebBrowserType(store);
		if (browser != null) {
			BrowserFactory.setDefaultBrowserStyle(browser.getStyle());
		}
	}

	public static WebBrowserType getWebBrowserType(IPreferenceStore store) {
		String name = store.getString(BROWSER_PREFERENCE_NAME);
		if (StringUtils.isNotEmpty(name)) {
			return WebBrowserType.getWebBrowserType(name);
		}
		return null;
	}

	public static void setDefaultWebBrowserType(IPreferenceStore store,
			WebBrowserType browserType) {
		store.setDefault(BROWSER_PREFERENCE_NAME, browserType.name());
	}

	// --------------- Match Highlighter

	/**
	 * 
	 * @param store
	 * @param enabled
	 */
	public static void setDefaultMarkOccurrences(IPreferenceStore store,
			ShowTokenType showTokenTypes, boolean enabled) {
		store.setDefault(showTokenTypes.getType(), enabled);
	}

	public static void updateMarkOccurrences(CMBuilder builder,
			IPreferenceStore store) {
		Collection<ShowTokenType> types = new ArrayList<ShowTokenType>();
		for (ShowTokenType tokenType : ShowTokenType.getAll()) {
			if (store.getBoolean(tokenType.getType())) {
				types.add(tokenType);
			}
		}

		MatchHighlighterOption matchHighlighter = builder.getOptions()
				.getMatchHighlighter();
		matchHighlighter.setShowTokenTypes(types);

	}
}
