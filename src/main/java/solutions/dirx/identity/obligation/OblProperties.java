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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JacksonXmlRootElement(localName = "properties")
public class OblProperties {
    private static final Logger LOG = LoggerFactory.getLogger(OblProperties.class);
    public static final String TAG = "properties";

    @JacksonXmlProperty(localName = "property")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<OblProperty> properties = new ArrayList<>();

    // ------

    public static OblProperties instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("OblProperties#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (!TAG.equals(sr.getLocalName())) {
            LOG.warn("OblProperties#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }
        if (!sr.isStartElement()) {
            LOG.warn("OblProperties#instanceFromXml: XmlReader is not at start tag");
            return null;
        }

        OblProperties props = new OblProperties();

        try {
            while (sr.hasNext()) {
                sr.nextTag();
                if (sr.isEndElement()) {
                    if (TAG.equals(sr.getLocalName())) {
                        break;
                    }
                    LOG.warn("Unexpected end of XML element '{}' parsing element '{}'", sr.getLocalName(), TAG);
                    break;
                }
                if (!sr.isStartElement()) {
                    if (XMLStreamConstants.END_DOCUMENT == sr.getEventType()) {
                        LOG.warn("pre-mature end of document reached parsing XML element '{}'", TAG);
                        break;
                    }
                    LOG.warn("expected XML start element parsing element '{}' but got event type {}", TAG, ObligationUtils.xmlEventTypeToString(sr.getEventType()));
                    continue;
                }

                String localName = sr.getLocalName();

                if (OblProperty.TAG.equals(localName)) {
                    OblProperty prop = OblProperty.instanceFromXml(sr);
                    if (Objects.nonNull(prop)) {
                        props.properties.add(prop);
                    }
                } else {
                    LOG.warn("Ignoring unsupported XML element in OblProperties#instanceFromXml: {}", localName);
                }
            }
        } catch (Exception e) {
            LOG.warn("{} in OblProperties#instanceFromXml: {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }

        return props;
    }
}
