package com.mxtech.appcompat
import com.mxtech.quiet.common.*
import java.io.File
import java.lang.Exception

fun fix() {
    val root = File("./appcompat")

    File(root, "build").deleteRecursively()

    unzip(File(root, "appcompat-1.3.0.aar"), File(root, "build/1.3.0/aar"))
    unzip(File(root, "appcompat-1.2.0.aar"), File(root, "build/1.2.0/aar"))

    unzip(File(root, "build/1.3.0/aar/classes.jar"), File(root, "build/1.3.0/jar"))
    unzip(File(root, "build/1.2.0/aar/classes.jar"), File(root, "build/1.2.0/jar"))

    val f12 = File(root, "build/1.2.0/jar/androidx/appcompat/widget/MenuPopupWindow\$MenuDropDownListView.class")
    val f13 = File(root, "build/1.3.0/jar/androidx/appcompat/widget/MenuPopupWindow\$MenuDropDownListView.class")
    val bytes = f13.readBytes()
    f12.writeBytes(bytes)

    val target = File(root, "build/1.2.0/classes-fixed.jar")
    zipWithFile(File(root, "build/1.2.0/jar/").absolutePath, target.absolutePath)

    val target13 = File(root, "build/1.3.0/aar/classes.jar")
    val readBytes = target.readBytes()
    target13.writeBytes(readBytes)

    zipWithFile(File(root, "build/1.3.0/aar").absolutePath, File(root, "build/appcompat-1.2.0.aar.fixed.aar").absolutePath, true)
}

fun main() {
    println("start fix appcompat.")
    try {
        fix()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}