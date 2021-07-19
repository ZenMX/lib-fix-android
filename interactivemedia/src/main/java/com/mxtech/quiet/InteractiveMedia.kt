package com.mxtech.quiet

import javassist.ClassPool
import javassist.CtNewMethod
import java.io.*
import com.mxtech.quiet.common.*

fun execM(vararg commands: String): String {
    var returnString = ""
    var pro: Process? = null
    val runTime = Runtime.getRuntime()
    if (runTime == null) {
        System.err.println("Create runtime false!")
    }

    for (command in commands) {
        try {
            pro = runTime!!.exec(command)
            val input = BufferedReader(InputStreamReader(pro.inputStream))
            val output = PrintWriter(OutputStreamWriter(pro.outputStream))
            var line: String?
            while (input.readLine().also { line = it } != null) {
                returnString = """
                $returnString$line
                
                """.trimIndent()
            }


        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

//    input.close()
//    output.close()
    pro?.destroy()

    return returnString
}

@Throws(InterruptedException::class)
fun exec(command: String): String {
    var returnString = ""
    var pro: Process? = null
    val runTime = Runtime.getRuntime()
    if (runTime == null) {
        System.err.println("Create runtime false!")
    }
    try {
        pro = runTime!!.exec(command)
        val input = BufferedReader(InputStreamReader(pro.inputStream))
        val output = PrintWriter(OutputStreamWriter(pro.outputStream))
        var line: String?
        while (input.readLine().also { line = it } != null) {
            returnString = """
                $returnString$line
                
                """.trimIndent()
        }
        input.close()
        output.close()
        pro.destroy()
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
    return returnString
}


@Throws(Exception::class)
private fun interactivemedia(root: File) {
    println("start fix interactivemedia")
    val pool = ClassPool.getDefault()
    val libName = "interactivemedia-3.19.4.jar"

    val buildRoot = File(root, "build/$libName")

    buildRoot.mkdirs()
    buildRoot.deleteRecursively()

    val source = File(root, libName)
    unzip(source, buildRoot)

    val extension = source.extension

    pool.appendClassPath(source.absolutePath)
    val cc = pool["com.google.ads.interactivemedia.v3.internal.aka"]
    val a = cc.getDeclaredMethod("a")
    val aSafe = CtNewMethod.copy(a, "aThr", cc, null)
    cc.addMethod(aSafe)

    val directoryName = buildRoot.absolutePath
    a.setBody(
        "try {" +
                "return $0.aThr($1);" +
                "}catch(java.lang.NullPointerException e) { throw new java.io.IOException(e); }"
    )
    cc.writeFile(directoryName)

    zipWithFile(buildRoot.absolutePath, File(root, "build/$libName.fixed.$extension").absolutePath)
}


fun main() {
    try {
        var root = File("./")
        root = File(root, "/interactivemedia/data")
        interactivemedia(root)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}