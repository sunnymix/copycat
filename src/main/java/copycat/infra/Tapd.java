package copycat.infra;

import copycat.cmd.option.Option;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static copycat.infra.Tapd.Pair.*;

public class Tapd {
    public static List<String> getChildren(String folderId, List<Option> options, String html) {
        List<String> childrenId = new ArrayList<>();
        System.out.printf("[FOLDER CHILDREN]%n  folderId: %s%n%n", folderId);
        int folderIdIdx = html.indexOf("baseZNodesForLoadMoreNodes");
        if (folderIdIdx > 0) {
            folderIdIdx = html.indexOf("id:\"" + folderId + "\"", folderIdIdx);
        }
        if (folderIdIdx > 0) {
            String json = _findParent(html, folderIdIdx);
            json = StringEscapeUtils.unescapeJava(json);
            String childrenStr = _findProperty("children", json, 0);
            System.out.printf("[CHILDREN STR]%n%s%n%n", childrenStr);
            List<String> children = _findObjs(childrenStr);
            System.out.printf("[CHILDREN LIST]%n%s%n%n", String.join("\n", children));
            childrenId = _findChildrenId(children);
            System.out.printf("[CHILDREN ID]%n%s%n%n", String.join("\n", childrenId));
        }
        return childrenId;
    }

    private static String _findParent(String json, int from) {
        String res = "";
        char objStartToken = '{', objEndToken = '}';
        int start = -1, end = -1;
        // find start:
        int cursor = from;
        while (cursor >= 0) {
            if (json.charAt(cursor) == objStartToken) {
                start = cursor;
                break;
            }
            cursor--;
        }
        if (start < 0) {
            return res;
        }
        // find end:
        json = json.substring(start);
        return _findPair(json, objStartToken, objEndToken);
    }

    private static List<String> _findObjs(String json) {
        List<String> objs = new ArrayList<>();
        while (!json.isEmpty()) {
            Range range = _findPairRange(json, '{', '}');
            if (range.ok()) {
                objs.add(json.substring(range.left, range.right));
                json = json.substring(range.right);
            } else {
                break;
            }
        }
        return objs;
    }

    private static List<String> _findChildrenId(List<String> children) {
        List<String> ids = new ArrayList<>();
        for (String child : children) {
            String id = _findProperty("id", child, 0).trim();
            if (StringUtils.isNotBlank(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    private static String _findPair(String json, char leftToken, char rightToken) {
        Range range = _findPairRange(json, leftToken, rightToken);
        if (range.ok()) {
            return json.substring(range.left, range.right);
        }
        return null;
    }

    private static Range _findPairRange(String json, char leftToken, char rightToken) {
        String res = null;
        Range range = new Range(-1, -1);
        int cursor = -1;
        range.left = cursor = json.indexOf(leftToken);
        if (range.left < 0) {
            return range;
        }
        Stack<Character> tokens = new Stack<>();
        while (cursor < json.length()) {
            char c = json.charAt(cursor);
            if (c == leftToken) {
                tokens.push(leftToken);
            }
            if (c == rightToken) {
                tokens.pop();
            }
            if (tokens.empty()) {
                range.right = cursor + 1;
                break;
            }
            cursor++;
        }
        return range;
    }

    private static String _findProperty(String name, String json, int from) {
        HashMap<String, String> properties = new HashMap<>();
        _findProperties(json, from, properties);
        return properties.get(name);
    }

    private static int _findPropertyNameStart(String json, int idx) {
        idx--;
        while (idx >= 0) {
            char c = json.charAt(idx);
            if (c == '{' || c == ' ' || c == ',' || c == '"' || c == '\'') {
                break;
            }
            idx--;
        }
        return idx + 1;
    }

    private static void _findProperties(String json, int from, HashMap<String, String> properties) {
        int cursor = from;
        int nameStart = -1, nameEnd = -1, valueEnd = -1;
        String name = null, value = null;
        while (cursor < json.length()) {
            char c = json.charAt(cursor);
            if (c == ':') {
                nameStart = _findPropertyNameStart(json, cursor);
                nameEnd = cursor;
                break;
            }
            cursor++;
        }
        if (nameStart >= 0 && nameEnd > nameStart) {
            name = json.substring(nameStart, nameEnd);
        }
        if (name != null) {
            json = json.substring(nameEnd).trim();
            if (json.charAt(0) == ':') {
                json = json.substring(1).trim();
                cursor = 0;
                Stack<Character> tokens = new Stack<>();
                boolean strStart = false;
                while (cursor < json.length()) {
                    char c = json.charAt(cursor);
                    if (c == STR.left || c == STR2.left) {
                        if (strStart) {
                            tokens.pop();
                            strStart = false;
                        } else {
                            tokens.push(c);
                            strStart = true;
                        }
                    } else {
                        if (c == OBJ.left || c == ARR.left) {
                            tokens.push(c);
                        }
                        if (c == OBJ.left || c == ARR.right) {
                            tokens.pop();
                        }
                    }
                    if (tokens.isEmpty() && (c == ',' || c == OBJ.right || c == ARR.right)) {
                        valueEnd = cursor;
                        break;
                    }
                    cursor++;
                }
                if (valueEnd > 0) {
                    value = json.substring(0, valueEnd);
                }
            }
        }
        if (StringUtils.isNotBlank(name) && value != null) {
            properties.put(name, value);
            _findProperties(json.substring(valueEnd + 1), 0, properties);
        }
    }

    static class Pair {
        char left, right;

        Pair(char left, char right) {
            this.left = left;
            this.right = right;

        }

        static Pair STR = new Pair('"', '"');
        static Pair STR2 = new Pair('\'', '\'');
        static Pair OBJ = new Pair('{', '}');
        static Pair ARR = new Pair('[', ']');
    }

    static class Range {
        int left = -1, right = -1;

        Range(int left, int right) {
            this.left = left;
            this.right = right;
        }

        boolean ok() {
            return this.left >= 0 && this.left < this.right;
        }
    }

    public static void main(String[] args) {
        System.out.printf("name: '%s'%n", _findProperty("name", "{name:\"Object\",link_direction:\"\",id:\"11528\",url:\"1152\", target:\"114350\",info:'[]'}", 0));

        String json1 = "{\"name\": \"a\", \"age\": 18, info:'[]', \"children\":[{\"name\": \"b\"}, {\"name\": \"c\"}]\"}";
        String json2 = "{\"name\": \"a\", \"age\": 18, info:'[]', \"children\":[{name: \"b\"}, {name: 'c'}]\"}";
        String json3 = "[{name: \"b\"}, {name: 'c'}]";
        System.out.printf("Objs: '%s'%n", _findObjs(json3));
        System.out.printf("Parent: '%s'%n", _findParent(json1, 2));
    }
}
