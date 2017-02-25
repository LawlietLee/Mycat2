package io.mycat.databaseorient;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import io.mycat.databaseorient.adapter.DBadapter;
import io.mycat.databaseorient.adapter.OrientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 长宏 on 2017/2/23 0023.
 * 用来演示orientdb
 */
public class TestOrientDB {
    public static void main(String[] args) throws OrientException {
        DBadapter.currentDB = "changhong";
        List<Map<String, String>> list = DBadapter.getInstance().exequery("select * from t1");
        System.out.println(list.size());

        ODatabaseDocumentTx db = new ODatabaseDocumentTx
                ("plocal:database/changhong")
                .open("admin", "admin");
        ODocument animal = new ODocument("t1");
        animal.field("id", "66");
        animal.save();

        for (ODocument animal1 : db.browseClass("t1")) {
            System.out.println(animal1.field("id")+"");
        }
        System.out.println("sql :---------------------------------------");
        String sqlquery="select * from t1";
        List<ODocument> result = db.query(
                new OSQLSynchQuery<ODocument>(
                        sqlquery));
        result.forEach(a-> java.lang.System.out.println(a.field("id")+""));
    }

}
