package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ODGlobalProperties {
    public Map<String, ODProperty> properties = new HashMap<>();

    @JsonSetter(value = "property")
    public void addProperty(ODProperty property) {
        if (Objects.isNull(property)) { return; }
        properties.put(property.getName(), property);
    }
}
