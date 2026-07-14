package org.sara.api.builders;


import org.sara.api.Modelos.LoginModel;
import org.sara.api.Util.LoginUtil;

public class CreateSessionBuilder {


    public static LoginModel getToken() {
        return LoginModel.builder()
                .username(LoginUtil.getLoginUtil("username.sara3"))
                .password(LoginUtil.getLoginUtil("password.sara3")).build();
    }
}
