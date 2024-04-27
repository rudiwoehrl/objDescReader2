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
import solutions.dirx.identity.objdesc.objects.BaseObjectType;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Objects;

@JacksonXmlRootElement(localName = "reference")
public class ReferenceRule extends PropertyRule {
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceRule.class);
    public static final String TAG = "reference";

    @JacksonXmlProperty(isAttribute = true)
    public String baseObject;
    public BaseObjectType baseObjectType;
    @JacksonXmlProperty(isAttribute = true)
    public String attribute;
    @JacksonXmlProperty(isAttribute = true)
    public String address = null;
    @JacksonXmlProperty(isAttribute = true)
    public String range;
    public Integer from = null;
    public Integer to = null;
    @JacksonXmlProperty(isAttribute = true)
    public String type;
    @JacksonXmlProperty(isAttribute = true, localName = "case")
    public String cvtCase;
    public Boolean isCaseUpper = null;

    public boolean isInteger = false;
    @JacksonXmlProperty(isAttribute = true)
    public String baseIndex;
    public Integer baseIndexInt;


    public ReferenceRule() {}


    public String toString() {
        return String.format("baseObject=%s, address=%s, attribute=%s, range=%s, case=%s, type=%s, baseIndex=%s",
            baseObject, address, attribute, range, cvtCase, type, baseIndex);
    }

    public static ReferenceRule instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("ReferenceRule#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("ReferenceRule#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            ReferenceRule rule = xmlMapper.readValue(sr, ReferenceRule.class);

            if (!rule.validate()) { return null; }

            return rule;
        } catch (IOException e) {
            LOG.warn("IOException in ReferenceRule#instanceFromXml: {}", sr.getLocalName());
            return null;
        }
    }

    private boolean validate() {
        if (Objects.isNull(attribute) || attribute.isBlank()) {
            LOG.warn("No attribute in reference rule");
            return false;
        }

        if (Objects.isNull(baseObject) || baseObject.isBlank()) {
            LOG.warn("No baseObject in reference rule");
            return false;
        }

        baseObjectType = BaseObjectType.instanceFromClassname(baseObject);
        if (BaseObjectType.OTHER.equals(baseObjectType)) {
            LOG.warn("Not supported baseObject {} in reference rule", baseObject);
            return false;
        }

        if (Objects.nonNull(type)) {
            String lType = type.toLowerCase();
            if ("integer".equals(lType)) {
                isInteger = true;
            } else if (! "string".equals(lType)) {
                LOG.warn("Wrong type {} in reference rule. Use as String.", type);
            }
        }

        if (! extractFromTo()) { return false; }

        if (! extractCase()) { return false; }

        if (! extractBaseIndex()) { return false; }

        return true;
    }

    /**
     * Extracts indexes from and to from range.
     * From and to remain null, if range is not a correct string.
     * To might remain null, if range contains only start index.
     *
     * Note that method is public just for unit tests.
     *
     * @return true if range is correct.
     */
    public boolean extractFromTo() {
        if (Objects.isNull(range) || range.isEmpty()) { return true; }

        String[] tokens = range.trim().split(":");
        if ((tokens.length < 1) || (tokens.length > 2)) {
            LOG.warn("Wrong number of indexes in range '{}' of reference rule. Ignoring it.", range);
            return false;
        }

        if (tokens.length == 1) {
            try {
                from = Integer.valueOf(tokens[0]);
            } catch (Exception e) {
                LOG.warn("{} calculating from-index of range '{}' of reference rule: {}",
                        e.getClass().getSimpleName(), range, e.getMessage());
                return false;
            }
        } else {
            try {
                String sFrom = tokens[0];
                from = Objects.nonNull(sFrom) && (sFrom.length() > 0)
                        ? Integer.valueOf(sFrom) : 0;
                to = Integer.valueOf(tokens[1]);
            } catch (Exception e) {
                LOG.warn("{} calculating  from- or to-index of range '{}' of reference rule: {}",
                        e.getClass().getSimpleName(), range, e.getMessage());
                from = null; to = null;
                return false;
            }
        }
        if (from < 0) {
            LOG.warn("Range of reference rule cannot start with index < 0: {}. Using start index 0.", range);
            from = 0;
        }
        if (Objects.nonNull(to) && (to <= from)) {
            LOG.warn("Range to={} in reference rule must be higher than from={}", to, from);
            from = null; to = null;
            return false;
        }

        return true;
    }

    private boolean extractBaseIndex() {
        if (Objects.isNull(baseIndex) || baseIndex.isEmpty()) { return true; }
        try {
            baseIndexInt = Integer.valueOf(baseIndex.trim());
        } catch (NumberFormatException e) {
            LOG.warn("baseIndex '{}' is not an Integer: {}. Ignoring it.", baseIndex, e.getMessage());
            baseIndexInt = null;
        }

        return true;
    }

    /**
     * Sets isCaseUpper according cvtCase string.
     * If case string is missing or empty, isCaseUpper remains null.
     *
     * @return true if case string is correct or missing or empty.
     */
    public boolean extractCase() {
        if (Objects.isNull(cvtCase) || cvtCase.isEmpty()) { return true; }
        String lcase = cvtCase.trim().toLowerCase();
        if (lcase.startsWith("up")) {
            isCaseUpper = Boolean.TRUE;
        } else if (lcase.startsWith("lo")) {
            isCaseUpper = false;
        } else {
            LOG.warn("Wrong case in reference rules: {}", cvtCase);
            isCaseUpper = null;
            return false;
        }

        return true;
    }
}
