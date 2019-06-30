package com.rpc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcMethod {

    private Method method;
    private Constructor[] parameterConstructors;
    private Class[] parameterTypes;

    RpcMethod(Method method) {
        this.method = method;
        method.setAccessible(true);
        parameterTypes = method.getParameterTypes();
        parameterConstructors = new Constructor[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            try {
                parameterConstructors[i] = parameterTypes[i].getConstructor(null);
                parameterConstructors[i].setAccessible(true);
            } catch (Exception ex) {
                parameterConstructors[i] = null;
            }
        }
    }


    public void Call(Object on, ArrayList<String> params) {
        if (params == null || params.size() != parameterTypes.length) {
            System.err.println("RpcMethod::Call(" + Arrays.toString(params.toArray()) + "):" + " invalid parameter count for method " + method.getName() + " in class " + method.getClass());
            return;
        }

        try {
            Object[] callParameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {

                Constructor valueConstructor = parameterConstructors[i];
                Object defaultValue = null;

                if (null != valueConstructor) {
                    try {
                        defaultValue = valueConstructor.newInstance();
                    } catch (Exception ex) {
                    }
                }

                callParameters[i] = Reflection.stringToObject(params.get(i), defaultValue);
            }

            method.invoke(on, callParameters.length == 0 ? null : callParameters);
        } catch (Exception ex) {
            return;
        }

    }
}
