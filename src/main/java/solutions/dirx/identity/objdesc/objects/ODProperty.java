package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;
import solutions.dirx.identity.objdesc.propertyRules.PropertyNamingRule;

import javax.xml.stream.XMLStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class ODProperty {
    private static final Logger LOG = LoggerFactory.getLogger(ODProperty.class);
    public static final String TAG = "property";

    @JacksonXmlProperty(isAttribute = true)
    private String name = null;
    public String getName() { return name; }

    @JacksonXmlProperty(isAttribute = true)
    private String type = "java.lang.String";
    public String getType() { return type; }

    @JacksonXmlProperty(isAttribute = true)
    private boolean multivalue = false;
    public boolean isMultiValue() { return multivalue; }

    @JacksonXmlProperty(isAttribute = true)
    private boolean incremental = false;
    public boolean isIncremental() { return incremental; }

    @JacksonXmlProperty(isAttribute = true)
    private boolean readonly = false;
    public boolean isReadonly() { return readonly; }

    @JacksonXmlProperty(isAttribute = true)
    public boolean mandatory = false;
    public boolean isMandatory() { return mandatory; }

    @JacksonXmlProperty(isAttribute = true)
    private String defaultvalue = null;
    public void setDefaultvalue(String defaultvalue) { this.defaultvalue = defaultvalue; }
    public String getDefaultValue() { return defaultvalue; }

    @JacksonXmlProperty(isAttribute = true)
    @JsonAlias({ "clearOnMasterRemoval", "clearonmasterremoval" })
    private String clearOnMasterRemoval = null;
    public boolean isClearOnMasterRemoval() { return "true".equalsIgnoreCase(clearOnMasterRemoval); }

    @JsonAlias({ "uniquein", "uniqueIn" })
    @JacksonXmlProperty(isAttribute = true)
    private String uniquein = null;
    public String getUniqueIn() { return uniquein; }

    @JacksonXmlProperty(isAttribute = true)
    private String subsetdelimiter = null;
    public String getSubsetdelimiter() { return subsetdelimiter; }

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

    @JacksonXmlProperty(isAttribute = true)
    private String applyDependsOn = null;
    public String getApplyDependsOn() { return applyDependsOn; }

    @JacksonXmlProperty(isAttribute = true)
    private String master = null;
    public String getMaster() { return master; }

    @JacksonXmlElementWrapper(localName = "extension")
    @JacksonXmlProperty(localName = "namingRule")
    private List<PropertyNamingRule> namingRules = new ArrayList<>();
    public List<PropertyNamingRule> getNamingRules() { return namingRules; }

    private ODPropScript script = null;
    public ODPropScript getScript() { return script; }

    // ----

    // see https://reflectoring.io/jackson/ for @JsonAnySetter
    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("property", fieldName, fieldValue);
    }

    // ----

    // TODO we probably don't need it
    @Deprecated
    public static ODProperty instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("ObjDescProperty#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("ObjDescProperty#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ODProperty property = xmlMapper.readValue(sr, ODProperty.class);

            if (!property.validate()) { return null; }

            return property;
        } catch (Exception e) {
            LOG.warn("{} in ObjDescProperty#instanceFromXml: {}", e.getClass().getSimpleName(), sr.getLocalName());
            return null;
        }
    }

    private boolean validate() {
        if (Objects.isNull(name) || name.isBlank()) {
            LOG.warn("XML attribute name missing in element <property>");
            return false;
        }
        if (Objects.isNull(type) || type.isBlank()) {
            LOG.warn("XML attribute type missing in element <property>");
            return false;
        }

        return true;
    }
}
