package fun.utils.xxljob.bridge.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value = "xxl-job-executor")
@Slf4j
public class ExecutorController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MainProperties mainProperties;

    public ExecutorController() {
        log.info("loading ExecutorController");
    }

    /*
    a、心跳检测
    说明：调度中心检测执行器是否在线时使用
    ------
    地址格式：{执行器内嵌服务跟地址}/beat
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
    响应数据格式：
        {
          "code": 200,      // 200 表示正常、其他失败
          "msg": null       // 错误提示消息
        }
    */
    @RequestMapping(value = "/{executor}/beat")
    public JSONObject beat(@PathVariable String executor, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall(executor, "log", null, token);
    }

    /*
    b、忙碌检测
    说明：调度中心检测指定执行器上指定任务是否忙碌（运行中）时使用
    ------
    地址格式：{执行器内嵌服务跟地址}/idleBeat
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
        {
            "jobId":1       // 任务ID
        }
    响应数据格式：
        {
          "code": 200,      // 200 表示正常、其他失败
          "msg": null       // 错误提示消息
        }

    * */
    @RequestMapping(value = "/{executor}/idleBeat")
    public JSONObject idleBeat(@PathVariable String executor, @RequestBody JSONObject body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall(executor, "idleBeat", body, token);
    }

    /*
    *
   c、触发任务
    说明：触发任务执行
    ------
    地址格式：{执行器内嵌服务跟地址}/run
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
        {
            "jobId":1,                                  // 任务ID
            "executorHandler":"demoJobHandler",         // 任务标识
            "executorParams":"demoJobHandler",          // 任务参数
            "executorBlockStrategy":"COVER_EARLY",      // 任务阻塞策略，可选值参考 com.xxl.job.core.enums.ExecutorBlockStrategyEnum
            "executorTimeout":0,                        // 任务超时时间，单位秒，大于零时生效
            "logId":1,                                  // 本次调度日志ID
            "logDateTime":1586629003729,                // 本次调度日志时间
            "glueType":"BEAN",                          // 任务模式，可选值参考 com.xxl.job.core.glue.GlueTypeEnum
            "glueSource":"xxx",                         // GLUE脚本代码
            "glueUpdatetime":1586629003727,             // GLUE脚本更新时间，用于判定脚本是否变更以及是否需要刷新
            "broadcastIndex":0,                         // 分片参数：当前分片
            "broadcastTotal":0                          // 分片参数：总分片
        }
    响应数据格式：
        {
          "code": 200,      // 200 表示正常、其他失败
          "msg": null       // 错误提示消息
        }
    * */

    @RequestMapping(value = "/{executor}/run")
    public JSONObject run(@PathVariable String executor, @RequestBody JSONObject body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall(executor, "run", body, token);
    }


    /*
    *
      f、终止任务
    说明：终止任务
    ------
    地址格式：{执行器内嵌服务跟地址}/kill
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
        {
            "jobId":1       // 任务ID
        }
    响应数据格式：
        {
          "code": 200,      // 200 表示正常、其他失败
          "msg": null       // 错误提示消息
        }
    * */

    @RequestMapping(value = "/{executor}/kill")
    public JSONObject kill(@PathVariable String executor, @RequestBody JSONObject body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall(executor, "kill", body, token);
    }



    /*
    *
    d、查看执行日志
    说明：终止任务，滚动方式加载
    ------
    地址格式：{执行器内嵌服务跟地址}/log
    Header：
        XXL-JOB-ACCESS-TOKEN : {请求令牌}
    请求数据格式如下，放置在 RequestBody 中，JSON格式：
        {
            "logDateTim":0,     // 本次调度日志时间
            "logId":0,          // 本次调度日志ID
            "fromLineNum":0     // 日志开始行号，滚动加载日志
        }
    响应数据格式：
        {
            "code":200,         // 200 表示正常、其他失败
            "msg": null         // 错误提示消息
            "content":{
                "fromLineNum":0,        // 本次请求，日志开始行数
                "toLineNum":100,        // 本次请求，日志结束行号
                "logContent":"xxx",     // 本次请求日志内容
                "isEnd":true            // 日志是否全部加载完
            }
        }
    * */

    @RequestMapping(value = "/{executor}/log")
    public JSONObject log(@PathVariable String executor, @RequestBody JSONObject body, @RequestHeader("XXL-JOB-ACCESS-TOKEN") @Nullable String token) {
        return reCall(executor, "log", body, token);
    }

    private JSONObject reCall(String executor, String action, JSON body, String token) {

        //桥地址 转 原执器地址
        String executorUrl = executor.replaceAll("@", "/");
        executorUrl = executorUrl.endsWith("/") ? executorUrl : executorUrl + "/";

        JSONObject info = new JSONObject();

        info.put("executor", executorUrl);
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

            String string = restTemplate.postForObject(executorUrl + action, requestHttpEntity, String.class);

            result = JSON.parseObject(string);

        } catch (Exception e) {

            result.put("code", 500);
            result.put("msg", e.toString());

        }

        info.put("result", result);
        log.info(JSON.toJSONString(info, true));
        return result;
    }
}