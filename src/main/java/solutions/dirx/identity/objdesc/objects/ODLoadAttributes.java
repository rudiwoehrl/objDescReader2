package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JacksonXmlRootElement(localName = "loadAttributes")
@JsonDeserialize(using = ODLoadAttributes.LoadAttributesDeserializer.class)
//@JsonDeserialize(converter = ObjDescLoadAttributes.LoadAttributesConverter.class)
public class ODLoadAttributes {
    private static final Logger LOG = LoggerFactory.getLogger(ODLoadAttributes.class);
    public static final String TAG = "loadAttributes";

    private Map<String, String> attributes = new HashMap<>();
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }

    //    @JsonAnySetter
//    public void allSetter(String fieldName, String fieldValue) {
//        System.out.print("ObjDescLoadAttributes: fieldName: " + fieldName + ", fieldValue: " + fieldValue + "\n");
//        attributes.put(fieldName, fieldValue);
//    }
//    public void setAttributes(String elemName, List<Map<String, String>> attributes) {
//        System.out.print("ObjDescLoadAttributes: elemName: " + elemName + ", attributes: " + attributes + "\n");
//    }

    // ----

    public static class LoadAttributesDeserializer extends StdDeserializer<ODLoadAttributes> {
        public LoadAttributesDeserializer() {
            super(ODLoadAttributes.class);
        }

        @Override
        public ODLoadAttributes deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JacksonException {
            ODLoadAttributes objDescLoadAttributes = new ODLoadAttributes();

            Map<String, String> attributes = new HashMap<>();
            objDescLoadAttributes.setAttributes(attributes);

//        System.out.println("currentName: " + jp.currentName()); // gleich wie nextFieldName
            JsonNode loadAttrNode = jp.getCodec().readTree(jp);
//        System.out.println("attrNode.fieldNames: ");
            // alle fields name unter loadAttributes
//        System.out.println("attrNode.findValuesAsText: " + attrNode.findValuesAsText("name"));
//        System.out.println("attrNode.findPath: " +  loadAttrNode.findPath("name"));
//        loadAttrNode.fields().forEachRemaining(System.out::println);
            loadAttrNode.properties().stream()
                    .forEach(e -> {
//                            System.out.println(e.getKey() + ": " + e.getValue());
                        attributes.put(e.getKey(), e.getValue().findPath("name").textValue());
                    });

            return objDescLoadAttributes;
        }

        // ----

        public static class LoadAttributesConverter extends StdConverter<Map<String, String>, ODLoadAttributes> {
            @Override
            public ODLoadAttributes convert(Map<String, String> content) {
                //return content.get("_content");
                ODLoadAttributes objDescLoadAttributes = new ODLoadAttributes();
                System.out.println("LoadAttributesConverter: content: " + content);
                // results in: content: {roles=, permissions=, groups=, assignedgroups=, accounts=, fn_accounts=, exclude=}

                return objDescLoadAttributes;
            }
        }
    }
}
