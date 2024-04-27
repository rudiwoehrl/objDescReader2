/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) Atos 2023
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Atos IT Solutions and Services GmbH ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Atos IT Solutions and Services GmbH.
 *
 *  END OF COPYRIGHT NOTICE.
 *
 */

package solutions.dirx.identity.obligation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutions.dirx.identity.objdesc.objects.BaseObjectType;
import solutions.dirx.identity.objdesc.propertyRules.PropertyNamingRule;

import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@JacksonXmlRootElement(localName = "property")
public class OblProperty {
    private static final Logger LOG = LoggerFactory.getLogger(OblProperty.class);
    public static final String TAG = "property";

    @JacksonXmlProperty(isAttribute = true)
    public String name = null;
    @JacksonXmlProperty(isAttribute = true)
    public String baseObject = null;
    public BaseObjectType baseObjectType;
    @JacksonXmlProperty(isAttribute = true)
    public String value = null;
    @JacksonXmlProperty(localName = "namingRule")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<PropertyNamingRule> namingRules = new ArrayList<>();

    // ----

    public OblProperty() {}

    // ----

    public static OblProperty instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("OblProperty#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("OblProperty#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }
        if (! sr.isStartElement()) {
            LOG.warn("OblProperty#instanceFromXml: XmlReader is not at start tag");
            return null;
        }

        OblProperty prop = new OblProperty();

        try {
            if (0 == sr.getAttributeCount()) {
                LOG.warn("XML element 'property' has no attributes, but should have at least 2");
                return null;
            }

            for (int i=0; i < sr.getAttributeCount(); i++) {
                switch (sr.getAttributeLocalName(i)) {
                    case "name" -> prop.name = sr.getAttributeValue(i);
                    case "baseObject" -> prop.baseObject = sr.getAttributeValue(i);
                    case "value" -> prop.value = sr.getAttributeValue(i);
                    default -> LOG.warn("Wrong attribute name '{}' in XML obligation property", sr.getAttributeLocalName(i));
                }
            }
            if (Objects.nonNull(prop.baseObject)) {
                prop.baseObjectType = BaseObjectType.instanceFromClassname(prop.baseObject);
            }

            while (sr.hasNext()) {
                sr.nextTag();
                if (sr.isEndElement()) {
                    if (TAG.equals(sr.getLocalName())) {
                        break;
                    }
                    LOG.warn("Unexpected end of XML element '{}' parsing element '{}'", sr.getLocalName(), TAG);
                    break;
                }
                if (! sr.isStartElement()) {
                    LOG.warn("expected XML start element parsing element '{}' but got event type {}", TAG, ObligationUtils.xmlEventTypeToString(sr.getEventType()));
                    continue;
                }

                String localName = sr.getLocalName();

                if (PropertyNamingRule.TAG.equals(localName)) {
                    PropertyNamingRule rule = PropertyNamingRule.instanceFromXml(sr);
                    if (Objects.nonNull(rule)) { prop.namingRules.add(rule); }
                }
                else {
                    LOG.warn("Ignoring unsupported XML element in OblProperty#instanceFromXml: {}", localName);
                }
            }

            if (!prop.validate()) { return null; }
        } catch (Exception e) {
            LOG.warn("{} in OblProperty#instanceFromXml: {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }

        return prop;
    }

    private boolean validate() {
        if (Objects.isNull(name) || name.isBlank()) {
            LOG.warn("XML attribute name missing in element <property>");
            return false;
        }
        if (Objects.isNull(baseObject) || baseObject.isBlank()) {
            LOG.warn("XML attribute baseObject missing in property '{}'", name);
            return false;
        }
        if (! Set.of(BaseObjectType.USER, BaseObjectType.ACCOUNT).contains(baseObjectType)) {
            LOG.warn("Not supported baseObject {} in property {}", baseObject, name);
            return false;
        }
        if (Objects.isNull(value) && namingRules.isEmpty()) {
            LOG.warn("Need at least 1 naming rule in property {}, if no value is given.", name);
            return false;
        }

        return true;
    }
}
