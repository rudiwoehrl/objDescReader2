package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODMapping;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ODMappingTest {

    @Test
    public void readMapping() {
        final String xml = """
	<mapping>
		<attribute objectclass="{any}dxrUser"/>
		<attribute objectclass="~dxrPersona"/>
        <attribute dn="*,$(./../../..)" />
	</mapping>
            """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODMapping mapping = xmlMapper.readValue(xml, ODMapping.class);

            List<String> attributeValues = mapping.rawMappings.get("objectclass");
            assertEquals("Expect 2 objectclass values", 2, attributeValues.size());
            assertTrue("Expect {any}dxrUser", attributeValues.contains("{any}dxrUser"));
            assertTrue("Expect ~dxrPersona", attributeValues.contains("~dxrPersona"));

            attributeValues = mapping.rawMappings.get("dn");
            assertEquals("Expect 1 dn value", 1, attributeValues.size());
            assertTrue("Expect *,$(./../../..)", attributeValues.contains("*,$(./../../..)"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
