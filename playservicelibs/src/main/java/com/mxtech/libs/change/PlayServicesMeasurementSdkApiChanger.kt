package com.mxtech.libs.change

import javassist.ClassPool
import javassist.CtNewMethod
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
class PlayServicesMeasurementSdkApiChanger : BaseChanger() {

    override fun libName(): String {
        return "play-services-measurement-sdk-api-18.0.2.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        var cc = pool["com.google.android.gms.internal.measurement.zzbr"]
        val zzW = cc.getDeclaredMethod("zzW")
        val zzWNew = CtNewMethod.copy(zzW, "zzWNew", cc, null)
        cc.addMethod(zzWNew)
        zzW.setBody(
            "{" +
                    "android.util.Log.e(\"HOOK_LOG\", \"impl class:\" + $0.zzk.getClass().getName());" +
                    "$0.zzWNew($$);" +
                    "}"

        )

        cc.writeFile(File(buildRoot, "jar").absolutePath)

        cc = pool["com.google.android.gms.internal.measurement.zzbg"]
        val runMethod = cc.getDeclaredMethod("run")
        runMethod.setBody("""{
        try {
            android.util.Log.e("HOOK_LOG", "else zza()"  + this.getClass().getName(), new java.lang.Exception());
            zza();
        } catch (Exception e) {
            android.util.Log.e("HOOK_LOG", "else zzb()" + this.getClass().getName());
            zzb();
        }
        }""".trimIndent())
        cc.writeFile(File(buildRoot, "jar").absolutePath)

    }

}