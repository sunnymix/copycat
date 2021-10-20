package copycat.cmd.option;

import copycat.cmd.option.options.DirOption;
import copycat.cmd.option.options.FolderOption;

import java.util.List;

public class Options {
    public static String getDir(List<Option> options) {
        return _getStr(options, DirOption.NAME);
    }

    public static String getCatalog(List<Option> options) {
        return _getStr(options, FolderOption.NAME);
    }

    public static String _getStr(List<Option> options, String name) {
        String str = null;
        Option option = _get(options, name);
        if (option != null && option.value() != null && !option.value().isBlank()) {
            str = option.value().trim();
        }
        return str;
    }

    private static Option _get(List<Option> options, String name) {
        for (Option option : options) {
            if (option.name().equals(name)) {
                return option;
            }
        }
        return null;
    }
}
