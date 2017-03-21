package io.mycat.orientserver.handler.data_mannipulation;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import io.mycat.config.ErrorCode;
import io.mycat.databaseorient.adapter.MDBadapter;
import io.mycat.databaseorient.adapter.MException;
import io.mycat.orientserver.OConnection;

/**
 * Created by 长宏 on 2017/3/18 0018.
 * LOAD DATA INFILE 'data.txt' INTO TABLE db2.my_table;
 */
public class MloaddataINfile {
    public static void handle(MySqlLoadDataInFileStatement x, OConnection connection) {

        if (MDBadapter.currentDB == null) {
            connection.writeErrMessage(ErrorCode.ER_NO_DB_ERROR, "没有选择数据库");
        }
        try {
            MDBadapter.exesql(x.toString());
            connection.writeok();
        } catch (MException e) {
            e.printStackTrace();
            connection.writeErrMessage(e.getMessage());
        }

    }
}
