package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "createFrom")
public class ODCreateFrom {
    @JacksonXmlProperty(isAttribute = true)
    public String destinationType = null;

    @JacksonXmlProperty(isAttribute = true)
    public String objectDescriptionName = null;
}
