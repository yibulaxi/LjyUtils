package com.ljy.ljyutils.stub;

import android.os.RemoteException;

import com.ljy.ljyutils.ISecurityCenter;

/**
 * Created by LJY on 2018/4/25.
 */

public class SecurityCenterImpl extends ISecurityCenter.Stub {

    private static final char SECRET_CODE='^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars=content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i]^=SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
