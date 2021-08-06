package com.mxtech.libs.change

import javassist.ClassPool
import javassist.CtClass
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

        val classNames = collectClassName(File(buildRoot, "jar"))
        var cc: CtClass
        for(clsName in classNames) {
            cc = pool[clsName]
            if(cc.isAnnotation || cc.isEnum || cc.isInterface || cc.isInterface || cc.isArray || cc.isPrimitive) continue
            val methods = cc.methods
            for (method in methods) {
                try {
                    if (method.methodInfo.codeAttribute == null) continue
                    method.insertBefore(
                        "android.util.Log.println(2, \"FA-SVC\", \"=============${method.longName}\");" +
                                "android.util.Log.v(\"FA-SVC\", \"=============${method.longName}\");"
                    )
                } catch (e: Exception){
                    println("method:${method.longName} insert log exception")
                }
            }
            cc.writeFile(File(buildRoot, "jar").absolutePath)
        }

//        var cc = pool["com.google.android.gms.measurement.internal.zzen"]
//        val constructor = cc.constructors[0]
//        constructor.insertAfter(
//            "java.util.Iterator it = zzf.entrySet().iterator();" +
//                    "java.lang.StringBuilder sb = new java.lang.StringBuilder();" +
//                    "while (it.hasNext()) {" +
//                    "    java.util.Map.Entry entry = it.next();" +
//                    "    sb.append(entry.getKey()).append(\"=\").append(entry.getValue()).append(\";\");" +
//                    "}" +
//                    "android.util.Log.println(2, \"FA-SVC\", \"map=\" + sb.toString());"
//        )
//        var methods = cc.methods
//        for (method in methods) {
//            if (method.methodInfo.codeAttribute == null) continue
//            try {
//                method.insertBefore(
//                    "android.util.Log.println(2, \"FA-SVC\", \"=============${method.longName}\");" +
//                            "android.util.Log.v(\"FA-SVC\", \"=============${method.longName}\");"
//                )
//            } catch (e: Exception){
//                println("method:${method.longName} insert log exception")
//            }
//        }
//
//        cc.writeFile(File(buildRoot, "jar").absolutePath)
//
//        cc = pool["com.google.android.gms.measurement.internal.zzkd"]
//        val zzF = cc.getDeclaredMethod("zzF")
//        val zzFNew = CtNewMethod.copy(zzF, "zzFNew", cc, null)
//        cc.addMethod(zzFNew)
//
//        zzF.setBody(
//            "{" +
//                    "android.util.Log.println(2, \"FA-SVC\", \"==========hook upload data method============\");" +
//                    "$0.zzFNew();" +
//                    "}"
//        )
//        methods = cc.methods
//        for (method in methods) {
//            if (method.methodInfo.codeAttribute == null) continue
//            try {
//                method.insertBefore(
//                    "android.util.Log.println(2, \"FA-SVC\", \"=============${method.longName}\");" +
//                            "android.util.Log.v(\"FA-SVC\", \"=============${method.longName}\");"
//                )
//            } catch (e: Exception){
//                println("method:${method.longName} insert log exception")
//            }
//        }
//        cc.writeFile(File(buildRoot, "jar").absolutePath)
//
//        cc = pool["com.google.android.gms.measurement.internal.zzkf"]
//        methods = cc.methods
//        for (method in methods) {
//            if (method.methodInfo.codeAttribute == null) continue
//            try {
//                method.insertBefore(
//                    "android.util.Log.println(2, \"FA-SVC\", \"=============${method.longName}\");" +
//                            "android.util.Log.v(\"FA-SVC\", \"=============${method.longName}\");"
//                )
//            } catch (e: Exception){
//                println("method:${method.longName} insert log exception")
//            }
//        }
//        cc.writeFile(File(buildRoot, "jar").absolutePath)

//        val zzh = cc.getDeclaredMethod("zzh")
//        val zzhNew = CtNewMethod.copy(zzh, "zzhNew", cc, null)
//
//        cc.addMethod(zzhNew)
//
//        //SecurityException
//        zzh.setBody(
//            "{" +
//                    "android.util.Log.e(\"HOOK_LOG\",\"hook method:${zzh.longName}\", new java.lang.Exception());" +
//                    "return $0.zzhNew($$);" +
//                    "}"
//
//        )

//        cc.writeFile(File(buildRoot, "jar").absolutePath)
    }

    fun collectClassName(rootDir: File) : List<String>{
        val list = ArrayList<String>()
        val files = rootDir.listFiles()
        if(files != null && files.isNotEmpty()) {
            for(file in files) {
                if(file.isDirectory) {
                    list.addAll(collectClassName(file))
                } else {
                    if(!file.path.endsWith(".class")) continue
                    val name = file.path.replace("/", ".").replace(".class", "").replace("..playservicelibs.build.play.jar.", "")
                    list.add(name)
                }
            }
        }
        return list
    }

}