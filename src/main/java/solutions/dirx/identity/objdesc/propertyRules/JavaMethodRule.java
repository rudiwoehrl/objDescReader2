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

@JacksonXmlRootElement(localName = "javamethod")
public class JavaMethodRule extends PropertyRule {
    private static final Logger LOG = LoggerFactory.getLogger(JavaMethodRule.class);
    public static final String TAG = "javamethod";

    @JacksonXmlProperty(isAttribute = true)
    public String name;

    // ----

    public JavaMethodRule() {}

    // ----

    @Override
    public String toString() {
        return String.format("JavaMethod name=%s", name);
    }

    public static JavaMethodRule instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("JavaMethodRule#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("JavaMethodRule#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            JavaMethodRule rule = xmlMapper.readValue(sr, JavaMethodRule.class);

            if (!rule.validate()) { return null; }

            return rule;
        } catch (IOException e) {
            LOG.warn("IOException in JavaMethodRule#instanceFromXml '{}': {}", sr.getLocalName(), e.getMessage());
            return null;
        }
    }

    private boolean validate() {
        if (Objects.isNull(name) || name.isBlank()) {
            LOG.warn("No name in javamethod rule");
            return false;
        }

        return true;
    }
}
