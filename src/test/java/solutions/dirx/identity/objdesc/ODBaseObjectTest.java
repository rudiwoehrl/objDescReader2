package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODBaseObject;

import static org.junit.Assert.*;

public class ODBaseObjectTest {
    @Test
    public void readGlobalProperties() {
        // see cn=GlobalProperties.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company.
        String xml = """
<globalproperties>
	<property name="$this" label="this" readonly="true" multivalue="false" visible="false" mandatory="true" method=""/>
	<property name="$displayname" label="Name" readonly="false" multivalue="false" visible="true" mandatory="true" method=""/>
</globalproperties>
        """;

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        try {
            ODBaseObject odObject = xmlMapper.readValue(xml, ODBaseObject.class);
            assertNotNull("baseObject null", odObject);
            assertNotNull("properties null", odObject.odProperties);
            assertEquals(2, odObject.odProperties.getProperties().size());
            assertNotNull("$this property null", odObject.odProperties.getProperties().get("$this"));
            assertNotNull("$displayname property null", odObject.odProperties.getProperties().get("$displayname"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
