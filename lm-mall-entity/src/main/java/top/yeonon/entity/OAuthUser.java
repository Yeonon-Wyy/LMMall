package top.yeonon.entity;

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

    private String oauthId;

    private Integer userId;
}