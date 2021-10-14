package copycat.cmd.option;

import java.util.List;

public class Options {
    public static Option get(List<Option> options, String name) {
        for (Option option : options) {
            if (option.name().equals(name)) {
                return option;
            }
        }
        return null;
    }
}
