package com.mxtech.libs.change

import com.mxtech.quiet.common.unzip
import com.mxtech.quiet.common.zipWithFile
import java.io.File

/**
 * Created by pengwei.liao on 2021/8/4.
 */
abstract class BaseChanger {

    fun change() {
        val root = File("./playservicelibs")
        val buildRoot = File(root, "build/play")

        buildRoot.deleteRecursively()

        unzip(File(root, libName()), File(buildRoot, "aar"))
        unzip(File(buildRoot, "aar/classes.jar"), File(buildRoot, "jar"))

        changeLib(buildRoot)

        val file = File(buildRoot, "classes.changed.jar")
        zipWithFile(File(buildRoot, "jar").absolutePath, file.absolutePath)

        val target = File(buildRoot, "aar/classes.jar")

        file.copyTo(target, true)

        zipWithFile(File(buildRoot, "aar").absolutePath, File(buildRoot, "${libName()}.changed.aar").absolutePath, true)

        println("update lib completed!!")
    }

    abstract fun libName(): String

    abstract fun changeLib(buildRoot: File)
}