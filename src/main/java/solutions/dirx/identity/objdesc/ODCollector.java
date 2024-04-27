package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import netscape.ldap.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutions.dirx.identity.objdesc.objects.ODBaseObject;
import solutions.dirx.identity.objdesc.objects.ODConfiguration;
import solutions.dirx.identity.objdesc.objects.ODGlobalProperties;
import solutions.dirx.identity.objdesc.objects.ODVariables;
import solutions.dirx.identity.objdesc.util.ObjDescException;

import java.util.*;

public class ODCollector {
    private static final Logger LOG = LoggerFactory.getLogger(ODCollector.class);

    static final Set<String> variableEntries = Set.of(
            "Variables.xml"
    );
    static final Set<String> propertyEntries = Set.of(
            "GlobalProperties.xml"
    );
    static final Set<String> errorEntries = Set.of(
            "newTS.xml"
    );


    private final LDAPConnection ldapConnection;

    private String rootDN = null;

    private ObjDescHolder odHolder = null;

    private XmlMapper xmlMapper = XmlMapper.builder()
//            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

    public ODCollector(
            LDAPConnection ldapConnection,
            ObjDescHolder odHolder)
    {
        this.ldapConnection = ldapConnection;
        this.odHolder = odHolder;
    }

    public void collectObjectDescriptions(String rootDN)
    {
        this.rootDN = rootDN;

        ODConfiguration config = null;

        List<LDAPEntry> foundODs = searchODs();
        for (LDAPEntry ldapEntry : foundODs) {
            LDAPAttribute attr = ldapEntry.getAttribute("dxrObjDesc");
            String odXml = Objects.nonNull(attr) ? attr.getStringValue(0) : null;
            if (Objects.isNull(odXml)) {
                LOG.warn("Object description entry '{}' has no dxrObjDesc attribute", ldapEntry.getDN());
                continue;
            }
            attr = ldapEntry.getAttribute("cn");
            String odCN = Objects.nonNull(attr) ? attr.getStringValue(0) : null;


            try {
                if ("Config.xml".equalsIgnoreCase(odCN)) {
                    config = transformToConfig(ldapEntry.getDN(), odXml);
                    continue;
                }

                if (specialHandling(ldapEntry, odXml)) {
                    continue;
                }

                ODBaseObject odObject = transformToODObject(ldapEntry, odXml);

                if (Objects.nonNull(odObject)) {
                    odHolder.objectDescriptions.put(normalizeDN(ldapEntry.getDN()), odObject);
                }
            } catch (ObjDescException alreadyLogged) {
            } catch (Exception e) {
                LOG.warn("{} while parsing object description XML: {}", e.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    private String normalizeDN(String dn) {
        // TODO check with Storage.normalize and UnboundID LDAPDN.normalize
        return LDAPDN.normalize(dn);
    }

    private boolean specialHandling(
            LDAPEntry ldapEntry,
            String odXml)
            throws ObjDescException
    {
        LDAPAttribute attr = ldapEntry.getAttribute("cn");
        String odCN = Objects.nonNull(attr) ? attr.getStringValue(0) : null;
        if (Objects.isNull(odCN) || odCN.isEmpty()) {
            LOG.warn("Object description entry '{}' has no cn attribute", ldapEntry.getDN());
            return true;
        }

        if (variableEntries.contains(odCN)) {
            processAsVariables(ldapEntry.getDN(), odXml);
            return true;
        }

        if (propertyEntries.contains(odCN) || odCN.endsWith(".properties")) {
            processProperties(ldapEntry.getDN(), odXml);
            return true;
        }

        if (! odCN.endsWith(".xml")) {
            LOG.warn("Object description entry '{}' with unsupported suffix will be ignored", ldapEntry.getDN());
            return true;
        }

        if (odXml.contains("<wizard")) { return true; }

        return false;
    }

    private ODBaseObject transformToODObject(
            LDAPEntry ldapEntry,
            String odXml)
    {
        try {
            return xmlMapper.readValue(odXml, ODBaseObject.class);
        } catch (Exception e) {
            LOG.warn("{} while parsing object description of entry '{}': {}",
                    e.getClass().getSimpleName(), ldapEntry.getDN(), e.getMessage());
            // TODO logging in this project not visible in test
            System.err.printf("%s parsing object description of entry '%s': %s\n",
                    e.getClass().getSimpleName(), ldapEntry.getDN(), e.getMessage());
            return null;
        }
    }

    ODConfiguration transformToConfig(
            String entryDN,
            String odXml)
            throws ObjDescException
    {
        try {
            return xmlMapper.readValue(odXml, ODConfiguration.class);
        } catch (JsonProcessingException e) {
            LOG.warn("{} while parsing object description configuration entry '{}': {}",
                    e.getClass().getSimpleName(), entryDN, e.getMessage());
            throw new ObjDescException(e);
        }
    }

    void processAsVariables(
            String entryDN,
            String odXml)
            throws ObjDescException
    {
        try {
            ODVariables vars = xmlMapper.readValue(odXml, ODVariables.class);
            if (Objects.isNull(vars)) { return; }
            odHolder.variables.putAll(vars.variablesMap);
        } catch (Exception e) {
            LOG.warn("{} while parsing entry '{}' as variables: {}",
                    e.getClass().getSimpleName(), entryDN, e.getMessage());
            throw new ObjDescException(e);
        }
    }

    void processProperties(
            String entryDN,
            String odXml)
            throws ObjDescException
    {
        try {
            ODGlobalProperties props = xmlMapper.readValue(odXml, ODGlobalProperties.class);
            if (Objects.isNull(props)) { return; }
            odHolder.globalProperties.putAll(props.properties);
        } catch (Exception e) {
            LOG.warn("{} while parsing entry '{}' as global properties: {}",
                    e.getClass().getSimpleName(), entryDN, e.getMessage());
            throw new ObjDescException(e);
        }
    }

    private List<LDAPEntry> searchODs() {
        List<LDAPEntry> foundEntries = new ArrayList<>();

        final String filter = "(&(objectclass=dxrObjectDesc)(dxrType=ODML))";
        final String searchBase = rootDN;
        final String[] reqestedAttrs = new String[] {"cn","dxrObjDesc"};

        try {
            LDAPSearchResults searchResult = ldapConnection.search(searchBase, LDAPv2.SCOPE_SUB, filter, reqestedAttrs, false);
            while (searchResult.hasMoreElements()) {
                LDAPEntry ldapEntry = searchResult.next();
                // TODO handle sizelimit reached --> paged search

                foundEntries.add(ldapEntry);
            }
        } catch (LDAPException e) {
            LOG.warn("LDAPException with error code {} while searching object descriptions: {}", e.errorCodeToString(), e.getMessage());
            return Collections.emptyList();
        }

        return foundEntries;
    }
}
