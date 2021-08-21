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

        var cc = pool["com.google.android.gms.measurement.internal.zzjb"]

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

        cc = pool["com.google.android.gms.measurement.internal.zzhn"]
        val zzv = cc.getDeclaredMethod("zzv")
        zzv.insertBefore("""
//            android.os.Bundle bundle = $3;
//            if(bundle != null) {
//            java.lang.StringBuilder sb = new java.lang.StringBuilder();
//            java.util.Iterator it = bundle.keySet().iterator();
//            while (it.hasNext()) {
//                java.lang.String key = it.next();
//                java.lang.Object value = bundle.get(key);
//                sb.append(key).append("=").append(value).append(";");
//            }
            android.util.Log.e("HOOK_LOG", "arg1=" + $1 + ",arg2=" + $2 + ",arg3=" + $3);
//        }
        """.trimIndent())

        val zzx = cc.getDeclaredMethod("zzx")
        zzx.insertBefore("""
//            android.os.Bundle bundle = $4;
//            if(bundle != null) {
//            java.lang.StringBuilder sb = new java.lang.StringBuilder();
//            java.util.Iterator it = bundle.keySet().iterator();
//            while (it.hasNext()) {
//                java.lang.String key = it.next();
//                java.lang.Object value = bundle.get(key);
//                sb.append(key).append("=").append(value).append(";");
//            }
            android.util.Log.e("HOOK_LOG", "zzx bundle=" + $4);
//        }
        """.trimIndent())
//        zzv.insertBefore("""
//            android.util.Log.e("HOOK_LOG", "arg1=" + $1 + ",arg2=" + $2 + ",arg3=" + $3);
//        """.trimIndent())
        cc.writeFile(File(buildRoot, "jar").absolutePath)


        cc = pool["com.google.android.gms.measurement.internal.zzgt"]
        val runMethod = cc.getDeclaredMethod("run")
        runMethod.insertBefore("""
            android.util.Log.e("HOOK_LOG", "run class=" + $0.getClass().getName());
        """.trimIndent())
        cc.writeFile(File(buildRoot, "jar").absolutePath)

        cc = pool["com.google.android.gms.measurement.internal.zzjb"]
        cc.defrost()
        val zzD = cc.getDeclaredMethod("zzD")
        zzD.setBody("""
            {
                android.util.Log.e("HOOK_LOG", "zzD return false");
                return false;
            }
        """.trimIndent())
        cc.writeFile(File(buildRoot, "jar").absolutePath)

        cc = pool["com.google.android.gms.measurement.internal.zzae"]
        cc.defrost()
        val zzy = cc.getDeclaredMethod("zzy")
        zzy.insertAfter("""
            android.util.Log.e("HOOK_LOG", "zzy return " + ${'$'}_);
            ${'$'}_ = false;
        """.trimIndent())
        cc.writeFile(File(buildRoot, "jar").absolutePath)

    }

}