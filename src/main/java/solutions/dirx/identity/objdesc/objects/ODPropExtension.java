package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import solutions.dirx.identity.objdesc.propertyRules.PropertyNamingRule;

import java.util.List;

@JacksonXmlRootElement(localName = "extension")
public class ODPropExtension {
    public List<PropertyNamingRule> namingRules = null;
}
