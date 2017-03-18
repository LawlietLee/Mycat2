package io.mycat.orientserver.handler.data_define;

import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import io.mycat.config.ErrorCode;
import io.mycat.databaseorient.adapter.DBadapter;
import io.mycat.databaseorient.adapter.MException;
import io.mycat.databaseorient.adapter.TableAdaptor;
import io.mycat.orientserver.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 */
public class DropTable {
    public static void handle(SQLDropTableStatement x, OConnection connection) {
        if (DBadapter.currentDB == null) {
            connection.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "没有选择数据库");
        }
        x.getTableSources().forEach(table->{
            try {
                TableAdaptor.getInstance().droptable(DBadapter.currentDB,table.toString());
            } catch (MException e) {
                e.printStackTrace();
                connection.writeErrMessage(e.getMessage());
                return;
            }
        });
            connection.writeok();
    }
}
