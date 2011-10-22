package logmx.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lightysoft.logmx.business.ParsedEntry;
import com.lightysoft.logmx.mgr.LogFileParser;

public class GlassfishLogFileParser extends LogFileParser {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
	    "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final static boolean DEBUG = false;

    private boolean recordStarted = false;
    private StringBuilder buffer = new StringBuilder();

    @Override
    protected void parseLine(String s) throws Exception {

	if (s == null) {
	    return;
	}

	if (DEBUG) {
	    ParsedEntry entry = createNewEntry();
	    entry.setMessage(s);
	    addEntry(entry);
	}

	// There is a BUG in LogMX:
	// When auto-refresh is enabled and encoding is wide char,
	// the first char will be lost.
	if (s.startsWith("[#|") || s.startsWith("#|")) {
	    recordStarted = true;
	    buffer.setLength(0);
	}
	if (recordStarted) {
	    buffer.append(s);
	    buffer.append("\n");
	    if (s.endsWith("#]")) {
		recordEntry(buffer.toString());
		recordStarted = false;
	    }
	}
    }

    private void recordEntry(String s) throws Exception {

	String[] ss = s.split("\\|");
	if (ss.length < 7) {
	    return;
	}
	ParsedEntry entry = createNewEntry();
	entry.setDate(ss[1]);
	entry.setEmitter(ss[4]);
	entry.setLevel(ss[2]);
	entry.setMessage(ss[6]);
	entry.setThread(ss[5]);
	addEntry(entry);
    }

    @Override
    public Date getRelativeEntryDate(ParsedEntry entry) throws Exception {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Date getAbsoluteEntryDate(ParsedEntry entry) throws Exception {
	return DATE_FORMAT.parse(entry.getDate());
    }

    @Override
    public String getParserName() {
	return "Glassfish Log File Parser";
    }

    @Override
    public String getSupportedFileType() {
	return "Glassfish Log File";
    }

}
