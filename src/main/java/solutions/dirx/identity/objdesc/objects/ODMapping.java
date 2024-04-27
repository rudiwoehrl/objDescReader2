package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.*;

@JacksonXmlRootElement(localName = "mapping")
public class ODMapping {
    public Map<String, List<String>> rawMappings = new HashMap<>();

    @JsonSetter(value = "attribute")
    public void addAttribute(ODMappingAttribute attr) {
        List<String> existingMapping = rawMappings.get(attr.attributeName);
        if (Objects.isNull(existingMapping)) {
            existingMapping = new ArrayList<>();
            rawMappings.put(attr.attributeName, existingMapping);
        }
        existingMapping.add(attr.attributeValue);
    }
}
