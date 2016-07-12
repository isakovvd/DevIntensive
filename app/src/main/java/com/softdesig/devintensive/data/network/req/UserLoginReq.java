package com.softdesig.devintensive.data.network.req;

/**
 * @author IsakovVlad
 * @created on 12.07.16
 */

public class UserLoginReq {
    private String email;
    private String password;

    public UserLoginReq(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
