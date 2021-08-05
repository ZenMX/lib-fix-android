package com.mxtech.libs.change

import javassist.ClassPool
import javassist.CtNewMethod
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

        val cc = pool["com.google.android.gms.measurement.internal.zzkf"]

        val zzh = cc.getDeclaredMethod("zzh")
        val zzhNew = CtNewMethod.copy(zzh, "zzhNew", cc, null)

        cc.addMethod(zzhNew)

        //SecurityException
        zzh.setBody(
            "{" +
                    "android.util.Log.e(\"HOOK_LOG\",\"hook method:${zzh.longName}\", new java.lang.Exception());" +
                    "return $0.zzhNew($$);" +
                    "}"

        )

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

}