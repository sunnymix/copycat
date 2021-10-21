package copycat.cmd.option.options;

import copycat.cmd.option.NameValueOption;

/**
 * TODO: rename to Catalog
 */
public class CatalogOption extends NameValueOption {
    public static final String NAME = "catalog";
    public static final String ALIAS = "C";

    public CatalogOption(String value) {
        _name = NAME;
        _alias = ALIAS;
        _desc = "Set catalog url";
        _value = value;
    }
}
