package copycat.cmd.option;

public abstract class Option {
    public abstract String name();

    public abstract String alias();

    public abstract String desc();

    public abstract String value();

    @Override
    public String toString() {
        return String.format("%s=%s", name(), value());
    }
}
