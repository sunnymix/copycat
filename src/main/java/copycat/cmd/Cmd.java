package copycat.cmd;

import copycat.cmd.option.Option;
import copycat.cmd.option.OptionFactory;
import copycat.cmd.runner.CmdRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cmd {
    public static final String OPTION_START = "--";
    public static final String OPTION_START_ALIAS = "-";

    public static final String OPTION_NAME_VALUE_GAP = "=";

    private final String _cmd;

    private String _action;

    private List<String> _optionStrs;

    private List<String> _urls;

    private List<Option> _options;

    public Cmd(String cmd) {
        _reset();
        _cmd = cmd;
        _split();
        _parseOptions();
    }

    public void run() {
        CmdRunner.run(_options, _urls);
    }

    @Override
    public String toString() {
        return "Cmd{" +
                "_cmd='" + _cmd + '\'' +
                ", _options=" + _options +
                ", _urls=" + _urls +
                '}';
    }

    public void print() {
        StringBuilder s = new StringBuilder();
        s.append("\n");
        s.append("====================").append("\n");
        s.append("Cmd").append("\n");
        s.append("Optional: ").append(_cmd).append("\n");
        s.append("Action: ").append(_action).append("\n");
        s.append("Urls: ").append(_urls).append("\n");
        s.append("Options: ").append("\n");
        for (Option option : _options) {
            s.append("    ").append(option.name()).append(": ").append(option.value()).append("\n");
        }
        s.append("====================").append("\n");
        System.out.println(s);
    }

    private void _reset() {
        _action = "";
        _options = new ArrayList<>();
        _urls = new ArrayList<>();
    }

    private void _split() {
        if (_cmd.isBlank()) {
            CmdException.EmptyCommand();
        }

        List<String> options = new ArrayList<>();
        String action = "", url = "";

        int matchStart = 0, matchCount = 0, firstOptionStart = -1, lastOptionStart = -1;

        String regexp = "(^| )(\\-|\\-\\-)[a-zA-Z]+[a-zA-Z]*";
        Pattern pattern = Pattern.compile(regexp);
        Matcher match = pattern.matcher(_cmd);

        while (match.find(matchStart)) {
            matchCount++;
            int optionStart = match.start();
            if (firstOptionStart == -1) {
                firstOptionStart = optionStart;
            }
            if (lastOptionStart >= 0) {
                options.add(_cmd.substring(lastOptionStart, optionStart).trim());
            }
            lastOptionStart = optionStart;
            matchStart = match.end();
        }

        if (firstOptionStart > 0) {
            action = _cmd.substring(0, firstOptionStart);
        }

        int urlStart = _cmd.lastIndexOf(" ");
        if (lastOptionStart >= 0 && options.size() < matchCount) {
            options.add(_cmd.substring(lastOptionStart, urlStart).trim());
        }

        url = _cmd.substring(urlStart).trim();

        _action = action;
        _optionStrs = options;
        _urls.add(url);
    }

    private void _parseOptions() {
        for (String optionStr : _optionStrs) {
            if (optionStr.isBlank()) {
                continue;
            }
            String[] nameAndValue = _splitOptionNameAndValue(optionStr);
            if (nameAndValue[0] == null) {
                break;
            } else {
                _options.add(_parseOption(nameAndValue));
            }
        }
    }

    private String[] _splitOptionNameAndValue(String optionStr) {
        String name = null, value = null;
        // name idx:
        int nameStartIdx = -1, nameEndIdx = -1, gapIdx = -1, valueStartIdx = -1;
        if (optionStr.startsWith(OPTION_START)) nameStartIdx = OPTION_START.length();
        else if (optionStr.startsWith(OPTION_START_ALIAS))
            nameStartIdx = OPTION_START_ALIAS.length();
        else return new String[]{null, null};
        // gap:
        gapIdx = optionStr.indexOf(OPTION_NAME_VALUE_GAP);
        if (gapIdx > nameStartIdx) {
            nameEndIdx = gapIdx;
            valueStartIdx = gapIdx + OPTION_NAME_VALUE_GAP.length();
        } else {
            nameEndIdx = optionStr.length();
        }
        // name:
        if (nameEndIdx > nameStartIdx) {
            name = optionStr.substring(nameStartIdx, nameEndIdx);
        }
        // value:
        if (valueStartIdx > 0) {
            value = optionStr.substring(valueStartIdx);
        }
        // name & value:
        return new String[]{name, value};
    }

    private Option _parseOption(String[] nameAndValue) {
        String name = nameAndValue[0], value = nameAndValue[1];
        return OptionFactory.create(name, value);
    }

    public static void main(String[] args) {
        String cmd = "--header=Cooke: token=x, user=s --dir=/a/b/c d/ http://domain.com/path";
        System.out.println(new Cmd(cmd).toString());
    }
}
