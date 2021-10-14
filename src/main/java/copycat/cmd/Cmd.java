package copycat.cmd;

import copycat.cmd.option.Option;
import copycat.cmd.option.OptionFactory;
import copycat.cmd.runner.CmdRunner;

import java.util.ArrayList;
import java.util.List;

public class Cmd {
    public static final String NAME = "copycat";

    public static final String OPTIONS_GAP = " ";

    public static final String OPTION_START = "--";
    public static final String OPTION_START_ALIAS = "-";

    public static final String OPTION_NAME_VALUE_GAP = "=";

    private final String _cmd;

    private String[] _optionStrs;

    private List<Option> _options;

    private List<String> _urls;

    public Cmd(String cmd) {
        _reset();
        _cmd = cmd;
        _split();
        _parse();
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

    public void printInfo() {
        System.out.println(toString());
    }

    private void _reset() {
        _options = new ArrayList<>();
        _urls = new ArrayList<>();
    }

    private void _checkOptionStrs() {
        if (_optionStrs == null || _optionStrs.length == 0) CmdException.EmptyCommand();
    }

    private void _parse() {
        _checkOptionStrs();
        _parseName();
        _parseOptions();
        _parseUrls();
    }

    private void _split() {
        if (_cmd.isBlank()) CmdException.EmptyCommand();
        _optionStrs = _cmd.trim().split(OPTIONS_GAP);
        if (_optionStrs.length == 0) CmdException.EmptyCommand();
    }

    private void _parseName() {
        String nameStr = _optionStrs[0];
        if (!nameStr.equals(NAME)) CmdException.BadCommandName();
    }

    private void _parseOptions() {
        for (int i = 1; i < _optionStrs.length; i++) {
            String optionStr = _optionStrs[i];
            if (optionStr.isBlank()) continue;
            String[] nameAndValue = _splitOptionNameAndValue(optionStr);
            if (nameAndValue[0] == null) break;
            else _options.add(_parseOption(nameAndValue));
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

    private void _parseUrls() {
        int urlStartIdx = 1 + _options.size();
        for (int i = urlStartIdx; i < _optionStrs.length; i++) {
            _urls.add(_optionStrs[i]);
        }
        if (_urls.isEmpty()) CmdException.HasNoUrl();
    }

    public static void help() {
        StringBuilder i = new StringBuilder();
        i.append("OVERVIEW: Copycat, copy everything\n\n");
        i.append("USAGE: copycat [options] url...\n\n");
        i.append("OPTIONS:\n");
        for (Option option : OptionFactory.options()) {
            String value = option.value() == null ? "<value>" : option.value();
            i.append(String.format("  --%s|-%s=%s  %s\n", option.name(), option.alias(), value, option.desc()));
        }
        System.out.println(i);
    }
}
