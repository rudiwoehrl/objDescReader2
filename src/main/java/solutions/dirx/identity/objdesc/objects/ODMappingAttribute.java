package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@JacksonXmlRootElement(localName = "attribute")
@JsonDeserialize(using = ODMappingAttribute.AttributeDeserializer.class)
public class ODMappingAttribute {
    private static final Logger LOG = LoggerFactory.getLogger(ODMappingAttribute.class);
    public static final String TAG = "attribute";

    public String attributeName = null;
    public String attributeValue = null;

    // ----

    public static class AttributeDeserializer extends StdDeserializer<ODMappingAttribute> {
        public AttributeDeserializer() {
            super(ODMappingAttribute.class);
        }

        @Override
        public ODMappingAttribute deserialize(
                JsonParser jp,
                DeserializationContext ctx)
                throws IOException, JacksonException
        {
            ODMappingAttribute mappingAttr = new ODMappingAttribute();

            JsonNode attrNode = jp.getCodec().readTree(jp);

            attrNode.fields().forEachRemaining(e -> {
//                System.out.println(e.getKey() + ": " + e.getValue());
                mappingAttr.attributeName = e.getKey();
                mappingAttr.attributeValue = e.getValue().textValue();
                });

            return mappingAttr;
        }
    }

}
