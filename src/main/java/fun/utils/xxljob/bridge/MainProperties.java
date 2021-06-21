package fun.utils.xxljob.bridge;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "fun.utils.xxljob.bridge")
public class MainProperties {


    private String serverPath = "http://localhost/";
    private String bridgePath = "http://localhost/";


    public String getServerPath() {
        return StringUtils.endsWith(serverPath,"/") ? serverPath : serverPath + "/";
    }

    public String getBridgePathPath() {
        return StringUtils.endsWith(bridgePath,"/") ? bridgePath : bridgePath + "/";
    }

}