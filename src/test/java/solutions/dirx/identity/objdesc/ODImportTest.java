package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.ODImport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ODImportTest {
    @Test
    public void readImportFromString() {
        String xml = """
        <import file="abc"/>
        """;

        XmlMapper xmlMapper = XmlMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        try {
            ODImport odImp = xmlMapper.readValue(xml, ODImport.class);
            assertEquals("abc", odImp.file);

            xml = """
                    <importx file="abc"/>""";
            odImp = xmlMapper.readValue(xml, ODImport.class);
            assertEquals("abc", odImp.file);

            xml = """
                    <import filex="abc"/>""";
            odImp = xmlMapper.readValue(xml, ODImport.class);
            assertNull(odImp.file);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getDNFromImport() {
        String rootDN = "cn=My-Company";
        String url = "storage://DirXmetaRole/cn=Variables.xml,$(rootDN)?content=dxrObjDesc";
        String expected = "cn=Variables.xml,cn=My-Company";
        ODImport imp = new ODImport();
        imp.file = url;
        assertEquals(expected, imp.getDNFromImport(rootDN));

        imp.file = "//DirXmetaRole/cn=Variables.xml,$(rootDN)?content=dxrObjDesc";
        assertNull("Expect null when missing schema", imp.getDNFromImport(rootDN));

        imp.file = "storage://DirXmetaRolex/cn=Variables.xml,$(rootDN)?content=dxrObjDesc";
        assertNull("Expect null with wrong host", imp.getDNFromImport(rootDN));

        imp.file = "storage://DirXmetaRole/cn=Variables.xml,cn=My-Company?content=dxrObjDesc";
        assertNull("Expect null with missing root placeholder", imp.getDNFromImport(rootDN));

        imp.file = "//DirXmetaRole/cn=Variables.xml,$(rootDN)?something else";
        assertNull("Expect null when missing schema", imp.getDNFromImport(rootDN));
    }
}
