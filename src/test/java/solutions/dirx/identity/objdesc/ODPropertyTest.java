package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODProperty;
import solutions.dirx.identity.objdesc.propertyRules.FixedValueRule;
import solutions.dirx.identity.objdesc.propertyRules.PropertyRule;
import solutions.dirx.identity.objdesc.propertyRules.ReferenceRule;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.*;

public class ODPropertyTest {

    private static XMLInputFactory f;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        f = XMLInputFactory.newFactory();
    }

    private static XMLStreamReader prepareXmlReader(String xml) {
        XMLStreamReader sr = null;
        try {
            sr = f.createXMLStreamReader(new StringReader(xml));
            assertEquals("No START_ELEMENT at beginning", XMLStreamConstants.START_ELEMENT, sr.next());
            return sr;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void instanceFromXml() {
        final String xml = """
	<property name="cn" 
	    type="java.lang.String"
	    />
        """;

        XMLStreamReader sr;
        ODProperty prop;
        try {
            sr = prepareXmlReader(xml);
            prop = ODProperty.instanceFromXml(sr);
            sr.close();
            assertNotNull("Property is null", prop);
            assertEquals("Wrong property name", "cn", prop.getName());
            assertEquals("Wrong property type", "java.lang.String", prop.getType());
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readPropertyFromString_useXmlMapper() {
        final String xml = """
            <property name="cn" 
                 type="test.NotExistingClass"
                 incremental="true"
                 multivalue="true"
                 readonly="true"
                 mandatory="true"
                 defaultvalue="$(script:CountryFromLocation)"
                 clearOnMasterRemoval="true"
                 uniqueIn="$(./../../..)"
                 subsetdelimiter=" "
                 dependsOn="dxrLocationLink, ou"
                 applyDependsOn="save"
                 master="dxrSponsor.dxrUID"
                 />
            """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODProperty prop = xmlMapper.readValue(xml, ODProperty.class);
            assertEquals("Wrong property name", "cn", prop.getName());
            assertEquals("Wrong property type", "test.NotExistingClass", prop.getType());
            assertTrue("Expect incremental true", prop.isIncremental());
            assertTrue("Expect multi-value true", prop.isMultiValue());
            assertTrue("Expect readonly true", prop.isReadonly());
            assertTrue("Expect mandatory true", prop.isMandatory());
            assertEquals("Wrong default value", "$(script:CountryFromLocation)", prop.getDefaultValue());
            assertTrue("Expect clearOnMasterRemoval true", prop.isClearOnMasterRemoval());
            assertEquals("Wrong uniqueIn", "$(./../../..)", prop.getUniqueIn());
            assertEquals("Wrong subsetdelimiter", " ", prop.getSubsetdelimiter());
            assertEquals("Wrong 2 dependsOn attributes", 2, prop.getDependsOnAttributes().size());
            assertTrue("Expect dxrLocationLink as dependsOn", prop.getDependsOnAttributes().contains("dxrLocationLink"));
            assertTrue("Expect ou as dependsOn", prop.getDependsOnAttributes().contains("ou"));
            assertEquals("Wrong applyDependsOn", "save", prop.getApplyDependsOn());
            assertEquals("Wrong master", "dxrSponsor.dxrUID", prop.getMaster());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readProperty_namingRules() {
        final String xml = """
                		<property name="cn"
                			mandatory="true"
                			type="java.lang.String"
                			dependsOn="dxrName,employeeNumber" >
                			<extension>
                				<namingRule>
                					<reference baseObject="SvcTSAccount" attribute="dxrName" />
                					<fixedValue value="valA"/>
                				</namingRule>
                				<namingRule>
                					<reference baseObject="SvcUser" attribute="sn" />
                					<fixedValue value=" "/>
                					<reference baseObject="SvcUser" attribute="givenName" />
                				</namingRule>
                			</extension>
                		</property>
            """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODProperty prop = xmlMapper.readValue(xml, ODProperty.class);

            assertNotNull("No naming rules", prop.getNamingRules());

            assertEquals("Wrong number of naming rules", 2, prop.getNamingRules().size());
            List<PropertyRule> propRules = prop.getNamingRules().get(0).propertyRules;
            assertTrue("expect reference rule: "+propRules.get(0).getClass().getSimpleName(), propRules.get(0) instanceof ReferenceRule);
            assertTrue("expect fixedValue rule: "+propRules.get(1).getClass().getSimpleName(), propRules.get(1) instanceof FixedValueRule);
            propRules = prop.getNamingRules().get(1).propertyRules;
            assertTrue("expect reference rule: "+propRules.get(0).getClass().getSimpleName(), propRules.get(0) instanceof ReferenceRule);
            assertTrue("expect fixedValue rule: "+propRules.get(1).getClass().getSimpleName(), propRules.get(1) instanceof FixedValueRule);
            assertTrue("expect reference rule as 3rd rule: "+propRules.get(2).getClass().getSimpleName(), propRules.get(2) instanceof ReferenceRule);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**

     */

    @Test
    public void readProperty_script() {
        final String xml = """
     <property name="description"
        type="java.lang.String"
        defaultvalue="$(script:AccountDescription)"
        >
        <script name="AccountDescription"
            return="description"
            reference="storage://DirXmetaRole/cn=AccountDescription.js...,$(rootDN)"
            />
     </property>
            """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODProperty prop = xmlMapper.readValue(xml, ODProperty.class);

            assertEquals("wrong default value", "$(script:AccountDescription)", prop.getDefaultValue());

            assertNotNull("expect script", prop.getScript());
            assertEquals("wrong script name", "AccountDescription", prop.getScript().getName());
            assertEquals("wrong script return", "description", prop.getScript().getReturnAttribute());
            assertEquals("wrong script reference", "storage://DirXmetaRole/cn=AccountDescription.js...,$(rootDN)", prop.getScript().getReference());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
