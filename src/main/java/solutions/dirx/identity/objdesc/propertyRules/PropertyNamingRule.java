/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) Atos 2024
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

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutions.dirx.identity.obligation.ObligationUtils;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents XML element namingRule in an obligation.
 *
 * Note:
 * A naming rule contains an ordered list of sub-elements fixedValue and reference.
 * Jackson's XMLMapper is not able to de-serialize such a list automatically.
 * That's the reason we are working with XMLStreamReader and partial read.
 */
@JacksonXmlRootElement(localName = "namingRule")
public class PropertyNamingRule {
    private static final Logger LOG = LoggerFactory.getLogger(PropertyNamingRule.class);
    public static final String TAG = "namingRule";

    /** contains non-Javamethod rules */
    public List<PropertyRule> propertyRules = new ArrayList<>();

    /** java method rules; should occur max once */
    public List<JavaMethodRule> methodRules = new ArrayList<>();
    public JavaMethodRule getJavamethod() {
        if (methodRules.isEmpty()) { return null; }
        if (methodRules.size() > 1) {
            LOG.warn("More than 1 javamethod rules in naming rules: {}. Taking first.", methodRules.stream().map(r -> r.name).toList());
        }

        return methodRules.get(0);
    }

    /** true if one of the property rules is a break rule */
    private boolean hasBreakRule = false;
    public boolean isHasBreakRule() { return hasBreakRule; }

    // ----

    @JsonSetter(value = ReferenceRule.TAG)
    public void addReferenceRule(ReferenceRule rule) {
        propertyRules.add(rule);
    }

    @JsonSetter(value = FixedValueRule.TAG)
    public void addFixedValueRule(FixedValueRule fixedValueRule) {
        propertyRules.add(fixedValueRule);
    }

    @JsonSetter(value = BreakRule.TAG)
    public void addBreakRule(BreakRule rule) {
        propertyRules.add(rule);
        hasBreakRule = true;
    }

    @JsonSetter(value = CounterRule.TAG)
    public void addCounterRule(CounterRule rule) {
        propertyRules.add(rule);
    }

    @JsonSetter(value = JavaMethodRule.TAG)
    public void addJavaMethodRule(JavaMethodRule rule) {
        methodRules.add(rule);
    }

    @JsonSetter(value = RandomRule.TAG)
    public void addRandomRule(RandomRule rule) {
        propertyRules.add(rule);
    }

    // ----

    public static PropertyNamingRule instanceFromXml(XMLStreamReader sr) {
        if (Objects.isNull(sr)) {
            LOG.warn("NamingRule#instanceFromXml: no XMLStreamReader");
            return null;
        }
        if (! TAG.equals(sr.getLocalName())) {
            LOG.warn("NamingRule#instanceFromXml: wrong local name in XML string: '{}'", sr.getLocalName());
            return null;
        }
        if (! sr.isStartElement()) {
            LOG.warn("NamingRule#instanceFromXml: XmlReader is not at start tag");
            return null;
        }

        PropertyNamingRule rule = new PropertyNamingRule();
        rule.propertyRules = new ArrayList<>();

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

                if (! sr.isStartElement()) {
                    if (XMLStreamConstants.END_DOCUMENT == sr.getEventType()) {
                        LOG.warn("pre-mature end of document reached parsing XML element '{}'", TAG);
                        break;
                    }
                    LOG.warn("expected XML start element parsing element '{}' but got event type {}", TAG, ObligationUtils.xmlEventTypeToString(sr.getEventType()));
                    continue;
                }

                String localName = sr.getLocalName();

                if (FixedValueRule.TAG.equals(localName)) {
                    FixedValueRule fixedValue = FixedValueRule.instanceFromXml(sr);
                    if (Objects.nonNull(fixedValue)) { rule.propertyRules.add(fixedValue); }
                }
                else if (ReferenceRule.TAG.equals(localName)) {
                    ReferenceRule reference = ReferenceRule.instanceFromXml(sr);
                    if (Objects.nonNull(reference)) { rule.propertyRules.add(reference); }
                }
                else if (CounterRule.TAG.equals(localName)) {
                    CounterRule counter = CounterRule.instanceFromXml(sr);
                    if (Objects.nonNull(counter)) { rule.propertyRules.add(counter); }
                }
                else if (RandomRule.TAG.equals(localName)) {
                    RandomRule random = RandomRule.instanceFromXml(sr);
                    if (Objects.nonNull(random)) { rule.propertyRules.add(random); }
                }
                else if (JavaMethodRule.TAG.equals(localName)) {
                    JavaMethodRule methodRule = JavaMethodRule.instanceFromXml(sr);
                    if (Objects.nonNull(methodRule)) {
                        //rule.propertyRules.add(methodRule);
                        rule.methodRules.add(methodRule);
                    }
                }
                else if (BreakRule.TAG.equals(localName)) {
                    BreakRule breakRule = BreakRule.instanceFromXml(sr);
                    if (Objects.nonNull(breakRule)) {
                        rule.propertyRules.add(breakRule);
                        rule.hasBreakRule = true;
                    }
                }
                else {
                    LOG.warn("Ignoring unsupported XML element in OblNamingRule#instanceFromXml: {}", localName);
                    sr.nextTag();
                    if (sr.isEndElement()) {
                        if (localName.equals(sr.getLocalName())) {
                            continue;
                        }
                        LOG.warn("Unexpected end of XML element '{}'; expected end of '{}'", sr.getLocalName(), localName);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("{} in OblNamingRule#instanceFromXml: {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }

        if (!rule.validate()) { return null; }

        return rule;
    }

    private boolean validate() {
        if (propertyRules.isEmpty() && methodRules.isEmpty()) {
            LOG.warn("No naming rule defined");
            return false;
        }

        return true;
    }
}
