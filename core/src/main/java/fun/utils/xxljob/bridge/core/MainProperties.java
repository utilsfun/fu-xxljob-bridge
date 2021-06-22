package fun.utils.xxljob.bridge.core;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fun.utils.xxljob.bridge")
public class MainProperties {

    private String adminApi;
    private String bridgePath;

    public String getAdminApi() {
        return StringUtils.endsWith(adminApi, "/") ? adminApi : adminApi + "/";
    }

    public String getBridgePath() {
        return StringUtils.endsWith(bridgePath, "/") ? bridgePath : bridgePath + "/";
    }

}