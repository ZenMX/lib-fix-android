package com.mxtech.quiet.common

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream


@Throws(IOException::class)
fun unzip(file: File, root: File) {
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

fun listFile(dir: File, listDir: Boolean, callback: (File) -> Unit) {
    val listFiles = dir.listFiles()
    if (listFiles != null) {
        for (file in listFiles) {
            if (file.isFile) {
                callback.invoke(file)
            } else if (file.isDirectory) {
                if (listDir) {
                    callback.invoke(file)
                }
                listFile(file, listDir, callback)
            }
        }
    }
}

fun zipWithFile(path: String, target: String, addDir: Boolean = false) {
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
    listFile(file, addDir, callback)

    zip.flush()
    zip.close()
}
