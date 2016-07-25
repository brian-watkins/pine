package org.pine

import java.lang.reflect.Method

class ProxyMethod {

    public static Method metaMethodProxy (MetaMethod metaMethod) {
        Method proxyMethod = Method.newInstance([Object.class, "proxyMethod", [] as Class[], Object.class,
                                                 [] as Class[], 0, 0, "", [] as byte[], [] as byte[],
                                                 [] as byte[]] as Object[])

        proxyMethod.metaClass.invokeMethod = { String name, args ->
            return metaMethod.invokeMethod(name, args)
        }

        proxyMethod.metaClass.getProperty = { String name ->
            return metaMethod."${name}"
        }

        return proxyMethod
    }
}
