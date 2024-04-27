package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.*;

@JacksonXmlRootElement(localName = "properties")
public class ODProperties {
    private Map<String, ODProperty> properties = new HashMap<>();
    public Map<String, ODProperty> getProperties() {
        return properties;
    }
    @JsonSetter(value = "property")
    public void addProperty(ODProperty property) {
        if (Objects.isNull(property)) { return; }
        properties.put(property.getName(), property);
    }

    private List<ODPropScript> scripts = new ArrayList<>();
    public List<ODPropScript> getScripts() {
        return scripts;
    }
    @JsonSetter(value = "script")
    public void addScript(ODPropScript script) {
        scripts.add(script);
    }

    private List<OD_IF> rawIfs = null;
    public List<OD_IF> getIfs() { return rawIfs; }
    @JsonSetter(value = "if")
    public void addIf(OD_IF rawIf) {
        if (Objects.isNull(rawIfs)) { rawIfs = new ArrayList<>();}
        rawIfs.add(rawIf);
    }

    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("properties", fieldName, fieldValue);
    }
}
