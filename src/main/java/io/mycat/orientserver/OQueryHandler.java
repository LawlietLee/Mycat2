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
package io.mycat.orientserver;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import io.mycat.config.ErrorCode;
import io.mycat.net.handler.FrontendQueryHandler;
import io.mycat.net.mysql.OkPacket;
import io.mycat.orientserver.handler.adminstatement.*;
import io.mycat.orientserver.handler.data_mannipulation.SelectHandler;
import io.mycat.orientserver.handler.tx_and_lock.BeginHandler;
import io.mycat.orientserver.handler.tx_and_lock.SavepointHandler;
import io.mycat.orientserver.handler.tx_and_lock.StartHandler;
import io.mycat.orientserver.parser.SQLvisitor;
import io.mycat.orientserver.parser.ServerParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 服务器查询处理器
 */
public class OQueryHandler implements FrontendQueryHandler {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(OQueryHandler.class);

    private final OConnection source;
    protected Boolean readOnly;
    private MySqlASTVisitor mySqlASTVisitor;

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public OQueryHandler(OConnection source) {
        this.source = source;
        mySqlASTVisitor = new SQLvisitor(source);
    }

    @Override
    public void query(String sql) {

        OConnection c = this.source;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(new StringBuilder().append(c).append(sql).toString());
        }
        SQLStatement mySqlStatement = null;
        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            mySqlStatement = parser.parseStatement();
        } catch (Exception e) {
            e.printStackTrace();
            c.writeErrMessage(ErrorCode.ER_SELECT_REDUCED, e.getMessage());
            return;
        }
        //
        int rs = ServerParse.parse(sql);
        int sqlType = rs & 0xff;

        switch (sqlType) {
            //explain sql
            case ServerParse.EXPLAIN:
                ExplainHandler.handle(sql, c, rs >>> 8);
                break;
            //explain2 datanode=? sql=?
            case ServerParse.EXPLAIN2:
                Explain2Handler.handle(sql, c, rs >>> 8);
                break;
            case ServerParse.SET:
                SetHandler.handle(sql, c, rs >>> 8);
                break;
            case ServerParse.SHOW:
                ShowHandler.handle(sql, c, rs >>> 8);
                break;
            case ServerParse.SELECT:
                SelectHandler.handle(sql, c, rs >>> 8);
                break;
            case ServerParse.START:
                StartHandler.handle(sql, c, rs >>> 8);
                break;
            case ServerParse.BEGIN:
                BeginHandler.handle(sql, c);
                break;
            //不支持oracle的savepoint事务回退点
            case ServerParse.SAVEPOINT:
                SavepointHandler.handle(sql, c);
                break;
            case ServerParse.KILL:
                KillHandler.handle(sql, rs >>> 8, c);
                break;
            //不支持KILL_Query
            case ServerParse.KILL_QUERY:
                LOGGER.warn(new StringBuilder().append("Unsupported command:").append(sql).toString());
                c.writeErrMessage(ErrorCode.ER_UNKNOWN_COM_ERROR, "Unsupported command");
                break;
            case ServerParse.USE:
                UseHandler.handle(sql, c, rs >>> 8);
                break;
            case ServerParse.COMMIT:
                c.commit();
                break;
            case ServerParse.ROLLBACK:
                c.rollback();
                break;
            case ServerParse.HELP:
                LOGGER.warn(new StringBuilder().append("Unsupported command:").append(sql).toString());
                c.writeErrMessage(ErrorCode.ER_SYNTAX_ERROR, "Unsupported command");
                break;
            case ServerParse.MYSQL_CMD_COMMENT:
                c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
                break;
            case ServerParse.MYSQL_COMMENT:
                c.write(c.writeToBuffer(OkPacket.OK, c.allocate()));
                break;
            case ServerParse.LOAD_DATA_INFILE_SQL:
                c.loadDataInfileStart(sql);
                break;
            case ServerParse.MIGRATE:
                MigrateHandler.handle(sql, c);
                break;
            case ServerParse.LOCK:
                c.lockTable(sql);
                break;
            case ServerParse.UNLOCK:
                c.unLockTable(sql);
                break;
            default:
                if (readOnly) {
                    LOGGER.warn(new StringBuilder().append("User readonly:").append(sql).toString());
                    c.writeErrMessage(ErrorCode.ER_USER_READ_ONLY, "User readonly");
                    break;
                }

//                c.execute(sql, rs & 0xff);
                mySqlStatement.accept(mySqlASTVisitor);

        }
    }

}
