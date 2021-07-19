package com.mxtech.quiet

import javassist.ClassPool
import javassist.CtNewMethod
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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

@Throws(IOException::class)
private fun unzip(file: File, root: File) {
    val zipFile = ZipFile(file)
    val entries = zipFile.entries()
    while (entries.hasMoreElements()) {
        val zipEntry = entries.nextElement()
        val target = File(root, zipEntry.name)
        if (zipEntry.isDirectory) {
            target.mkdir()
        } else {
            target.parentFile.mkdirs()
            val inputStream = zipFile.getInputStream(zipEntry)
            val readBytes = inputStream.readBytes()
            target.writeBytes(readBytes)
        }
    }
}

private fun listFile(dir: File, callback: (File) -> Unit) {
    val listFiles = dir.listFiles()
    if (listFiles != null) {
        for (file in listFiles) {
            if (file.isFile) {
                callback.invoke(file)
            } else if (file.isDirectory) {
                listFile(file, callback)
            }
        }
    }
}

private fun jar(path: String, target: String) {
    val file = File(target)
    val command = "jar -cvf ${file.name} $path"
    val exec = execM("cd $path", command)
    println(exec)
}


private fun zipWithFile(path: String, target: String) {
    val fos = FileOutputStream(target)
    val zip = ZipOutputStream(fos)
    val file = File(path)
    val rootPath = file.absolutePath
    val callback = { f: File ->
        val absolutePath = f.absolutePath
        val index = absolutePath.indexOf(rootPath)
        var name = absolutePath.substring(index + 1 + rootPath.length)
        if (f.isDirectory) {
            name += "/"
        }
        val zipEntry = ZipEntry(name)
        zip.putNextEntry(zipEntry)

        if (f.isFile) {
            zip.write(f.readBytes())
            zip.flush()
        }

        zip.closeEntry()
    }
    listFile(file, callback)

    zip.flush()
    zip.close()
}


@Throws(Exception::class)
private fun interactivemedia(root: File) {
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