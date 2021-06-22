package fun.utils.xxljob.bridge.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping(value = "xxl-job-admin/api")
@Slf4j
public class AdminController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MainProperties mainProperties;

    public AdminController() {
        log.info("loading AdminController");
    }

    /*
    * b、执行器注册
    说明：执行器注册时使用，调度中心会实时感知注册成功的执行器并发起任务调度
    ------
    地址格式：{调度中心跟地址}/registry
    Header：
    XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
    {
        "registryGroup":"EXECUTOR",                     // 固定值
        "registryKey":"xxl-job-executor-example",       // 执行器AppName
        "registryValue":"http://127.0.0.1:9999/"        // 执行器地址，内置服务跟地址
    }
    响应数据格式：
    {
        "code": 200,      // 200 表示正常、其他失败
        "msg": null      // 错误提示消息
    }
    */
    @RequestMapping(value = "/registry")
    public JSONObject registry(@RequestBody JSONObject body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {


        String action = "registry";
        JSONObject info = new JSONObject();
        info.put("admin", mainProperties.getAdminApi() + action);
        info.put("action", action);
        info.put("token", token);
        info.put("body", body.clone());


        //篡改执器地址为 桥地址
        String registryValue = body.getString("registryValue");
        String codeRegistryValue = registryValue.replaceAll("/", "@");
        String newRegistryValue = mainProperties.getBridgePath() + "xxl-job-executor/" + codeRegistryValue + "/";

        body.put("registryValue", newRegistryValue);
        info.put("registryValue", newRegistryValue);


        JSONObject result = new JSONObject();
        result.put("code", 500);
        result.put("msg", "remote server fail");

        try {

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            if (StringUtils.isNotBlank(token)) {
                requestHeaders.set("XXL-JOB-ACCESS-TOKEN", token);
            }

            HttpEntity<JSONObject> requestHttpEntity = new HttpEntity<>(body, requestHeaders);

            String string = restTemplate.postForObject(mainProperties.getAdminApi() + action, requestHttpEntity, String.class);

            result = JSON.parseObject(string);

        } catch (Exception e) {

            result.put("code", 500);
            result.put("msg", e.toString());

        }


        info.put("result", result);
        log.info(JSON.toJSONString(info, true));

        return result;
    }

    /*
    * a 任务回调
    说明：执行器执行完任务后，回调任务结果时使用
    ------
    地址格式：{调度中心跟地址}/callback
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
        [{
            "logId":1,              // 本次调度日志ID
            "logDateTim":0,         // 本次调度日志时间
            "executeResult":{
                "code": 200,        // 200 表示任务执行正常，500表示失败
                "msg": null
            }
        }]
    响应数据格式：
    {
      "code": 200,      // 200 表示正常、其他失败
      "msg": null      // 错误提示消息
    }
    * */
    @RequestMapping(value = "/callback")
    public JSONObject callback(@RequestBody JSONArray body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall("callback", body, token);
    }

    /*
    * c、执行器注册摘除
    说明：执行器注册摘除时使用，注册摘除后的执行器不参与任务调度与执行
    ------
    地址格式：{调度中心跟地址}/registryRemove
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
        {
            "registryGroup":"EXECUTOR",                     // 固定值
            "registryKey":"xxl-job-executor-example",       // 执行器AppName
            "registryValue":"http://127.0.0.1:9999/"        // 执行器地址，内置服务跟地址
        }
    响应数据格式：
        {
          "code": 200,      // 200 表示正常、其他失败
          "msg": null      // 错误提示消息
        }
    * */

    @RequestMapping(value = "/registryRemove")
    public JSONObject registryRemove(@RequestBody JSONObject body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall("registryRemove", body, token);
    }


    private JSONObject reCall(String action, JSON body, String token) {

        JSONObject info = new JSONObject();
        info.put("admin", mainProperties.getAdminApi() + action);
        info.put("action", action);
        info.put("token", token);
        info.put("body", body);


        JSONObject result = new JSONObject();
        result.put("code", 500);
        result.put("msg", "remote server fail");

        try {

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            if (StringUtils.isNotBlank(token)) {
                requestHeaders.set("XXL-JOB-ACCESS-TOKEN", token);
            }

            HttpEntity<JSON> requestHttpEntity = new HttpEntity<>(body, requestHeaders);

            result = restTemplate.postForObject(mainProperties.getAdminApi() + action, requestHttpEntity, JSONObject.class);


        } catch (Exception e) {

            result.put("code", 500);
            result.put("msg", e.toString());

        }

        info.put("result", result);
        log.info(JSON.toJSONString(info, true));
        return result;
    }

}