package com.mxtech.libs.change

import javassist.ClassPool
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
class PlayServicesAnalyticsImplChanger: BaseChanger() {

    override fun libName(): String {
        return "play-services-analytics-impl-17.0.0.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        val cc = pool["com.google.android.gms.internal.gtm.zzz"]

        val zzbv = cc.getDeclaredMethod("zzbv")
        zzbv.setBody("return \"666ad3ed-ac8f-4225-b1f5-7090a3e320f9\";")
        val zzbt = cc.getDeclaredMethod("zzbt")
        zzbt.setBody("return \"5eee83fbb96168ee0b0a54c4a21066a5\";")

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }
}