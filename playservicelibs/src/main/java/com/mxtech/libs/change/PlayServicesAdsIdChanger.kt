package com.mxtech.libs.change

import javassist.ClassPool
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
class PlayServicesAdsIdChanger: BaseChanger() {

    override fun libName(): String {
        return "play-services-ads-identifier-17.0.0.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        val cc = pool["com.google.android.gms.internal.ads_identifier.zzg"]

        val zzbv = cc.getDeclaredMethod("getId")
        zzbv.setBody("return \"666ad3ed-ac8f-4225-b1f5-7090a3e320f9\";")

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

}