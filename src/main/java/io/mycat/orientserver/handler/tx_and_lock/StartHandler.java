/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package io.mycat.orientserver.handler.tx_and_lock;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStartTransactionStatement;
import io.mycat.net.mysql.OkPacket;
import io.mycat.orientserver.OConnection;

/**
 * @author 完成
 */
public final class StartHandler {
    private static final byte[] AC_OFF = new byte[]{7, 0, 0, 1, 0, 0, 0, 0,
            0, 0, 0};

    public static void handle(String stmt, OConnection c, int offset) {

                if (c.isAutocommit()) {
                    c.setAutocommit(false);
                    c.write(c.writeToBuffer(AC_OFF, c.allocate()));
                } else {
//                    c.getSession2().commit();
                    c.writeok();
                }

    }

    public static void handle(MySqlStartTransactionStatement x,OConnection c) {
        c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
    }
}