package solutions.dirx.identity.objdesc.util;

import java.util.Objects;

public class ObjDescUtils {
    public static final String SCHEMA_STORAGE = "storage";
    public static final String HOST_DXI = "DirXmetaRole";
    public static String ROOT = "$(rootDN)";

    public static String getDNFromUrlString(
            String rootDN,
            String urlString)
    {
        String prefix = String.format("%s://%s/", SCHEMA_STORAGE, HOST_DXI);

        if (Objects.isNull(rootDN) || Objects.isNull(urlString)) { return null; }

        int startIdx = urlString.indexOf(prefix);
        if (startIdx < 0) { return null; }
        int rootIdx = urlString.indexOf(ROOT);
        if (rootIdx < 0) { return null; }

        return urlString.substring(startIdx + prefix.length(), rootIdx) + rootDN;
    }
}
