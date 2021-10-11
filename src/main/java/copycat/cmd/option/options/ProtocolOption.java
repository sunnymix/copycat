package copycat.cmd.option.options;

import copycat.cmd.option.NameValueOption;

public class ProtocolOption extends NameValueOption {
    public ProtocolOption(String value) {
        _name = "protocol";
        _alias = "T";
        _desc = "Set communication protocol, defaults to be http...";
        _value = value;
    }
}
