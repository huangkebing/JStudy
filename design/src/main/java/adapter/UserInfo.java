package adapter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 原有实现使用的入参类
 */
@Data
@AllArgsConstructor
public class UserInfo {
    private String name;
    private int age;
    private String phone;
}
