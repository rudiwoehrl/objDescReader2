package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.*;
import solutions.dirx.identity.objdesc.propertyRules.ReferenceRule;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;
import test.utils.LdapDomainUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.*;

import static org.junit.Assert.*;

public class ODExperiments {
    private static LdapDomainUtils ldapUtils = null;

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
    public void readObjectWithXmlMapper() {
        final String xml = """
            <object name="SvcUserToRole" class="siemens.dxr.service.nodes.SvcUserToRole">
                <import file="storage://DirXmetaRole/cn=dxrProject.xml,cn=BusinessObjects,cn=Object Descriptions,cn=Configuration,$(rootDN)?content=dxrObjDesc" />
                <mapping>
                    <attribute objectclass="{any}dxrUserToRole"/>
                </mapping>
                <properties>
                    <property name="cn" 
                        type="java.lang.String"
                        />
                    <property name="dxrAssignFrom" 
                        type="java.lang.String"
                        />
                </properties>
            </object>
            """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODObject odObject = xmlMapper.readValue(xml, ODObject.class);
            assertEquals("Wrong property name", "SvcUserToRole", odObject.getName());
            assertEquals("Wrong property class", "siemens.dxr.service.nodes.SvcUserToRole", odObject.getClassName());
            List<ODProperty> props = odObject.getProperties();
            assertNotNull(props);
            assertEquals("Wrong number of properties", 2, props.size());
//            List<ObjDescMappingAttribute> mapAttrs = odObject.getMappingAttributes();
//            assertNotNull(mapAttrs);
//            assertEquals("Wrong number of mapping attributes", 1, mapAttrs.size());
//            assertEquals("Wrong mapping attribute", "{any}dxrUserToRole", mapAttrs.get(0).getObjectclass());
            assertNotNull("null import", odObject.rawImport);
            assertTrue("wrong import start", odObject.rawImport.file.startsWith("storage://DirXmetaRole"));
            odObject.doPostProcessing();
            assertEquals("Wrong type of property cn", "java.lang.String", odObject.getPropertyDescription("cn").getType());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readAccountFromString() {
        final String xml = """
                <body>
                	<import
                		file="storage://DirXmetaRole/cn=TSAccount.xml,cn=Object Descriptions,cn=default,cn=TargetSystems,cn=Configuration,$(rootDN)?content=dxrObjDesc" />

                	<variables>
                		<var name="TSdefaultDN"
                			value="cn=Default,cn=TargetSystems,cn=Configuration,$(rootDN)" />
                		<var name="TStypeDN"
                			value="cn=Unify Office,cn=TargetSystems,cn=Configuration,$(rootDN)" />
                	</variables>

                	<mapping>
                		<attribute objectclass="{any}dxrTargetSystemAccount" />
                		<attribute dn="*,$(./../../..)" />
                	</mapping>
                	
	                <extension>
		                <loadAttributes>
			                <groups name="{cn,description,dxrState}" />
		                </loadAttributes>
	                </extension>
                	
                	<properties>
                		<property name="objectclass"
                			defaultvalue="{dxrTargetSystemAccount,inetOrgPerson,organizationalPerson,person,top}" />

                		<property name="employeeNumber"
                			label="Employee Number"
                			type="java.lang.String"
                			master="dxrUserLink.employeeNumber"
                			incremental="false"/>

                		<property name="$telephoneNumber"
                			type="java.lang.String"
                			dependsOn="telephoneNumber"
                			defaultvalue="$(script:formatTelephoneNumber)"/>
                		
                		<property name="cn"
                			mandatory="true"
                			type="java.lang.String"
                			dependsOn="dxrName,employeeNumber" >
                			<extension>
                				<namingRule>
                					<reference baseObject="SvcTSAccount" attribute="dxrName" />
                				</namingRule>
                				<namingRule>
                					<reference baseObject="SvcUser" attribute="cn" />
                				</namingRule>
                			</extension>
                		</property>
                			
                		<script name="formatTelephoneNumber"
                			return="telephoneNumber"
                			reference="storage://DirXmetaRole/cn=formatTelephoneNumber.js,cn=JavaScripts,$(TStypeDN)?content=dxrObjDesc"/>
                	</properties>
                </body>
                """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODBaseObject odObject = xmlMapper.readValue(xml, ODBaseObject.class);
//            odObject.doPostProcessing();

            assertEquals("storage://DirXmetaRole/cn=TSAccount.xml,cn=Object Descriptions,cn=default,cn=TargetSystems,cn=Configuration,$(rootDN)?content=dxrObjDesc",
                    odObject.rawImport.file);

            ODVariables vars = odObject.variables;
            assertNotNull(vars);
            assertEquals("wrong variable TSdefaultDN",
                    "cn=Default,cn=TargetSystems,cn=Configuration,$(rootDN)", vars.variablesMap.get("TSdefaultDN"));
            assertEquals("wrong variable TStypeDN",
                    "cn=Unify Office,cn=TargetSystems,cn=Configuration,$(rootDN)", vars.variablesMap.get("TStypeDN"));

            assertNotNull(odObject.mapping);
            Map<String, List<String>> rawMappings = odObject.mapping.rawMappings;
            assertEquals("Wrong number of mapping attributes", 2, rawMappings.size());
            assertTrue("wrong value for mapping attribute objectclass", rawMappings.get("objectclass").contains("{any}dxrTargetSystemAccount"));

//            odObject.getExtension().getLoadAttributes().getAttributes().forEach((k, v) -> {System.out.println(k+": "+v);});
            assertNotNull("Expect loadAttributes in extension", odObject.getExtension().loadAttributes);
            String loadAttr = odObject.getExtension().loadAttributes.getAttributes().get("groups");
            assertNotNull("Expect loadAttribute groups", loadAttr);
            assertTrue("loadattribute groups should contain dxrState", loadAttr.contains("dxrState}"));

            ODProperties odProperties = odObject.odProperties;
            assertNotNull(odProperties);

            ODProperty employeeNrProp = odProperties.getProperties().get("employeeNumber");
            assertNotNull("No property employeeNumber", employeeNrProp);
            assertEquals("Wrong master in employeeNumber", "dxrUserLink.employeeNumber", employeeNrProp.getMaster());

            ODProperty telNrProp = odProperties.getProperties().get("$telephoneNumber");
            assertNotNull("No property $telephoneNumber", telNrProp);
            assertEquals("Wrong dependson in $telephoneNumber", "telephoneNumber", telNrProp.dependsOn);

            ODProperty cnProp = odProperties.getProperties().get("cn");
            assertNotNull("No property cn", cnProp);
            assertNotNull("Null naming rules in cn", cnProp.getNamingRules());
            assertEquals("expect 2 naming rules for cn", 2, cnProp.getNamingRules().size());

            List<ODPropScript> propScripts = odProperties.getScripts();
            assertTrue("Not enough scripts", Objects.nonNull(propScripts) && !propScripts.isEmpty());

            ODPropScript scriptfmtTelNr = propScripts.stream()
                    .filter(s -> "formatTelephoneNumber".equalsIgnoreCase(s.getName()))
                    .findFirst().get();
            assertNotNull("No script formatTelephoneNumber", scriptfmtTelNr);
            assertEquals("Wrong return attribute", "telephoneNumber", scriptfmtTelNr.getReturnAttribute());
            assertEquals("storage://DirXmetaRole/cn=formatTelephoneNumber.js,cn=JavaScripts,$(TStypeDN)?content=dxrObjDesc",
                    scriptfmtTelNr.getReference());

            UnrecognizedFieldCollector.printUnrecognizedFields();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readAccountODFromLdap() {
        final String entryDN = "cn=TSAccount.xml,cn=Object Descriptions,cn=Default,cn=TargetSystems,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

            ODBody odObject = xmlMapper.readValue(sObjdesc[0], ODBody.class);
//            odObject.doPostProcessing();

            ODDefinition definition = odObject.getDefinition();
            assertNotNull(definition);
            assertEquals("Wrong targetsystem", "$(./../../..)", definition.targetsystem);

            Map<String, List<String>> rawMappings = odObject.mapping.rawMappings;
            assertEquals("Wrong number of mapping attributes", 2, rawMappings.size());
            assertTrue("wrong value for mapping attribute objectclass", rawMappings.get("objectclass").contains("{any}dxrTargetSystemAccount)"));

            ODVariables vars = odObject.variables;
            assertNotNull(vars);
            assertTrue("TSdefaultDN not in variables",
                    "cn=Default,cn=TargetSystems,cn=Configuration,$(rootDN)".equalsIgnoreCase(vars.variablesMap.get("TSdefaultDN")));

            List<ODProperty> properties = odObject.getProperties();
            assertNotNull(properties);
            ODProperty userLinkProp = properties.stream()
                    .filter(p -> "dxrUserLink".equalsIgnoreCase(p.getName()))
                    .findFirst()
                    .get();
            assertNotNull("No userLink property", userLinkProp);
            assertNotNull("Null naming rules in userlink", userLinkProp.getNamingRules());
            assertTrue("At least 3 naming rule for userlink", userLinkProp.getNamingRules().size() > 2);

            ODProperty scriptProp = properties.stream()
                    .filter(p -> "$dxrPwdChangeState".equalsIgnoreCase(p.getName()))
                    .findFirst()
                    .get();
            assertNotNull("No $dxrPwdChangeState property", scriptProp);
            assertEquals("Wrong script name", "getProperty", scriptProp.getScript().getName());
            assertEquals("Wrong return attribute", "value", scriptProp.getScript().getReturnAttribute());
            assertNull("Script reference not null", scriptProp.getScript().getReference());
            assertNotNull("Script body null", scriptProp.getScript().getScriptBody());

            ODPropScript objectScript = odObject.script;
            assertNotNull("object has no script", objectScript.getScriptBody());
            assertEquals("Wrong script name", "onShow", objectScript.getName());

            ODProperty useruidProp = properties.stream()
                    .filter(p -> "dxrUserUid".equalsIgnoreCase(p.getName()))
                    .findFirst()
                    .get();
            assertNotNull("No userUid property", useruidProp);
            assertEquals("Wrong useruid dependson", "dxrUserLink", useruidProp.dependsOn);

            ODProperty employeeNrProp = properties.stream()
                    .filter(p -> "employeeNumber".equalsIgnoreCase(p.getName()))
                    .findFirst()
                    .get();
            assertNotNull("No employeeNumber property", employeeNrProp);
            assertEquals("Wrong master in employeeNumber", "dxrUserLink", employeeNrProp.getMaster());

            UnrecognizedFieldCollector.printUnrecognizedFields();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readObject_containsDefinition() {
        final String xml = """
            <object name="accessPolicy">
                <definition
                    class="siemens.dxr.service.nodes.AccessPolicy"
                    namingattribute="cn"
                    displayattribute="cn"
                    />
                <mapping>
                    <attribute objectclass="{any}dxrAccessPolicy"/>
                </mapping>
                <properties>
                    <property name="cn" 
                        type="java.lang.String"
                        />
                    <property name="dxrType"/>
                </properties>
            </object>
            """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODObject odObject = xmlMapper.readValue(xml, ODObject.class);
            assertEquals("Wrong property name", "accessPolicy", odObject.getName());
            assertEquals("Wrong property class", "siemens.dxr.service.nodes.AccessPolicy", odObject.getClassName());
            List<ODProperty> props = odObject.getProperties();
            assertNotNull(props);
            assertEquals("Wrong number of properties", 2, props.size());
            assertEquals("Wrong type", "java.lang.String", props.get(1).getType());
//            List<ObjDescMappingAttribute> mapAttrs = odObject.getMappingAttributes();
//            assertNotNull(mapAttrs);
//            assertEquals("Wrong number of mapping attributes", 1, mapAttrs.size());
//            assertEquals("Wrong mapping attribute", "{any}dxrAccessPolicy", mapAttrs.get(0).getObjectclass());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an object description with "<object ...>" tag from LDAP
     * mapping to BaseObjDescObject.
     */
    @Test
    public void readODFromLdap() {
        final String entryDN = "cn=AccessPolicy.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

//            ObjDescObject odObject = xmlMapper.readValue(sObjdesc[0], ObjDescObject.class);
            ODBaseObject odObject = xmlMapper.readValue(sObjdesc[0], ODBaseObject.class);
            odObject.doPostProcessing();

            assertEquals("Wrong odName", "accessPolicy", odObject.getName());
            assertEquals("Wrong class", "siemens.dxr.service.nodes.SvcAccessPolicy", odObject.getClassName());
            List<ODProperty> props = odObject.getProperties();
            assertNotNull(props);
            assertEquals("Wrong type of property cn", "java.lang.String", odObject.getPropertyDescription("cn").getType());
//            List<ObjDescMappingAttribute> mapAttrs = odObject.getMappingAttributes();
//            assertNotNull(mapAttrs);
//            assertEquals("Wrong number of mapping attributes", 1, mapAttrs.size());
//            assertEquals("Wrong mapping attribute", "{any}dxrAccessPolicy", mapAttrs.get(0).getObjectclass());

            UnrecognizedFieldCollector.printUnrecognizedFields();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an object description with "<body ...>" tag from LDAP
     * mapping to ObjDescBody.
     */
    @Test
    public void readBodyFromLdap() {
        final String entryDN = "cn=User.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

            ODBody odObject = xmlMapper.readValue(sObjdesc[0], ODBody.class);
            odObject.doPostProcessing();

            assertEquals("Wrong class", "siemens.dxr.service.nodes.SvcUser", odObject.getClassName());
            List<ODProperty> props = odObject.getProperties();
            assertNotNull(props);
            assertEquals("Wrong type of property owner", "siemens.dxm.storage.StorageObject", odObject.getPropertyDescription("owner").getType());
            ODProperty objClassProp = odObject.getPropertyDescription("objectclass");
            assertNotNull("objclass property null", objClassProp);
            String objectclass = objClassProp.getDefaultValue();
            assertTrue("Need dxrUser", objectclass.contains("dxrUser"));

            Map<String, List<String>> rawMappings = odObject.mapping.rawMappings;
            assertTrue("Not enough mapping attributes", rawMappings.size() > 4);

//            odObject.getExtension().getLoadAttributes().getAttributes().forEach((k, v) -> {System.out.println(k+": "+v);});
            String rolesName = odObject.getExtension().loadAttributes.getAttributes().get("roles");
            assertNotNull(rolesName);
            assertTrue(rolesName.endsWith(",dxrRoleParamLink}"));

            UnrecognizedFieldCollector.printUnrecognizedFields();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an object description with "<body ...>" tag from LDAP
     * mapping to BaseObjDescObject.
     * This fails.
     */
    @Test
    public void readBodyFromLdapMapToBaseObject() {
        final String entryDN = "cn=User.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

            ODBody odObject = (ODBody) xmlMapper.readValue(sObjdesc[0], ODBaseObject.class);
            fail("Expect ClassCastException");
        } catch (ClassCastException expected) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readODAccountWithScriptFromLdap() {
        final String entryDN = "cn=TSAccount.xml,cn=Object Descriptions,cn=Unify Office,cn=TargetSystems,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

            ODBody odObject = xmlMapper.readValue(sObjdesc[0], ODBody.class);

            ODProperties odProperties = odObject.odProperties;
            assertNotNull(odProperties);

            ODProperty empNrProp = odProperties.getProperties().get("employeeNumber");
            assertNotNull(empNrProp);
            assertEquals("dxrUserLink.employeeNumber", empNrProp.getMaster());

            ODProperty cnProp = odProperties.getProperties().get("cn");
            assertNotNull(cnProp);
            assertNotNull(cnProp.getNamingRules());
            assertTrue(cnProp.getNamingRules().size() == 4);
            Object ruleObj = cnProp.getNamingRules().get(0);
            assertTrue("wrong rule: "+ruleObj.getClass().getName(), ruleObj instanceof ReferenceRule);

            List<ODPropScript> scripts = odObject.odProperties.getScripts();
            assertNotNull(scripts);
            assertEquals("Wrong number of scripts", 1, scripts.size());
            assertEquals("formatTelephoneNumber", scripts.get(0).getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readFailedODFromLdap() {
        final String entryDN = "cn=TSAccount.xml,cn=Object Descriptions,cn=Medico,cn=TargetSystems,cn=Configuration,cn=My-Company";

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            String[] sObjdesc = ldapUtils.readAttribute(entryDN, "dxrObjDesc");

            ODBody odObject = xmlMapper.readValue(sObjdesc[0], ODBody.class);
//            odObject.doPostProcessing();

            System.out.println("Unrecognized fields:");
            UnrecognizedFieldCollector.printUnrecognizedFields();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readAllObjectDescriptions() {
        final String searchBase = "cn=Object Descriptions,cn=Customer Extensions,cn=Configuration,cn=My-Company";
        final String domain = "cn=My-Company";
        final Set<String> entries2Ignore = Set.of(
                "Config.xml", "Variables.xml", "GlobalProperties.xml", "newTS.xml"
        );
        // maybe we need their info later
        final String filter = "(&(objectclass=dxrObjectDesc)(dxrType=ODML)(!(cn=Config.xml))(!(cn=Variables.xml)))";

        Set<String> objDescNamesNotXml = new HashSet<>();
        Set<String> objDescNamesFailed = new HashSet<>();
        long startTime = System.currentTimeMillis();
        int numFoundObjDescs = 0;
        int numEntriesWithUnrecognizedFields = 0;
        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        try {
            ldapUtils = new LdapDomainUtils();
            List<Map<String, String>> objDescs = ldapUtils.searchSubtree(domain, filter, new String[] {"cn","dxrObjDesc"}, false);
            for (Map<String, String> objDesc : objDescs) {
                String odName = objDesc.get("cn");
                if (entries2Ignore.contains(odName)) { continue; }
                if (odName.endsWith(".properties")) {
                    objDescNamesNotXml.add(odName);
                    continue;}
                if (!odName.endsWith(".xml")) {
                    objDescNamesNotXml.add(odName); }

                String odXml = objDesc.get("dxrObjDesc");
                if (odXml.contains("<wizard")) { continue; }

                try {
                    ODBaseObject odObject = xmlMapper.readValue(odXml, ODBaseObject.class);
//                odObject.doPostProcessing();
                    numFoundObjDescs++;
//                    System.out.println("Object: " + odName + ", class: " + odObject.getClassName());
                    if (! UnrecognizedFieldCollector.unrecognizedFieldMap.isEmpty()) {
                        numEntriesWithUnrecognizedFields++;
                        System.out.println("Unrecognized fields in entry :" + objDesc.get("dn"));
                        UnrecognizedFieldCollector.printUnrecognizedFields();
                        UnrecognizedFieldCollector.unrecognizedFieldMap.clear();
                    }
                } catch (JsonProcessingException e) {
//                    System.err.printf("%s parsing object description %s: %s\n", e.getClass().getName(), objDesc.get("dn"), e.getMessage());
                    objDescNamesFailed.add(objDesc.get("dn"));
                }
            }
            System.out.printf("Found %d object descriptions in %d ms\n", numFoundObjDescs, System.currentTimeMillis()- startTime);
            System.out.printf("%d object descriptions not ending with xml: %s\n", objDescNamesNotXml.size(), objDescNamesNotXml);
            System.out.printf("%d object descriptions failed to parse: %s\n", objDescNamesFailed.size(), objDescNamesFailed);
            System.out.printf("%d object descriptions with unrecognized fields\n", numEntriesWithUnrecognizedFields);
//            UnrecognizedFieldCollector.printUnrecognizedFields();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helps to find samples of object description entries with certain properties.
     */
    @Test
    public void searchODsByFilter() {
        final String searchBase = "cn=My-Company";
        final String specificFilter = "(dxrObjDesc=*master*)";
        final String filterTemplate = "(&(objectclass=dxrObjectDesc)(dxrType=ODML)(!(cn=Config.xml))%s)";
        final String filter = String.format(filterTemplate, specificFilter);

        try {
            ldapUtils = new LdapDomainUtils();
            List<Map<String, String>> objDescs = ldapUtils.searchSubtree(searchBase, filter, new String[] {"cn","dxrObjDesc"}, false);
            for (Map<String, String> objDesc : objDescs) {
                System.out.println("Found " + objDesc.get("dn"));

//                String odXml = objDesc.get("dxrObjDesc");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
