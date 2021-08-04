package com.mxtech.libs.change

import javassist.ClassPool
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
class PlayServicesMeasurementChanger : BaseChanger() {

    override fun libName(): String {
        return "play-services-measurement-18.0.2.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        val cc = pool["com.google.android.gms.internal.measurement.zzdj"]

        val zzbv = cc.getDeclaredMethod("zzG")
        zzbv.setBody(
            "{String uuid = String.valueOf(java.util.UUID.randomUUID());" +
                    "System.out.println(\"========uuid:\" + uuid + \"========\");" +
                    "return uuid;}"
        )

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

}