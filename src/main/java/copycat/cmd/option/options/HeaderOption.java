package copycat.cmd.option.options;

import copycat.cmd.option.NameValueOption;

import java.net.URLDecoder;

public class HeaderOption extends NameValueOption {
    private static final String GAP = ":";

    public HeaderOption(String value) {
        _name = "header";
        _alias = "H";
        _desc = "Set a header arg, eg. --header=Cookie:c1=v1;c2=v2";
        _value = _decode(value);
    }

    public String[] parseNameAndValue() {
        String[] nameAndValue = new String[]{null, null};
        if (_value != null && !_value.isBlank()) {
            int gapIdx = _value.indexOf(GAP);
            if (gapIdx > 0) {
                String name = _value.substring(0, gapIdx);
                String value = _value.substring(gapIdx + GAP.length());
                nameAndValue = new String[]{name, value};
            }
        }
        return nameAndValue;
    }

    private String _decode(String value) {
        String res = value;
        try {
            res = URLDecoder.decode(value, "UTF-8");
        } catch (Throwable e) {
        }
        return res;
    }
}
