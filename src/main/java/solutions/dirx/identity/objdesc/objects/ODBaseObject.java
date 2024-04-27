package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.*;

// see https://github.com/FasterXML/jackson-annotations for @JsonTypeInfo
// see https://stackoverflow.com/questions/48155494/jackson-xml-serialization-list-of-inherited-classes
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
// see https://stackoverflow.com/questions/68072295/jackson-serialization-xml-with-root-to-abstract-class?rq=3
//@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = AbstractObjDescObject.class)
// see https://www.baeldung.com/jackson-deduction-based-polymorphism
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = ObjDescObject.class, name = "object"),
//        @JsonSubTypes.Type(value = ObjDescBody.class, name = "body")
//})
public class ODBaseObject {

    @JacksonXmlProperty(isAttribute = true)
    private String name = null;
    public String getName() { return name; }

    @JacksonXmlProperty(localName = "class", isAttribute = true)
    private String className = null;
    public String getClassName() {
        if (Objects.nonNull(className)) { return className; }
        if (Objects.nonNull(definition)) { return definition.getClassName(); }
        return className;
    }

    @JsonProperty("import")
    public ODImport rawImport = null;

    //@JsonProperty("definition")
    private ODDefinition definition = null;
    public ODDefinition getDefinition() { return definition; }

    @JacksonXmlProperty(localName = "mapping")
    public ODMapping mapping = null;

    @JacksonXmlElementWrapper(localName = "variables")
    public ODVariables variables = null;

    @JacksonXmlProperty(localName = "properties")
    public ODProperties odProperties = null;

//    @JacksonXmlProperty(localName = "property")
//    @JacksonXmlElementWrapper(localName = "properties")
    private List<ODProperty> properties = new ArrayList<>();
    public List<ODProperty> getProperties() { return properties; }

    // TODO fill from properties list
    private Map<String, ODProperty> propertiesMap = new HashMap<>();
    public ODProperty getPropertyDescription(String propertyName) {
        return propertiesMap.get(propertyName.toLowerCase());
    }

    private ODExtension extension = null;
    public ODExtension getExtension() { return extension; }

//  @JacksonXmlProperty(localName = "script")
    public ODPropScript script = null;

    @JacksonXmlProperty(localName = "if")
    public OD_IF rawIf = null;

    public List<ODCreateFrom> createFrom = null;

    @JacksonXmlProperty(isAttribute = true)
    public String primaryodname = null;

    @JacksonXmlProperty(isAttribute = true)
    public String accesscontrol = null;

    @JacksonXmlProperty(isAttribute = true)
    public String namingattribute = null;
    @JacksonXmlProperty(isAttribute = true)
    public String displayattribute = null;
    @JacksonXmlProperty(isAttribute = true)
    public String displayname = null;
    @JacksonXmlProperty(isAttribute = true)
    public String defaultstatusmodule = null;


    // see https://reflectoring.io/jackson/ for @JsonAnySetter
    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("objdesc", fieldName, fieldValue);
    }

    // ----

    public void doPostProcessing() {
//        for (ObjDescProperty prop : properties) {
//            propertiesMap.put(prop.getName().toLowerCase(), prop);
//        }
    }
}
