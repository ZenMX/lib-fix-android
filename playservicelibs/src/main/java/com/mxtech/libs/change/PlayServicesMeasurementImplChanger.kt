package com.mxtech.libs.change

import javassist.ClassPool
import javassist.CtNewMethod
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
class PlayServicesMeasurementImplChanger : BaseChanger() {

    override fun libName(): String {
        return "play-services-measurement-impl-18.0.2.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        val cc = pool["com.google.android.gms.measurement.internal.zzjb"]

        val zzr = cc.getDeclaredMethod("zzR")
        val zzrNew = CtNewMethod.copy(zzr, "zzrNew", cc, null)

        cc.addMethod(zzrNew)

        //SecurityException
        zzr.setBody(
            "{" +
                    "java.util.Iterator it = zzf.iterator();" +
                    "while(it.hasNext()) {" +
                    "    String cName = it.next().getClass().getName();" +
                    "    android.util.Log.e(\"HOOK_LOG\",\"taskName:\" + cName, new java.lang.Exception());" +
                    "}" +
//                    "for(Runnable run : zzf) {" +
//                    "    String cName = run.getClass().getName();" +
//                    "    android.util.Log.e(\"HOOK_LOG\",\"taskName:\" + cName, new java.lang.Exception());" +
//                    "}" +
                    "$0.zzrNew();" +
                    "}"

        )

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

}