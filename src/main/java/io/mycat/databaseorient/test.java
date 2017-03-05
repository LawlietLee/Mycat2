package io.mycat.databaseorient;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.List;
import java.util.Scanner;
public class test implements Log{
    ODatabaseDocumentTx db;

    public test() {

        db = new ODatabaseDocumentTx
                ("plocal:database/changhong")
//                .create();
                .open("admin", "admin");
        ODocument oDocument = new ODocument("my");
        oDocument.field("id", "d");
        oDocument.field("name", "changhong");
        oDocument.save();
        for (ODocument animal : db.browseClass("my")) {
            System.out.println(animal.field("id").toString());
        }

        info("count:"+db.countClass("my"));
        List<ODocument> result = db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT * FROM my WHERE id like 'd%'"));
        result.stream().forEach(a->info(a.field("id")));
        db.command(
                new OCommandSQL("UPDATE my SET id = 'd2'"
                )).execute();
        result=db.query(
                new OSQLSynchQuery<ODocument>(
                        "SELECT * FROM my WHERE id like 'd%'"));
        result.stream().forEach(a->info(a.field("id")));

        db.close();

    }

    public static void main(String[] args) {

        test dmoe1 = new test();
//        dmoe1.tests();
 //      dmoe1.comtest();

    }

    private void comtest() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String sql = scanner.nextLine();
            try {
                if (sql.startsWith("select")) {
                    List<ODocument> result = db.query(
                            new OSQLSynchQuery<ODocument>(
                                    sql));
                    result.stream().forEach(a -> info(a.toMap()));
                } else {

                    Object object=       db.command(new OCommandSQL(
                            sql)).execute();
                    info(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
                info(e.getMessage());
            }

        }
    }

    private void tests() {
        OSchema schema= db.getMetadata().getSchema();
//        schema.dropClass("my");
//        OClass oClass = schema.createClass("my1");
        OClass oClass = schema.createClass("cc2");
        oClass.createProperty("id", OType.INTEGER);
        oClass.createProperty("name", OType.STRING);

    }
}
