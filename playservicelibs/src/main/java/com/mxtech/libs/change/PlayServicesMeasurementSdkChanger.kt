package com.mxtech.libs.change

import javassist.ClassPool
import javassist.CtNewMethod
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
class PlayServicesMeasurementSdkChanger : BaseChanger() {

    override fun libName(): String {
        return "play-services-measurement-sdk-18.0.2.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        val cc = pool["com.google.android.gms.measurement.internal.AppMeasurementDynamiteService"]

        val logEvent = cc.getDeclaredMethod("logEvent")
        val logEventNew = CtNewMethod.copy(logEvent, "logEventNew", cc, null)

        cc.addMethod(logEventNew)

        //SecurityException
        logEvent.setBody(
            "{" +
//                    "java.lang.StringBuilder sb = new java.lang.StringBuilder();" +
//                    "java.util.Set<java.lang.String> keys = $3.keySet();" +
//                    "for(java.lang.String key : keys) {" +
//                    "    sb.append(key).append(\":\").append(b.get(key)).append(\";\");" +
//                    "}" +
//                    "java.lang.String ss = sb.toString();" +
                    "android.util.Log.e(\"HOOK_LOG\", \"bundle value:\" + $3);" +
                    "$0.logEventNew($$);" +
                    "}"

        )

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

}