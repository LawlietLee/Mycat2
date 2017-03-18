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
package io.mycat.orientserver.response;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import io.mycat.backend.mysql.PacketUtil;
import io.mycat.config.ErrorCode;
import io.mycat.config.Fields;
import io.mycat.databaseorient.adapter.DBadapter;
import io.mycat.databaseorient.adapter.MException;
import io.mycat.databaseorient.adapter.TableAdaptor;
import io.mycat.databaseorient.sqlhander.sqlutil.MSQLutil;
import io.mycat.net.mysql.EOFPacket;
import io.mycat.net.mysql.FieldPacket;
import io.mycat.net.mysql.ResultSetHeaderPacket;
import io.mycat.net.mysql.RowDataPacket;
import io.mycat.orientserver.OConnection;
import io.mycat.util.StringUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 默认的响应
 */
public class MorientResponse {

    private static   int FIELD_COUNT = 1;
    private static   ResultSetHeaderPacket header = PacketUtil.getHeader(FIELD_COUNT);
    private static   FieldPacket[] fields = new FieldPacket[FIELD_COUNT];
    private static   EOFPacket eof = new EOFPacket();
    static List<String> fieldss = null;
    private static void inithead(List<Map<String, String>> data, String stmt) {
        List<String> strings = null;
        if (data.size() > 0) {
            strings = new ArrayList<>(data.get(0).keySet());
        }
        else {
            strings = MSQLutil.gettablenamefileds(stmt);
        }
        FIELD_COUNT = strings.size();
        header = PacketUtil.getHeader(FIELD_COUNT);
        fields = new FieldPacket[FIELD_COUNT];
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        for (String string : strings) {
            fields[i] = PacketUtil.getField(string, Fields.FIELD_TYPE_VAR_STRING);
            fields[i++].packetId = ++packetId;
        }
        fieldss = strings;
        eof.packetId = ++packetId;
    }
    static {
        int i = 0;
        byte packetId = 0;
        header.packetId = ++packetId;
        fields[i] = PacketUtil.getField("DATABASE22", Fields.FIELD_TYPE_VAR_STRING);
        fields[i++].packetId = ++packetId;
        eof.packetId = ++packetId;
    }

    /**
     * Response.没有还回结果的语句
     *
     * @param c   the c
     * @param sql the sql
     */
    public static void response(OConnection c, SQLStatement sql) {
        if (sql instanceof SQLCreateDatabaseStatement) {
            handercreatedb(sql, c);
            return;
        }
        if (DBadapter.currentDB == null) {
            c.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "no database selected!!");
            return;
        }

        try {
          String re=  DBadapter.getInstance().exesql(sql.toString());
//          c.write(re.getBytes());
            c.writeok();
        } catch (MException e) {
            c.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "执行语句错误");
            e.printStackTrace();
            return;
        }
    }

    public static void responseselect(OConnection c, SQLStatement stmt) {
        if (DBadapter.currentDB == null) {
            c.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "no database selected!!");
            return;
        }
        List<Map<String, String>> data = null;
        try {
         data=  DBadapter.getInstance().exequery(stmt.toString());
        } catch (MException e) {
            e.printStackTrace();
            c.writeErrMessage(ErrorCode.ERR_HANDLE_DATA, e.getMessage());
            return;
        }
        inithead(data,stmt.toString());
        ByteBuffer buffer = c.allocate();

        // write header
        buffer = header.write(buffer, c, true);

        // write fields
        for (FieldPacket field : fields) {
            buffer = field.write(buffer, c, true);
        }

        // write eof
        buffer = eof.write(buffer, c, true);

        // write rows
        byte packetId = eof.packetId;
            for (Map<String,String> name : data) {
                RowDataPacket row = new RowDataPacket(FIELD_COUNT);
                for (String s : fieldss) {
//                    String s1 = String.valueOf(s);
                    String ss2 = String.valueOf(name.get(s));
                    row.add(StringUtil.encode(ss2, c.getCharset()));
                }
                row.packetId = ++packetId;
                buffer = row.write(buffer, c, true);
            }
        // write last eof
        EOFPacket lastEof = new EOFPacket();
        lastEof.packetId = ++packetId;
        buffer = lastEof.write(buffer, c, true);

        // post write
        c.write(buffer);
    }

    private static void handercreatedb(SQLStatement sqlStatement, OConnection c) {

    }


}