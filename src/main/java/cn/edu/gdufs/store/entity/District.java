package cn.edu.gdufs.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:省/市/区数据的实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class District {
    private Integer id;
    private String parent;
    private String code;
    private String name;
}
