package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import netscape.ldap.LDAPException;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODGlobalProperties;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;
import test.utils.LdapDomainUtils;

import static org.junit.Assert.*;

public class ODCollectorTest {
    @Test
    public void collectObjectDescriptions() {
        try {
            LdapDomainUtils ldapUtils = new LdapDomainUtils();
            long startTime = System.currentTimeMillis();

            ObjDescHolder holder = ObjDescHolder.getInstance();
            ODCollector odCollector = new ODCollector(ldapUtils.ldapConn, holder);
            odCollector.collectObjectDescriptions("cn=My-Company");

            System.out.printf("Found %d object descriptions in %d ms\n",
                    holder.objectDescriptions.size(), System.currentTimeMillis() - startTime);
            assertTrue("Expect at least 1 object description", holder.objectDescriptions.size() > 0);

            if (! UnrecognizedFieldCollector.unrecognizedFieldMap.isEmpty()) {
                System.out.printf("%d unrecognized fields:\n", UnrecognizedFieldCollector.unrecognizedFieldMap.size());
                UnrecognizedFieldCollector.printUnrecognizedFields();
            }
        } catch (LDAPException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void processAsVariables() {
        // see cn=Variables.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company.
        String xml = """
<variables>
    <var name="ppdir" value="$(confdir)/propertypages" />
    <var name="AuditObjectTypes" value="User,Role,Permission,Group,Account,Organization,OrganizationalUnit,Country,Location,Project,Context,CostUnit" />
</variables>
        """;

        try {
            LdapDomainUtils ldapUtils = new LdapDomainUtils();
            ObjDescHolder holder = ObjDescHolder.getInstance();
            ODCollector odCollector = new ODCollector(ldapUtils.ldapConn, holder);

            odCollector.processAsVariables(null, xml);
            assertEquals(2, holder.variables.size());
            assertEquals("$(confdir)/propertypages", holder.variables.get("ppdir"));
            assertNotNull(holder.variables.get("AuditObjectTypes"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
            ODGlobalProperties odProperties = xmlMapper.readValue(xml, ODGlobalProperties.class);
            assertNotNull("odProperties null", odProperties);
            assertEquals(2, odProperties.properties.size());
            assertNotNull("$this property null", odProperties.properties.get("$this"));
            assertNotNull("$displayname property null", odProperties.properties.get("$displayname"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
