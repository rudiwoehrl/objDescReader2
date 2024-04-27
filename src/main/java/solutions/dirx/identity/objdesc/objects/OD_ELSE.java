package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JacksonXmlRootElement(localName = "else")
public class OD_ELSE {

    private List<ODImport> odImports = null;
    public List<ODImport> getImports() { return odImports; }
    public void addImport(ODImport odImport) {
        if (Objects.isNull(odImports)) { odImports = new ArrayList<>();}
        this.odImports.add(odImport);
    }

    private ODDefinition definition = null;
    public void setDefinition(ODDefinition definition) {
        this.definition = definition;
    }
    public ODDefinition getDefinition() {
        return definition;
    }

    // ----

    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("property", fieldName, fieldValue);
    }
}
