package com.mxtech.fix

import java.io.File
import com.mxtech.quiet.common.*
import javassist.ClassPool
import javassist.CtNewMethod

fun main() {
    val root = File("./playservicebase")
    val buildRoot = File(root, "build/play")

    buildRoot.deleteRecursively()

    unzip(File(root, "play-services-base-17.1.0.aar"), File(buildRoot, "aar"))
    unzip(File(buildRoot, "aar/classes.jar"), File(buildRoot, "jar"))

    val pool = ClassPool.getDefault()
    val libName = "classes.jar"

    val source = File(buildRoot, "aar/$libName")

    pool.appendClassPath(source.absolutePath)
    val cc = pool["com.google.android.gms.common.api.internal.zaau"]

    val run = cc.getDeclaredMethod("run")
    val runThr = CtNewMethod.copy(run, "runThr", cc, null)

    cc.addMethod(runThr)

    //SecurityException
    run.setBody(
        "try {" +
                "$0.runThr();" +
                "}catch(java.lang.SecurityException e) {}"
    )

    cc.writeFile(File(buildRoot, "jar").absolutePath)

    val file = File(buildRoot, "classes.fixed.jar")
    zipWithFile(File(buildRoot, "jar").absolutePath, file.absolutePath)

    val target = File(buildRoot, "aar/classes.jar")

    val readBytes = file.readBytes()
    target.writeBytes(readBytes)


    zipWithFile(File(buildRoot, "aar").absolutePath, File(buildRoot, "play-services-base-17.1.0.aar.fixed.aar").absolutePath, true)

    println(cc.name)
}