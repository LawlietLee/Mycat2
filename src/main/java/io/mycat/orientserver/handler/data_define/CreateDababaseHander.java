package io.mycat.orientserver.handler.data_define;

import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import io.mycat.databaseorient.adapter.MDBadapter;
import io.mycat.databaseorient.adapter.MException;
import io.mycat.databaseorient.sqlhander.sqlutil.MSQLutil;
import io.mycat.orientserver.OConnection;

/**
 * Created by 长宏 on 2017/2/25 0025.
 */
public class CreateDababaseHander {
    public static void handle(SQLCreateDatabaseStatement createDatabaseStatement, OConnection c) {
        try {
            MDBadapter.createdb(MSQLutil.getdbname( createDatabaseStatement));
            c.writeok();
        } catch (MException e) {
            e.printStackTrace();
            c.writeErrMessage(e.getMessage());
        }

    }
}
