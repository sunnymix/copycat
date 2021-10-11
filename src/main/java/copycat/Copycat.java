package copycat;

/**
 * Copycat application entrance
 */
public class Copycat {
    public static void main(String[] args) {
        _info();
    }

    private static void _info() {
        StringBuilder i = new StringBuilder();
        i.append("OVERVIEW: copycat copy everything\n\n");
        i.append("USAGE: copycat [[option] [option]] url...\n\n");
        i.append("OPTIONS:\n");
        i.append("  -T=<value>,\n");
        i.append("  --protocol=<value>  Set communication protocol, defaults to be 'http'...\n");
        System.out.println(i);
    }
}
