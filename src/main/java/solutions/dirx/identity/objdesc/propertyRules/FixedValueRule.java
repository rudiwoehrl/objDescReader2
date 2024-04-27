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

package solutions.dirx.identity.objdesc.propertyRules;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Objects;

@JacksonXmlRootElement(localName = "fixedValue")
public class FixedValueRule extends PropertyRule {
    private static final Logger LOG = LoggerFactory.getLogger(FixedValueRule.class);
    public static final String TAG = "fixedValue";

    @JacksonXmlProperty(isAttribute = true)
    public String value;

    // ----

    public FixedValueRule() {}

    // ----

    public String toString() {
        return String.format("fixedValue='%s'", value);
    }


    public static FixedValueRule instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("FixedValue#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("FixedValue#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            FixedValueRule rule = xmlMapper.readValue(sr, FixedValueRule.class);

            if (!rule.validate()) { return null; }

            return rule;
        } catch (IOException e) {
            LOG.warn("IOException in FixedValue#instanceFromXml: {}", sr.getLocalName());
            return null;
        }
    }

    private boolean validate() {
        if (Objects.isNull(value)) {
            LOG.warn("No value in fixedValue rule");
            return false;
        }

        return true;
    }
}
