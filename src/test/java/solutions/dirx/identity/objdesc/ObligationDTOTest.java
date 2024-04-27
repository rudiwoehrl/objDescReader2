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

package solutions.dirx.identity.objdesc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import solutions.dirx.identity.objdesc.objects.BaseObjectType;
import solutions.dirx.identity.obligation.OblProperties;
import solutions.dirx.identity.obligation.OblProperty;
import solutions.dirx.identity.objdesc.propertyRules.FixedValueRule;
import solutions.dirx.identity.objdesc.propertyRules.PropertyNamingRule;
import solutions.dirx.identity.objdesc.propertyRules.PropertyRule;
import solutions.dirx.identity.objdesc.propertyRules.ReferenceRule;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ObligationDTOTest {

    private static XMLInputFactory f;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        f = XMLInputFactory.newFactory();
    }

    private static XMLStreamReader prepareXmlReader(String xml) {
        XMLStreamReader sr = null;
        try {
            sr = f.createXMLStreamReader(new StringReader(xml));
            assertEquals("No START_ELEMENT at beginning", XMLStreamConstants.START_ELEMENT, sr.next());
            return sr;
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void readProperty() {
        final String xmlOk = """
	<property name="dxmLyncInternetAccessEnabled" baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="TRUE"/>
		</namingRule>
		<namingRule>
			<fixedValue value="TWO"/>
		</namingRule>
	</property>
        """;
        final String xml_Value = """
	<property name="propA" baseObject="SvcTSAccount" value="valueA" >
	</property>
        """;
        final String xml_noRules = """
	<property name="propNoRules" baseObject="SvcTSAccount" >
	</property>
        """;
        final String xml_noName = """
	<property baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="TRUE"/>
		</namingRule>
	</property>
        """;
        final String xml_noBaseObject = """
	<property name="propNoBase" >
		<namingRule>
			<fixedValue value="TRUE"/>
		</namingRule>
	</property>
        """;
        final String xml_wrongAttribute = """
	<property name="propWrongAttr" baseObject="SvcTSAccount" wrongAttr="xxx">
		<fixedValue value="TRUE"/>
	</property>
        """;
        final String xml_wrongElement = """
	<property name="propWrongElem" baseObject="SvcTSAccount" >
		<fixedValue value="TRUE"/>
	</property>
        """;
        final String xml_wrongBaseObject = """
	<property name="propWrongBase" baseObject="SvcRole" value="valueA" >
	</property>
        """;

        XMLStreamReader sr;
        OblProperty prop;
        try {
            sr = prepareXmlReader(xmlOk);
            prop = OblProperty.instanceFromXml(sr);
            sr.close();
            assertNotNull("Property is null", prop);
            assertEquals("Wrong property name", "dxmLyncInternetAccessEnabled", prop.name);
            assertEquals("Wrong property base object", "SvcTSAccount", prop.baseObject);
            assertEquals("Wrong property base object type", BaseObjectType.ACCOUNT, prop.baseObjectType);
            assertEquals("Expect 2 rule elements", 2, prop.namingRules.size());
            assertNotNull("1. rule null", prop.namingRules.get(0));
            assertNotNull("2. rule null", prop.namingRules.get(1));

            sr = prepareXmlReader(xml_Value);
            prop = OblProperty.instanceFromXml(sr);
            sr.close();
            assertNotNull("Property is null", prop);
            assertEquals("Wrong value", "valueA", prop.value);

            List<String> wrongRules = List.of(xml_noRules, xml_noName, xml_noBaseObject, xml_wrongAttribute, xml_wrongElement, xml_wrongBaseObject);
            for (String xml : wrongRules) {
                sr = prepareXmlReader(xml);
                prop = OblProperty.instanceFromXml(sr);
                sr.close();
                assertNull("wrong property is not null: "+xml, prop);
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void readProperties() {
        final String xmlOk = """
<properties>
	<property name="name1" baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="TRUE"/>
		</namingRule>
	</property>
	<property name="name2" baseObject="SvcUser" >
		<namingRule>
			<reference baseObject="SvcUser" attribute="ou" />
		</namingRule>
	</property>
</properties>
        """;
        final String xml_wrongTag = """
<propertiesX>
	<property name="name1" baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="TRUE"/>
		</namingRule>
	</property>
</propertiesX>
        """;

        XMLStreamReader sr;
        OblProperties props;
        try {
            sr = prepareXmlReader(xmlOk);
            props = OblProperties.instanceFromXml(sr);
            sr.close();
            assertNotNull("Properties is null", props);
            assertEquals("Expect 2 property elements", 2, props.properties.size());
            assertNotNull("1. prop null", props.properties.get(0));
            assertNotNull("2. prop null", props.properties.get(1));
            assertEquals("1. property should have 1 naming rule", 1, props.properties.get(0).namingRules.size());
            assertEquals("2. property should have 1 naming rule", 1, props.properties.get(1).namingRules.size());

            sr = prepareXmlReader(xml_wrongTag);
            props = OblProperties.instanceFromXml(sr);
            sr.close();
            assertNull("Properties with wrong tag should be null", props);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    // ----

    final String xmlString_long = """
<properties>
	<property name="dxmLyncPrimaryUserAddress" baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="sip:"/>
			<reference baseObject="SvcUser" attribute="givenName" />
			<fixedValue value="."/>
			<reference baseObject="SvcUser" attribute="sn" />
			<fixedValue value="@"/>
			<reference baseObject="SvcTS" attribute="dxrOptions(dxmLyncBaseSipAddress)"/>
		</namingRule>
		<namingRule>
			<fixedValue value="sip:"/>
			<reference baseObject="SvcUser" attribute="sn" />
			<fixedValue value="@"/>
			<reference baseObject="SvcTS" attribute="dxrOptions(dxmLyncBaseSipAddress)"/>
		</namingRule>
		<namingRule>
			<fixedValue value="sip:"/>
			<reference baseObject="SvcUser" attribute="mail" />
		</namingRule>
	</property>

	<property name="dxmLyncPrimaryHomeServer" baseObject="SvcTSAccount" >
		<namingRule>
			<reference baseObject="SvcTS" attribute="dxrOptions(dxmLyncHomeServerName)" />
		</namingRule>
	</property>

	<property name="dxmLyncInternetAccessEnabled" baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="THREE"/>
		</namingRule>
	</property>

</properties>
        """;

    @Test
    public void readProperties_long() {
        XMLInputFactory f = XMLInputFactory.newFactory();
        try {
            XMLStreamReader sr = f.createXMLStreamReader(new StringReader(xmlString_long));
            assertTrue("StreamReader hasNext false", sr.hasNext());
            assertEquals("No START_ELEMENT at beginning", XMLStreamConstants.START_ELEMENT, sr.next());

            OblProperties props = OblProperties.instanceFromXml(sr);
            sr.close();

            assertNotNull("Properties is null", props);
            assertEquals("Expect 3 property elements", 3, props.properties.size());
            assertNotNull("1. prop null", props.properties.get(0));
            assertNotNull("2. prop null", props.properties.get(1));
            assertNotNull("3. prop null", props.properties.get(2));
            OblProperty prop = props.properties.get(0);
            assertEquals("Wrong name of 1. property", "dxmLyncPrimaryUserAddress", prop.name);
            assertEquals("Wrong baseObject of 1. property", "SvcTSAccount", prop.baseObject);
            assertEquals("Expect 3 naming rules in 1. property", 3, prop.namingRules.size());
            List<PropertyRule> ruleElements = prop.namingRules.get(0).propertyRules;
            assertEquals("Expect 6 rule elements in 1. naming rule", 6, ruleElements.size());
            FixedValueRule fixedValue = ((FixedValueRule)ruleElements.get(0));
            assertEquals("wrong fixed value 1.rule of 1. property", "sip:", fixedValue.value);
            ReferenceRule ref = ((ReferenceRule)ruleElements.get(1));
            assertEquals("wrong reference base object 2.rule of 1. prop element", "SvcUser", ref.baseObject);
            assertEquals("wrong reference attribute 2.rule of 1. prop element", "givenName", ref.attribute);
            ref = ((ReferenceRule)ruleElements.get(3));
            assertEquals("wrong reference base object 4.rule of 1. prop element", "SvcUser", ref.baseObject);
            assertEquals("wrong reference attribute 4.rule of 1. prop element", "sn", ref.attribute);
            prop = props.properties.get(1);
            assertEquals("Wrong name of 2. property", "dxmLyncPrimaryHomeServer", prop.name);
            assertEquals("Expect 1 naming rule in 2.property", 1, prop.namingRules.size());
            ruleElements = prop.namingRules.get(0).propertyRules;
            assertEquals("Expect 1 rule element in naming rule 2. property", 1, ruleElements.size());
            ref = ((ReferenceRule)ruleElements.get(0));
            assertEquals("wrong reference attribute 2.rule of 1. prop element", "dxrOptions(dxmLyncHomeServerName)", ref.attribute);
            prop = props.properties.get(2);
            assertEquals("Wrong name of 3. property", "dxmLyncInternetAccessEnabled", prop.name);
            assertEquals("Expect 1 naming rule in 3. property", 1, prop.namingRules.size());
            ruleElements = prop.namingRules.get(0).propertyRules;
            assertEquals("Expect 1 rule element in naming rule 3. property", 1, ruleElements.size());
            fixedValue = ((FixedValueRule)ruleElements.get(0));
            assertEquals("wrong fixed value 1. rule of 3. property", "THREE", fixedValue.value);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ends in
     * com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "fixedValue"
     * in OblNamingRule.
     */
    public void serializeProperties_withXmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.registerSubtypes( new NamedType(ReferenceRule.class, "reference"));
        mapper.registerSubtypes( new NamedType(FixedValueRule.class, "fixedValue"));

        try {
            OblProperties oblProps = mapper.readValue(xmlString_long, OblProperties.class);
            assertEquals("count properties not 3", 3, oblProps.properties.size());

            OblProperty prop = oblProps.properties.get(0);
            assertEquals("wrong prop baseObject", "SvcTSAccount", prop.baseObject);
            assertEquals("wrong prop name", "dxmLyncPrimaryUserAddress", prop.name);

            assertEquals("count naming rules not 3", 3, prop.namingRules.size());
            PropertyNamingRule rule = prop.namingRules.get(0);
            assertEquals("count fixedOrreferences not 6", 6, rule.propertyRules.size());
            PropertyRule fixRef = rule.propertyRules.get(0);
            System.out.println("FixedOrReference class 0: " + fixRef.getClass().getSimpleName());

//            FixedValue fixVal = fixRef.fixedValue;
//            assertEquals("", "sip:", fixVal.value);
//
//            Reference ref = fixRef.reference;
//            assertEquals("wrong reference1 baseObject", "SvcUser", ref.baseObject);
//            assertEquals("wrong reference1 attribute", "givenName", ref.attribute);

//            ref = rule.references.get(2);
//            assertEquals("wrong reference1 baseObject", "SvcTS", ref.baseObject);
//            assertEquals("wrong reference1 attribute", "dxrOptions(dxmLyncBaseSipAddress)", ref.attribute);
//
//            assertEquals("count fixedValues not 3", 3, rule.fixedValues.size());
//            assertEquals("wrong 2. fixedValue",".", rule.fixedValues.get(0).value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serializes the properties as map.
     * Map has 1 key "property"
     */
    @Test
    public void serializeProperties_asMap() {
        XmlMapper mapper = new XmlMapper();
        try {
            Map<String, Object> oblMap = mapper.readValue(xmlString_long, Map.class);
            System.out.println("#map keys: " + oblMap.keySet().size() + ": " + oblMap.keySet());
            List<?> props = (List<?>) oblMap.get("property");
            assertEquals("Expect 3 properties", 3, props.size());
            Map<String, ?> prop = (Map<String, ?>) props.get(0);
            List<?> rules = (List<?>) prop.get("namingRule");
//            System.out.println(rules.size() + " naming rules: " + rules);
            int i = 0;
            for (Object oRule : rules) {
                Map<String, Object> rule = (Map<String, Object>) oRule;
                System.out.println(rule.size() + " keys in rule " + ++i + ": " + rule.keySet());
                System.out.println("rule: " + rule);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // ----

    public void readXml_fixAndRef() {
        final String xmlString = """
<properties>
	<property name="dxmLyncInternetAccessEnabled" baseObject="SvcTSAccount" >
		<namingRule>
			<fixedValue value="@"/>
			<reference baseObject="SvcTS" attribute="dxrOptions(dxmLyncHomeServerName)" />
			<fixedValue value="."/>
			<reference baseObject="SvcTS" attribute="b" />
		</namingRule>
	</property>
</properties>
        """;
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(true);
        XmlMapper mapper = new XmlMapper(xmlModule);

        XMLInputFactory f = XMLInputFactory.newFactory();
        try {
            XMLStreamReader sr = f.createXMLStreamReader(new StringReader(xmlString));

            while (sr.hasNext()) {
                int x = sr.next();
                int et = sr.getEventType();
                if (et == XMLStreamConstants.START_ELEMENT) {
                    System.out.println(String.format("x=%d, et=%d: %s - %s", x, et, "START", sr.getLocalName()));
                    for (int i=0; i < sr.getAttributeCount(); i++) {
                        System.out.println(String.format("attribute %d: %s=%s", i, sr.getAttributeLocalName(i), sr.getAttributeValue(i)));
                    }

                }
                else if (et == XMLStreamConstants.END_ELEMENT) {
                    System.out.println(String.format("x=%d, et=%d: %s - %s", x, et, "END", sr.getLocalName()));
                }
            }

            sr.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
