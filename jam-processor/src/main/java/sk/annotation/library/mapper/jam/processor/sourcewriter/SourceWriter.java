package sk.annotation.library.mapper.jam.processor.sourcewriter;

import org.apache.commons.lang.StringUtils;

import java.io.PrintWriter;

public class SourceWriter implements AutoCloseable {
	final public PrintWriter pw;

	public SourceWriter(PrintWriter pw) {
		this.pw = pw;
	}

	private int level = 0;
	private String newLine = "\n";
	public void updateNewLine() {
		if (level < 0) level = 0;
		if (level == 0) newLine = "\n";
		newLine = "\n" + StringUtils.repeat("\t", level);
	}
	public void levelSpaceUp() {
		level++;
		updateNewLine();
	}
	public void levelSpaceDown() {
		level--;
		updateNewLine();
	}
	public void printNewLine() {
		pw.print(newLine);
	}
	public void print(String x) {
		if (!newLine.equalsIgnoreCase("\n")) {
			x =StringUtils.replace(x, "\n", newLine);
		}
		pw.print(x);
	}

	public void flush() {
		pw.flush();
	}

	@Override
	public void close() throws Exception {
		pw.close();
	}
}
