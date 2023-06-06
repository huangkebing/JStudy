package adapter;

import lombok.Data;
import java.util.Map;

/**
 * 新业务使用的实体类
 */
@Data
public class OutUserInfo {
    Map<String, String> userInfoMap;
}
