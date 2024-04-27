package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODBaseObject;
import solutions.dirx.identity.objdesc.objects.OD_IF;

import static org.junit.Assert.*;

public class OD_IFTest {

    @Test
    public void readID_body_definition() {
        // see cn=QueryContainer.xml,cn=Object Descriptions,cn=Configuration,cn=My-Company
        final String xml = """
            <body>
                <if var="roleLC" value="true">
                	<definition
                        class="siemens.dxr.service.nodes.SvcQueryContainer"
                        namingattribute="cn"
                        parents="UserRoot,dxrPermissionContainer"
                        />
                <else/>
                	<definition
                        class="siemens.dxr.service.nodes.SvcQueryContainer"
                        namingattribute="cn" 
                        parents="UserRoot,dxrPermissionContainer"
                        />
                </if>

                <mapping>
                	<attribute objectclass="{any}dxrContainer"/>
                	<attribute dxrType="{any}dxrQueryContainer"/>
                </mapping>
                <properties>
                     <property name="objectclass" defaultvalue="{dxrContainer,top}"/>
                </properties>
	            <extension>
		           <loadAttributes>
			          <children name="{cn,sn,givenname,description,dxrState}" />
		           </loadAttributes>
	            </extension>
            </body>
        """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODBaseObject odObject = xmlMapper.readValue(xml, ODBaseObject.class);
            OD_IF rawIf = odObject.rawIf;
            assertNotNull("if is null", rawIf);

            assertEquals("Wrong var", "roleLC", rawIf.getVar());
            assertEquals("Wrong value", "true", rawIf.getValue());
            assertNotNull("definition is null", rawIf.getDefinition());
            assertNotNull("else is null", rawIf.getElse());
            assertNotNull("else.definition is null", rawIf.getElse().getDefinition());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void readID_body_import() {
        // see cn=UserCommon.xml,cn=Object Descriptions,cn=Customer Extensions,cn=Configuration,cn=My-Company
        final String xml = """
            <body>
                <variables>
                    <var name="usePhoneNumberAssignment" value="false" />
                </variables>
	            <import file="storage://DirXmetaRole/cn=User.xml,cn=Object Descriptions,cn=Configuration,$(rootDN)?content=dxrObjDesc" />
	            <createFrom destinationType="dxrPersona" objectDescriptionName="PersonaFromUser"/>
	            <createFrom destinationType="dxrFunctionalUser" objectDescriptionName="FunctionalUserFromUser"/>
                <if var="usePhoneNumberAssignment" value="true">
		            <import file="storage://DirXmetaRole/cn=UserPhoneNumberAssignment.xml,cn=Object Descriptions,cn=Customer Extensions,cn=Configuration,$(rootDN)?content=dxrObjDesc" />
                </if>
            </body>
        """;

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODBaseObject odObject = xmlMapper.readValue(xml, ODBaseObject.class);
            OD_IF rawIf = odObject.rawIf;
            assertNotNull("if is null", rawIf);

            assertEquals("Wrong var", "usePhoneNumberAssignment", rawIf.getVar());
            assertEquals("Wrong value", "true", rawIf.getValue());
            assertNotNull("imports is null", rawIf.getImports());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
