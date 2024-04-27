package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JacksonXmlRootElement(localName = "object")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ODObject extends ODBaseObject {
    private static final Logger LOG = LoggerFactory.getLogger(ODObject.class);
    public static final String TAG = "object";
}
