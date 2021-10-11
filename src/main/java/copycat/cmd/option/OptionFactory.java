package copycat.cmd.option;

import copycat.cmd.CmdException;
import copycat.cmd.option.options.ProtocolOption;

import java.lang.reflect.Constructor;
import java.util.*;

public class OptionFactory {
    static {
        _options = new HashMap<>();
        add(new ProtocolOption(null));
    }

    private static final Map<String, Option> _options;

    public static List<Option> options() {
        Map<String, Option> map = new HashMap<>();
        for (Option opt : _options.values()) {
            if (!map.containsKey(opt.name())) map.put(opt.name(), opt);
        }
        return new ArrayList<>(map.values());
    }

    public static Map<String, Option> map() {
        return _options;
    }

    public static Set<String> names() {
        return _options.keySet();
    }

    public static boolean has(String name) {
        return _options.containsKey(name);
    }

    public static void add(Option option) {
        _options.put(option.name(), option);
        if (option.alias() != null && !option.alias().isBlank()) {
            _options.put(option.alias(), option);
        }
    }

    public static Option get(String name) {
        return _options.get(name);
    }

    public static Option create(String name, String value) {
        if (name == null) CmdException.EmptyOptionName();
        Option prototype = get(name), newInstance = null;
        if (prototype == null) newInstance = new UnknownOption(name, value);
        else {
            try {
                Constructor<? extends Option> constructor =
                        prototype.getClass().getConstructor(String.class);
                newInstance = constructor.newInstance(value);
            } catch (Throwable e) {
            }
        }
        if (newInstance == null) {
            CmdException.CannotCreateOption(prototype);
        }
        return newInstance;
    }
}
