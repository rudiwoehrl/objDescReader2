package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODBaseObject;
import solutions.dirx.identity.objdesc.objects.ODPropScript;

import static org.junit.Assert.*;

public class ODPropScriptTest {
    @Test
    public void readScript() {
        // see cn=RoleParams.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company.
        String xml = """
    <script name="onEdit" dependsOn="dxrType,dxrSingleValue" >
        <![CDATA[
			var obj=scriptContext.getObject();
			obj.executeScript("onShow",null);
        ]]>
    </script>
        """;

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        try {
            ODPropScript odScript = xmlMapper.readValue(xml, ODPropScript.class);
            assertNotNull("script null", odScript);
            assertEquals("onEdit", odScript.getName());
            assertEquals("dxrType,dxrSingleValue", odScript.dependsOn);
            assertEquals("Wrong 2 dependsOn attributes", 2, odScript.getDependsOnAttributes().size());
            assertTrue("Expect dxrType as dependsOn", odScript.getDependsOnAttributes().contains("dxrType"));
            assertTrue("Expect dxrSingleValue as dependsOn", odScript.getDependsOnAttributes().contains("dxrSingleValue"));
            assertTrue("wrong script content", odScript.getScriptBody().contains("var obj=scriptContext.getObject();"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
