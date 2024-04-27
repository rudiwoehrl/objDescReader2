package solutions.dirx.identity.objdesc;

import solutions.dirx.identity.objdesc.objects.ODBaseObject;
import solutions.dirx.identity.objdesc.objects.ODProperty;

import java.util.HashMap;
import java.util.Map;

public class ObjDescHolder {
    public Map<String, ODBaseObject> objectDescriptions = new HashMap<>();

    public Map<String, String> variables = new HashMap<>();

    public HashMap<String, ODProperty> globalProperties = new HashMap<>();

    // ----

    private static ObjDescHolder holderInstance = new ObjDescHolder();

    //---

    public static ObjDescHolder getInstance() {
        return holderInstance;
    }
}
