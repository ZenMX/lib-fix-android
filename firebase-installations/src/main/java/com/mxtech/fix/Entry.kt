package com.mxtech.fix

import java.io.File
import com.mxtech.quiet.common.*
import javassist.ClassPool
import javassist.CtNewMethod

fun main() {
    val root = File("./firebase-installations")
    val buildRoot = File(root, "build/play")

    val baseName = "firebase-installations-17.0.3"
    buildRoot.deleteRecursively()

    unzip(File(root, "${baseName}.aar"), File(buildRoot, "aar"))
    unzip(File(buildRoot, "aar/classes.jar"), File(buildRoot, "jar"))

    val pool = ClassPool.getDefault()
    val libName = "classes.jar"

    val source = File(buildRoot, "aar/$libName")

    pool.appendClassPath(source.absolutePath)
    val cc = pool["com.google.firebase.installations.remote.FirebaseInstallationServiceClient"]

    val run = cc.getDeclaredMethod("writeFIDCreateRequestBodyToOutputStream")
    val runThr = CtNewMethod.copy(run, "writeFIDCreateRequestBodyToOutputStreamThr", cc, null)

    cc.addMethod(runThr)

    //SecurityException
    run.setBody(
        "try {" +
                "$0.writeFIDCreateRequestBodyToOutputStreamThr($1, $2, $3);" +
                "}catch(java.lang.SecurityException e) {" +
                    "throw new java.io.IOException(e);" +
                "}"
    )

    cc.writeFile(File(buildRoot, "jar").absolutePath)

    val file = File(buildRoot, "classes.fixed.jar")
    zipWithFile(File(buildRoot, "jar").absolutePath, file.absolutePath)

    val target = File(buildRoot, "aar/classes.jar")

    file.copyTo(target, true)

    zipWithFile(File(buildRoot, "aar").absolutePath, File(buildRoot, "${baseName}.aar.fixed.aar").absolutePath, true)

    println(cc.name)
}