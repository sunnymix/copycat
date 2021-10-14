package copycat.cmd.option.options;

import copycat.cmd.option.NameValueOption;

public class DirOption extends NameValueOption {
    public static final String NAME = "dir";

    public DirOption(String value) {
        _name = NAME;
        _alias = "D";
        _desc = "Set base directory";
        _value = value;
    }
}
