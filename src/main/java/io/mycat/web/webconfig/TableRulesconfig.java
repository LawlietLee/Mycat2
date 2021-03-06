package io.mycat.web.webconfig;

import io.mycat.config.model.rule.TableRuleConfig;
import io.mycat.web.config.MyConfigLoader;
import io.mycat.web.config.MyReloadConfig;
import io.mycat.web.model.ReturnMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * Created by jiang on 2016/12/3 0003.
 *表规则的功能
 */
@SuppressWarnings("Duplicates")
@RestController
public class TableRulesconfig {
    /**
     * Gets .
     * 得打所有的表规则让用户选择
     *
     * @return the
     */
    @GetMapping(value = "/gettablerules")
    public ReturnMessage getsys() {
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setError(false);
        returnMessage.setObject(MyConfigLoader.getInstance().getTableRuleConfigMap().values().toArray());
        return returnMessage;
    }
    /**
     * Sets .增加一个表规则或者设置一个表规则
     *
     * @param d      the d
     * @param result the result
     * @return the
     */
    @PostMapping(value = "/addtablerule")
    public ReturnMessage setsysconfig(@Valid @RequestBody TableRuleConfig tableRuleConfig, BindingResult result) {
        ReturnMessage returnMessage = new ReturnMessage();
        if (result.hasErrors()) {
            returnMessage.setError(true);
            returnMessage.setMessage(result.toString());
            return returnMessage;
        }
        Map<String, TableRuleConfig> map = MyConfigLoader.getInstance().getTableRuleConfigMap();
        map.put(tableRuleConfig.getName(), tableRuleConfig);
        MyConfigLoader.getInstance().save();
      String dd=  MyReloadConfig.reloadconfig(false);
        if (dd == null) {

            returnMessage.setError(false);
        }
        else {
            returnMessage.setMessage(dd);
            returnMessage.setError(true);
        }
        return returnMessage;
    }

}
