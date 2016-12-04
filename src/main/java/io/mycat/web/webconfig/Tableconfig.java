package io.mycat.web.webconfig;

import io.mycat.config.model.SchemaConfig;
import io.mycat.config.model.TableConfig;
import io.mycat.web.config.MyConfigLoader;
import io.mycat.web.config.MyReloadConfig;
import io.mycat.web.model.ReturnMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by jiang on 2016/12/3 0003.
 * 表有关的配置功能
 */
@SuppressWarnings("Duplicates")
@RestController
public class Tableconfig {
    /**
     * Gets .
     * 得到一个数据库中所有的表，不是所有的表。
     *
     * @return the
     */
    @GetMapping(value = "/gettables/{dbname}")
    public ReturnMessage getsys(@PathVariable String dbname) {
        ReturnMessage returnMessage = new ReturnMessage();
        SchemaConfig schemaConfig = MyConfigLoader.getInstance().getSchemaConfig(dbname);
        if (schemaConfig == null) {
            returnMessage.setError(true);
            returnMessage.setMessage("不存在");
            return returnMessage;
        }
        returnMessage.setError(false);
        returnMessage.setObject(schemaConfig.getTables().values().toArray());
        return returnMessage;
    }

    /**
     * Sets .增加一个表。
     *
     * @param d      the d
     * @param result the result
     * @return the
     */
    @PostMapping(value = "/addtable/{dbname}")
    public ReturnMessage setsysconfig(@PathVariable String dbname, @Valid @RequestBody TableConfig tableConfig, BindingResult result) {
        ReturnMessage returnMessage = new ReturnMessage();
        if (result.hasErrors()) {
            returnMessage.setError(true);
            returnMessage.setMessage(result.toString());
            return returnMessage;
        }
        SchemaConfig schemaConfig = MyConfigLoader.getInstance().getSchemaConfig(dbname);
        schemaConfig.addtable(tableConfig);
        MyConfigLoader.getInstance().save();
        String dd = MyReloadConfig.reloadconfig(false);
        if (dd == null) {
            returnMessage.setError(false);
        } else {
            returnMessage.setMessage(dd);
            returnMessage.setError(true);
        }
        return returnMessage;
    }

}
