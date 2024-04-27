package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODConfiguration;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;
import test.utils.LdapDomainUtils;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ODConfigurationTest {

    @Test
    public void readConfigFromLdap() {
        final String entryDN = "cn=Config.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        try {
            LdapDomainUtils ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

            ODConfiguration odConf = xmlMapper.readValue(sObjdesc[0], ODConfiguration.class);

            assertNotNull("Expect at least 1 object", odConf.getObjects());
            assertEquals("Expect 1 object in objects", 1, odConf.getObjects().objects.size());
            assertFalse("Expect at least 1 import in configuration", odConf.getImports().isEmpty());
            assertFalse("Expect at least 1 if in objects", odConf.getObjects().getIfs().isEmpty());

            if (!UnrecognizedFieldCollector.unrecognizedFieldMap.isEmpty()) {
                UnrecognizedFieldCollector.printUnrecognizedFields();
                fail("Unrecognized fields");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readConfigFromString() {
        final String xml = """
<configuration>
        <import file="storage://DirXmetaRole/cn=Variables.xml,cn=Object Descriptions,cn=Configuration,$(rootDN)?content=dxrObjDesc"/>
        <objects>
            <import file="storage://DirXmetaRole/cn=MessageProperty.xml,cn=Object Descriptions,cn=Configuration,$(rootDN)?content=dxrObjDesc"/>
            <object name="UserRoot" class="siemens.dxr.service.nodes.SvcUserCont">
                <properties>
                    <property name="objectclass" defaultvalue="{dxrContainer,top}"/>
                </properties>
            </object>
            <if var="roleLC" value="true">
                <import file="storage://DirXmetaRole/cn=Role.xml,$(rootDN)?content=dxrObjDesc"/>
            </if>
            <import file="storage://DirXmetaRole/cn=main.xml,$(rootDN)?content=dxrObjDesc"/>
			<if var="supportPersonas" value="true">
				<import file="storage://DirXmetaRole/cn=PersonaFromPersona.xml,$(rootDN)?content=dxrObjDesc"/>
			</if>
        </objects>
</configuration>
        """;

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ODConfiguration odConf = xmlMapper.readValue(xml, ODConfiguration.class);

            assertEquals("Expect 1 object in objects", 1, odConf.getObjects().objects.size());
            assertEquals("Expect 1 import in configuration", 1, odConf.getImports().size());
            assertTrue("Wrong import value", odConf.getImports().get(0).file.contains("Variables.xml"));
            assertEquals("Expect 2 ifs in objects", 2, odConf.getObjects().getIfs().size());
            assertEquals("Expect 2 imports in objects", 2, odConf.getObjects().getImports().size());

            if (!UnrecognizedFieldCollector.unrecognizedFieldMap.isEmpty()) {
                UnrecognizedFieldCollector.printUnrecognizedFields();
                fail("Unrecognized fields");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
