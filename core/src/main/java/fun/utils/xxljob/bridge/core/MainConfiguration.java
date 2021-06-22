package fun.utils.xxljob.bridge.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@EnableConfigurationProperties(MainProperties.class)
@ComponentScan("fun.utils.xxljob.bridge.core")
public class MainConfiguration implements DisposableBean, InitializingBean {

    @Autowired
    private MainProperties mainProperties;

    @Autowired
    private ApplicationContext applicationContext;


    @Bean
    public RestTemplate restTemplate(){
       return new RestTemplate();
    }

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

//    @Bean
//    public AdminController adminController(){
//        return new AdminController();
//    }
//
//    @Bean
//    public ExecutorController executorController(){
//        return new ExecutorController();
//    }

    //最后一个bean,用于执行config加载后的主过程
    @Bean
    public void main() throws NoSuchMethodException {
        log.info("Initialize main program");
        //requestMappingHandlerMapping.registerMapping();
    }

    @Override
    public void destroy()  {
        log.info("Destroy ApiAutoConfiguration");
    }


    //执行顺序 Constructor > @Autowired > @postConstruct > afterPropertiesSet > @Bean (1,2,3,4 有顺序)

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialize afterPropertiesSet");
    }

    @PostConstruct
    public void postConstruct(){
        log.info("Initialize postConstruct");
    }

    public MainConfiguration() {
        log.info("Initialize Constructor");
    }
}

