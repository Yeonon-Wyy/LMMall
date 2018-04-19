package top.yeonon.lmmall.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OAuthUser {
    private Integer id;

    private String oauthType;

    private Integer oauthId;

    private Integer userId;
}