package solutions.dirx.identity.objdesc.util;

import java.util.*;

public class UnrecognizedFieldCollector {
    private static Map<String, Set<String>> ignoredFieldsMap = Map.of(
            "objdesc", Set.of("propertysheet", "actions", "action", "body", "icon", "candelete", "cancopy", "canrename",
                    "children", "version", "haschildren", "step", "superior", "parents", "label", "editor", "node",
                    "helpcontext", "illustrator", "filterobjectclass", "hasduedate", "sortpriority", "listconfig",
                    "xxicon"),
            "definition",
                Set.of("helpcontext", "displayname", "candelete", "hasduedate", "icon", "haschildren", "cancopy",
                        "canrename", "parents", "canmove", "node", "superior", "children", "label",
                        "filterobjectclass", "cluster",
                        "xxxxchildren"),
            "property",
                Set.of("editor", "visible", "label", "representation", "multivalueeditor", "editorparams", "readonly",
                        "tagprovider", "editable", "tags", "tagclass",
                        "accesscontrol", "xpath", "method",
                        "xxlabel", "xxmultivalueeditor", "xxeditorparams", "xxreadonly", "xxtagprovider", "xxeditable"),
            "object", Set.of(),
            "extension", Set.of("BPMN", "bpmn")
    );

    public static final Map<String, Map<String, Set<String>>> unrecognizedFieldMap = new HashMap<>();

    public static void addUnrecognizedField(
            String classname,
            String field,
            String value)
    {
        String classLc = classname.toLowerCase();
        String fieldLc = field.toLowerCase();
        Set<String> ignoredFields = ignoredFieldsMap.get(classLc);
        if (Objects.isNull(ignoredFields)) {
            System.err.printf("Unsupported class %s with field %s and value %s\n", classname, field, value);
            return;
        }
        if (ignoredFields.contains(fieldLc)) return;

        if (Objects.isNull(value) || value.isBlank()) {
            value = "emptyElement";
        }

        Map<String, Set<String>> fieldValues = unrecognizedFieldMap.get(classLc);
        if (Objects.isNull(fieldValues)) {
            fieldValues = new HashMap<>();
            unrecognizedFieldMap.put(classLc, fieldValues);
        }

        Set<String> values = fieldValues.get(fieldLc);
        if (Objects.isNull(values)) {
            values = new HashSet<>();
            fieldValues.put(fieldLc, values);
        }

        values.add(value);
    }

    public static void printUnrecognizedFields() {
//        System.out.println("Unrecognized fields:");
        unrecognizedFieldMap.forEach((classname, fieldValues) -> {
            fieldValues.forEach((fieldName, fieldValue) -> {
                System.out.printf("%s: %s - %s\n", classname, fieldName, fieldValue);
            });
        });
    }
}
