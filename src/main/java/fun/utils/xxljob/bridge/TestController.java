package fun.utils.xxljob.bridge;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "test")
@Slf4j
public class TestController {

    @RequestMapping(value = "/**")
    public void info(HttpServletRequest request){
        log.info(JSON.toJSONString(request,true));
    }
}
