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

import javax.xml.stream.XMLStreamConstants;

public class ObligationUtils {

    public static String xmlEventTypeToString(int et) {
        return switch(et) {
            case XMLStreamConstants.START_ELEMENT -> "START";
            case XMLStreamConstants.END_ELEMENT -> "END";
            case XMLStreamConstants.ATTRIBUTE -> "ATTRIBUTE";
            case XMLStreamConstants.DTD -> "DTD";
            case XMLStreamConstants.CDATA -> "CDATA";
            case XMLStreamConstants.CHARACTERS -> "CHARACTERS";
            case XMLStreamConstants.COMMENT -> "COMMENT";
            case XMLStreamConstants.END_DOCUMENT -> "END_DOCUMENT";
            case XMLStreamConstants.ENTITY_DECLARATION -> "ENTITY_DECLARATION";
            case XMLStreamConstants.ENTITY_REFERENCE -> "ENTITY_REFERENCE";
            case XMLStreamConstants.NAMESPACE -> "NAMESPACE";
            case XMLStreamConstants.NOTATION_DECLARATION -> "NOTATION_DECLARATION";
            case XMLStreamConstants.PROCESSING_INSTRUCTION -> "PROCESSING_INSTRUCTION";
            case XMLStreamConstants.SPACE -> "SPACE";
            case XMLStreamConstants.START_DOCUMENT -> "START_DOCUMENT";
            default -> et + ": unknown";
        };
    }
}
