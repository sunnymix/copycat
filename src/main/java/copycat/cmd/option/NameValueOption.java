package copycat.cmd.option;

public class NameValueOption extends Option {
    protected String _name;

    protected String _alias;

    protected String _desc;

    protected String _value;

    @Override
    public String name() {
        return _name;
    }

    @Override
    public String alias() {
        return _alias;
    }

    @Override
    public String desc() {
        return _desc;
    }

    @Override
    public String value() {
        return _value;
    }
}
