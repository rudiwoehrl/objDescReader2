package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.*;
import java.util.stream.Collectors;

@JacksonXmlRootElement(localName = "script")
public class ODPropScript {
    @JacksonXmlProperty(isAttribute = true)
    private String name = null;
    public String getName() { return name; }

    @JacksonXmlProperty(localName = "return", isAttribute = true)
    private String returnAttribute = null;
    public String getReturnAttribute() { return returnAttribute; }

    @JacksonXmlProperty(isAttribute = true)
    private String reference = null;
    public String getReference() { return reference; }


    @JacksonXmlProperty(isAttribute = true)
    public String dependsOn = null;
    private Set<String> dependsOnAttributes = null;
    public Set<String> getDependsOnAttributes() {
        if (Objects.isNull(dependsOnAttributes)) {
            dependsOnAttributes = new HashSet<>();
            if (Objects.nonNull(dependsOn)) {
                dependsOnAttributes = Arrays.stream(dependsOn.split(","))
                        .map(v -> v.trim()).collect(Collectors.toSet());
            }
        }
        return dependsOnAttributes;
    }

    @JacksonXmlCData
    @JacksonXmlText
    private String script = null;
    public  String getScriptBody() { return script; }

    // TODO cn=Email.xml,cn=Object Descriptions arg1

    public Map<String, String> arguments = new HashMap<>();

    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        if (fieldName.startsWith("arg")) {
            arguments.put(fieldName, fieldValue);
        } else {
            UnrecognizedFieldCollector.addUnrecognizedField("script", fieldName, fieldValue);
        }
    }

}
