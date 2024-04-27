package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutions.dirx.identity.objdesc.util.UnrecognizedFieldCollector;

import java.util.ArrayList;
import java.util.Objects;

@JacksonXmlRootElement(localName = "extension")
public class ODExtension {
    private static final Logger LOG = LoggerFactory.getLogger(ODExtension.class);
    public static final String TAG = "extension";

    public ODLoadAttributes loadAttributes = null;

//    @JacksonXmlProperty(localName = "initialDefinition")
//    @JacksonXmlCData
//    @JacksonXmlText
    public String initialDefinition = null;
    @JsonSetter(value = "initialDefinition")
    public void setInitialDefinition(String initialDefinition) {
        //System.err.println("ODExtension.setInitialDefinition: " + initialDefinition);
        this.initialDefinition = initialDefinition;
    }

    @JsonAnySetter
    public void allSetter(String fieldName, String fieldValue) {
        UnrecognizedFieldCollector.addUnrecognizedField("extension", fieldName, fieldValue);
    }
}
