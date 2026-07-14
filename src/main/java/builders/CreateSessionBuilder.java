package builders;


import Modelos.LoginModel;
import Util.LoginUtil;

public class CreateSessionBuilder {


    public static LoginModel getToken() {
        return LoginModel.builder()
                .username(LoginUtil.getLoginUtil("username.sara3"))
                .password(LoginUtil.getLoginUtil("password.sara3")).build();
    }
}
