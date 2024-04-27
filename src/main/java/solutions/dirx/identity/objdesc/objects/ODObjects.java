package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ODObjects {
    public List<ODBaseObject> objects = new ArrayList<>();
    @JsonSetter(value = "object")
    public void addObject(ODBaseObject object) {
        objects.add(object);
    }

    private List<ODImport> rawImports = null;
    public List<ODImport> getImports() { return rawImports; }
    @JsonSetter(value = "import")
    public void addImport(ODImport rawImport) {
        if (Objects.isNull(rawImports)) { rawImports = new ArrayList<>();}
        rawImports.add(rawImport);
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
        UnrecognizedFieldCollector.addUnrecognizedField("objects", fieldName, fieldValue);
    }
}
