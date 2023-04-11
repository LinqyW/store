package cn.edu.gdufs.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:用户的dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer uid;
    private String username;
}
