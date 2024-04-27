package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.*;

@JacksonXmlRootElement(localName = "if")
public class OD_IF {
    @JacksonXmlProperty(isAttribute = true)
    private String var = null;
    public String getVar() {
        return var;
    }

    @JacksonXmlProperty(isAttribute = true)
    private String value = null;
    public String getValue() {
        return value;
    }

    @JacksonXmlProperty(localName = "else")
    private OD_ELSE rawElse = null;
    public OD_ELSE getElse() {
        return rawElse;
    }

    private ODDefinition definition = null;
    public ODDefinition getDefinition() { return definition; }
    @JsonSetter(value = "definition")
    public void setDefinition(ODDefinition definition) {
        if (Objects.isNull(rawElse)) {
            this.definition = definition;
        } else {
            rawElse.setDefinition(definition);
        }
    }

    private List<ODImport> odImports = null;
    public List<ODImport> getImports() { return odImports; }
    @JsonSetter(value = "import")
    public void addImport(ODImport odImport) {
        if (Objects.isNull(rawElse)) {
            if (Objects.isNull(odImports)) { odImports = new ArrayList<>();}
            this.odImports.add(odImport);
        } else {
            rawElse.addImport(odImport);
        }
    }

    private Map<String, ODProperty> properties = new HashMap<>();
    public Map<String, ODProperty> getProperties() {
        return properties;
    }
    @JsonSetter(value = "property")
    public void addProperty(ODProperty property) {
        if (Objects.isNull(property)) { return; }
        properties.put(property.getName(), property);
    }

    // ----

    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("property", fieldName, fieldValue);
    }

}
