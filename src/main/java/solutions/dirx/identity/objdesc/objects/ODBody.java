package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JacksonXmlRootElement(localName = "body")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ODBody extends ODBaseObject {
    private static final Logger LOG = LoggerFactory.getLogger(ODBody.class);
    public static final String TAG = "body";
}
