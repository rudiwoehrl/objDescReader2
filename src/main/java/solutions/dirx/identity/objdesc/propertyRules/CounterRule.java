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

@JacksonXmlRootElement(localName = "counter")
public class CounterRule extends PropertyRule {
    private static final Logger LOG = LoggerFactory.getLogger(CounterRule.class);
    public static final String TAG = "counter";

    @JacksonXmlProperty(isAttribute = true)
    public String min;
    public Integer minInt;
    @JacksonXmlProperty(isAttribute = true)
    public String max;
    public Integer maxInt;
    public Integer length;

    // ----

    public CounterRule() {}

    // ----

    @Override
    public String toString() {
        return String.format("Counter min=%s max=%s", min, max);
    }

    public static CounterRule instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("CounterRule#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("CounterRule#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            CounterRule rule = xmlMapper.readValue(sr, CounterRule.class);

            if (!rule.validate()) { return null; }

            return rule;
        } catch (IOException e) {
            LOG.warn("IOException in CounterRule#instanceFromXml: {}", sr.getLocalName());
            return null;
        }
    }

    private boolean validate() {
        if (Objects.isNull(min) || min.isBlank()) {
            LOG.warn("No min in counter rule");
            return false;
        }
        try {
            minInt = Integer.valueOf(min);
            if (minInt < 0) {
                LOG.warn("Counter cannot start with min < 0: {}. Using 0.", min);
                minInt = 0;
            }
        } catch (NumberFormatException e) {
            LOG.warn("min '{}' is not an Integer: {}", min, e.getMessage());
            return false;
        }

        if (Objects.isNull(max) || max.isBlank()) {
            LOG.warn("No max in counter rule");
            return false;
        }
        try {
            maxInt = Integer.valueOf(max);
            if (maxInt < minInt) {
                LOG.warn("Counter max {} cannot be lower than min {}. Ignoring counter rule.", max, min);
                return false;
            }
        } catch (NumberFormatException e) {
            LOG.warn("max '{}' is not an Integer: {}", max, e.getMessage());
            return false;
        }

        length = maxInt.toString().length();

        return true;
    }
}
