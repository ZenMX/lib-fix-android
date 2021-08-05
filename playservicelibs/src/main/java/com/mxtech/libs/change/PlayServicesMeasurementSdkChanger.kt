package com.mxtech.libs.change

import javassist.ClassPool
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

        val methods = cc.declaredMethods
        for (method in methods) {
            val name = method.name
            println("====$name")
            method.insertBefore("android.util.Log.e(\"HOOK_LOG\", \"method called:$name\");")
        }

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

}