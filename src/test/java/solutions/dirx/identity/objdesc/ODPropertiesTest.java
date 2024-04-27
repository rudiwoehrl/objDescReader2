package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODPropScript;
import solutions.dirx.identity.objdesc.objects.ODProperties;
import solutions.dirx.identity.objdesc.objects.ODProperty;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ODPropertiesTest {

    @Test
    public void readProperties() {
        // for the if see cn=UserCommon.xml,cn=Object Descriptions,cn=Customer Extensions,cn=Configuration,cn=My-Company
        final String xml = """
	<properties>
		<property name="objectclass"
			defaultvalue="{dxrTargetSystemAccount,inetOrgPerson,organizationalPerson,person,top}" />
		<property name="employeeNumber"
			label="Employee Number"
			type="java.lang.String"
			master="dxrUserLink.employeeNumber"
			incremental="false"/>
		<script name="formatTelephoneNumber"
			return="telephoneNumber"
			reference="storage://DirXmetaRole/cn=formatTelephoneNumber.js,cn=JavaScripts,$(TStypeDN)?content=dxrObjDesc"/>
		<if var="usePhoneNumberAssignment" value="true">
			<import file="storage://DirXmetaRole/cn=UserPhoneNumberAssignment.xml,cn=Object Descriptions,cn=Customer Extensions,cn=Configuration,$(rootDN)?content=dxrObjDesc" />
		</if>
	</properties>
	""";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        try {
            ODProperties odProps = xmlMapper.readValue(xml, ODProperties.class);

            Map<String, ODProperty> props = odProps.getProperties();
            assertNotNull(props);
            assertEquals("Wrong number of properties", 2, props.size());

            List<ODPropScript> scripts = odProps.getScripts();
            assertNotNull(scripts);
            assertEquals("Wrong number of scripts", 1, scripts.size());

            assertNotNull("No ifs", odProps.getIfs());
            assertEquals("Wrong number of ifs", 1, odProps.getIfs().size());
            assertEquals("expect 1 import in if", 1, odProps.getIfs().get(0).getImports().size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
