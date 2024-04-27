package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import solutions.dirx.identity.objdesc.util.ObjDescUtils;

public class ODImport {

    @JacksonXmlProperty(isAttribute = true)
    public String file = null;

    public String getDNFromImport(String rootDN) {
        return ObjDescUtils.getDNFromUrlString(rootDN, file);
    }
}
