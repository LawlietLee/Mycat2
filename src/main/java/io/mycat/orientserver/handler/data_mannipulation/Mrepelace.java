package io.mycat.orientserver.handler.data_mannipulation;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import io.mycat.config.ErrorCode;
import io.mycat.databaseorient.adapter.DBadapter;
import io.mycat.databaseorient.adapter.MException;
import io.mycat.orientserver.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 */
public class Mrepelace {
    public static void handle(MySqlReplaceStatement x, OConnection connection) {

        if (DBadapter.currentDB == null) {
            connection.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "没有选择数据库");
        }
        try {
            DBadapter.getInstance().exesql(x.toString());
            connection.writeok();
        } catch (MException e) {
            e.printStackTrace();
            connection.writeErrMessage(e.getMessage());
        }
    }
}
