package codemirror.eclipse.swt.builder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CMBuilder {

	private final boolean runMode;
	private final String baseURL;
	private final List<String> scripts;
	private List<String> styles;

	private final Options options;
	private Map<String, String> commands;

	public CMBuilder(Mode mode, String baseURL, boolean runMode) {
		this.baseURL = baseURL;
		this.runMode = runMode;
		this.options = createOptions();
		this.styles = new ArrayList<String>();
		this.scripts = new ArrayList<String>();

		// <!-- CodeMirror -->
		installCodeMirror();
		installSearchAddon();

		// <!-- CodeMirror-Extension -->
		addScript("scripts/codemirror-extension/addon/selection/fullscreen.js");
		addStyle("scripts/codemirror-extension/addon/selection/fullscreen.css");

		// <!-- SWT Browser - CodeMirror -->
		addScript("scripts/eclipse/cm-eclipse.js");

		getOptions().setMode(mode);
		getOptions().setStyleActiveLine(true);
		getOptions().setLineWrapping(true);
		getOptions().setShowCursorWhenSelecting(true);

	}

	private void installCodeMirror() {
		addScript("scripts/codemirror/lib/codemirror.js");
		addStyle("scripts/codemirror/lib/codemirror.css");
	}

	protected void installSearchAddon() {
		addScript("scripts/codemirror/addon/dialog/dialog.js");
		addScript("scripts/codemirror/addon/search/searchcursor.js");
		addScript("scripts/codemirror/addon/search/search.js");
		addStyle("scripts/codemirror/addon/dialog/dialog.css");
	}

	protected void installHint(boolean withContextInfo, boolean withTemplates) {
		// <!-- CodeMirror -->
		addScript("scripts/codemirror/addon/hint/show-hint.js");
		// <!-- CodeMirror-Extension -->
		addStyle("scripts/codemirror-extension/addon/hint/show-hint-eclipse.css");
		if (withContextInfo) {
			installContextInfoHint();
		}
		if (withTemplates) {
			installTemplatesHint();
		}
	}

	protected void installContextInfoHint() {
		addScript("scripts/codemirror-extension/addon/hint/show-context-info.js");
		addStyle("scripts/codemirror-extension/addon/hint/show-context-info.css");
	}

	protected void installTemplatesHint() {
		addScript("scripts/codemirror/addon/runmode/runmode.js");

		addScript("scripts/codemirror-extension/addon/hint/templates-hint.js");
		addStyle("scripts/codemirror-extension/addon/hint/templates-hint.css");
	}

	protected Options createOptions() {
		return new Options(this);
	}

	public void write(Writer writer) throws IOException {
		writeBefore(writer);
		writeScript(writer);
		writeAfter(writer);
	}

	private void writeScript(Writer writer) throws IOException {
		write(writer, "<script type=\"text/javascript\" >");
		writeCommands(writer);
		write(writer,
				"var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), ");
		options.write(writer);
		write(writer, ");");
		write(writer, "</script>");
	}

	protected void writeCommands(Writer writer) throws IOException {
		if (commands != null) {
			String name = null;
			String script = null;
			for (Map.Entry<String, String> command : commands.entrySet()) {
				name = command.getKey();
				script = command.getValue();
				write(writer, "CodeMirror.commands.", false);
				write(writer, name, false);
				write(writer, "= function(cm) {", false);
				write(writer, script);
				write(writer, "}");
			}
		}
	}

	protected void writeBefore(Writer writer) throws IOException {
		write(writer, "<!doctype html>", false);
		// write(writer,
		// "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 401 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		write(writer, "<html>");
		writeHtmlHead(writer);
		write(writer, "<body onload=\"CMEclipse.loaded()\" >");
		if (runMode) {
			write(writer, "<pre id=\"code\" name=\"code\" ></pre>");
		} else {
			write(writer, "<form>");
			write(writer, "<textarea id=\"code\" name=\"code\" ></textarea>");
			write(writer, "</form>");
		}
	}

	private void writeHtmlHead(Writer writer) throws IOException {
		write(writer, "<head>");
		// tells Internet Explorer to display a webpage in IE9 mode, if
		// possible.
		write(writer,
				"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EDGE\" />");
		// <base href="http://www.myhomeurl.com/"/>
		/*
		 * write(writer, "<base href=\"", false); write(writer, baseURL, false);
		 * write(writer, "/", false); write(writer, "\" />");
		 */
		// <link rel="stylesheet" href="scripts/codemirror/lib/codemirror.css">
		for (String href : styles) {
			insertStyle(writer, href);
		}
		// <script src="scripts/codemirror/lib/codemirror.js"></script>
		for (String src : scripts) {
			insertScript(writer, src);
		}

		write(writer, "<style type=\"text/css\" >");
		write(writer,
				" .CodeMirror-activeline-background {background: #e8f2ff !important;}\n");
		write(writer,
				" .CodeMirror-matchingbracket{outline:1px solid grey; color:black !important;}");
		write(writer, "</style>");

		write(writer, "</head>");
	}

	private void insertStyle(Writer writer, String href) throws IOException {
		write(writer, "<link rel=\"stylesheet\" href=\"");
		write(writer, baseURL, false);
		write(writer, "/", false);
		write(writer, href, false);
		write(writer, "\">", false);
	}

	private void insertScript(Writer writer, String src) throws IOException {
		write(writer, "<script src=\"");
		write(writer, baseURL, false);
		write(writer, "/", false);
		write(writer, src, false);
		write(writer, "\"> </script>", false);
	}

	protected void writeAfter(Writer writer) throws IOException {
		write(writer, "</body>");
		write(writer, "</html>");
	}

	public void write(Writer writer, String content) throws IOException {
		write(writer, content, true);
	}

	public void write(Writer writer, String content, boolean lineBreak)
			throws IOException {
		if (lineBreak) {
			writer.write("\n");
		}
		writer.write(content);
	}

	public String getText() {
		StringWriter writer = new StringWriter();
		try {
			write(writer);
		} catch (IOException e) {
			// Should never thrown
		}
		System.err.println(writer);
		return writer.toString();
	}

	public void addStyle(String style) {
		if (!styles.contains(style)) {
			styles.add(style);
		}
	}

	public void addScript(String script) {
		if (!scripts.contains(script)) {
			scripts.add(script);
		}
	}

	public Options getOptions() {
		return options;
	}

	public void setCommand(String command, String script) {
		if (commands == null) {
			commands = new LinkedHashMap<String, String>();
		}
		commands.put(command, script);
	}
}