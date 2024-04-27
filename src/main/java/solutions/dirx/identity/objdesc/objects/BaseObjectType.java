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

package solutions.dirx.identity.objdesc.objects;

import java.util.Objects;

public enum BaseObjectType {
    ACCOUNT, USER, TS, GROUP, ROLEPARAM, OTHER;

    /**
     * @param clsName simple class name.
     * @return BaseObjectType.
     */
    public static BaseObjectType instanceFromClassname(String clsName) {
        if (Objects.isNull(clsName)) { return ACCOUNT; }

        return switch(clsName.toLowerCase()) {
            case "svctsaccount" -> ACCOUNT;
            case "svcuser" -> USER;
            case "svcts"-> TS;
            case "svcgroup" -> GROUP;
            case "svcpersona" -> USER;
            case "svcfunctionaluser" -> USER;
            case "svcuserfacet" -> USER;
            case "roleparamnode" -> ROLEPARAM;
            case "" -> ACCOUNT;
            default -> OTHER;
        };
    }
}
