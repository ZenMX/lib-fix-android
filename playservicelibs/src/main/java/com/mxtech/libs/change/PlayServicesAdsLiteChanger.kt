package com.mxtech.libs.change

import javassist.ClassPool
import javassist.CtField
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/5.
 */
class PlayServicesAdsLiteChanger: BaseChanger() {
    override fun libName(): String {
        return "play-services-ads-lite-20.2.0.aar"
    }

    override fun changeLib(buildRoot: File) {
        val pool = ClassPool.getDefault()
        val source = File(buildRoot, "aar/classes.jar")
        pool.appendClassPath(source.absolutePath)

        val cc = pool["com.google.android.gms.internal.ads.zzbgw"]
        cc.removeField(cc.getDeclaredField("zza"))
        val zza = CtField.make("public static final zzbgs<Boolean> zza = zzbgt.zzf(\"gads:consent:gmscore:dsid:enabled\", false);", cc)
        cc.addField(zza)

        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }
}