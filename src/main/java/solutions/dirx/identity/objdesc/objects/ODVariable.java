package solutions.dirx.identity.objdesc.objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JacksonXmlRootElement(localName = "var")
public class ODVariable {
    private static final Logger LOG = LoggerFactory.getLogger(ODVariable.class);
    public static final String TAG = "var";

    public String name = null;
    public String value = null;
}
