package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JacksonXmlRootElement(localName = "configuration")
public class ODConfiguration {

    private List<ODImport> odImports = null;
    public List<ODImport> getImports() { return odImports; }
    @JsonSetter(value = "import")
    public void addImport(ODImport odImport) {
        if (Objects.isNull(odImports)) { odImports = new ArrayList<>();}
        odImports.add(odImport);
    }

    //@JacksonXmlElementWrapper(localName = "objects", useWrapping = true)
    @JsonProperty("objects")
    private ODObjects objects = null;
    public ODObjects getObjects() {
        return objects;
    }
    public void setObjects(ODObjects objects) {
        this.objects = objects;
    }

    // ----

    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("objdesc", fieldName, fieldValue);
    }
}
