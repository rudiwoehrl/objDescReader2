package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

@JacksonXmlRootElement(localName = "definition")
public class ODDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(ODDefinition.class);
    public static final String TAG = "definition";

    @JacksonXmlProperty(localName = "class", isAttribute = true)
    private String className = null;
    public String getClassName() { return className; }

    @JacksonXmlProperty(isAttribute = true)
    public String name = null;

    @JacksonXmlProperty(isAttribute = true)
    private String namingattribute = null;
    public String getNamingattribute() { return namingattribute; }

    @JacksonXmlProperty(isAttribute = true)
    private String displayattribute = null;
    public String getDisplayattribute() { return displayattribute; }

    @JacksonXmlProperty(isAttribute = true)
    public String accesscontrol = null;

    @JacksonXmlProperty(isAttribute = true)
    public String targetsystem = null;

    // see https://reflectoring.io/jackson/ for @JsonAnySetter
    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("definition", fieldName, fieldValue);
    }
}
