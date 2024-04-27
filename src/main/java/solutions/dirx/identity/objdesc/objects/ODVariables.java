package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.HashMap;
import java.util.Map;

@JacksonXmlRootElement(localName = "variables")
public class ODVariables {

    public Map<String, String> variablesMap = new HashMap<>();

    @JsonSetter(value = "var")
    public void addVar(ODVariable var) {
        variablesMap.put(var.name, var.value);
    }
}
