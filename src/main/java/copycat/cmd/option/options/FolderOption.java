package copycat.cmd.option.options;

import copycat.cmd.option.NameValueOption;

public class FolderOption extends NameValueOption {
    public static final String NAME = "folder";
    public static final String ALIAS = "F";

    public FolderOption(String value) {
        _name = NAME;
        _alias = ALIAS;
        _desc = "Set folder id";
        _value = value;
    }
}
