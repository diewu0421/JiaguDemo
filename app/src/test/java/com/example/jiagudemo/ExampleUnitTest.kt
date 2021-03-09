package com.example.jiagudemo

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun readText() {
        val apkFile =
            File("/Users/apple/IntelliJIDEAProjects/JiaguDemo/app/build/outputs/apk/debug/app-debug.apk")

        val aarFile = File("/Users/apple/IntelliJIDEAProjects/JiaguDemo/mylibrary/build/outputs/aar/mylibrary-debug.aar")

        val apkFileDir = apkFile.parentFile
        val aarFileDir = aarFile.parentFile
        val tempDir = File("/Users/apple/IntelliJIDEAProjects/JiaguDemo/app/temp")
        if (tempDir.exists() && tempDir.isDirectory) {
            tempDir.listFiles().forEach {
                it.takeIf { it.exists() }?.let { it.delete()}
            }
        }

        AES.init(AES.DEFAULT_PWD)
        // apk的dex文件进行加密
//        Zip.unZip(apkFile, tempDir)
        val encryptDexFile = AES.encryptAPKFile(apkFile, tempDir)

        if (tempDir.isDirectory && tempDir.listFiles().isNotEmpty()) {
            tempDir.listFiles().filter { it.name.endsWith(".dex") }.forEach {
                it.renameTo(File(it.parentFile.absolutePath + "${File.separator}${it.nameWithoutExtension}_.dex"))
            }
        }


        // 处理aar
        val tempMainDex = File(tempDir.path + File.separator + "classes.dex")

        val aarDex = Dx.jar2Dex(aarFile)
        val fos = FileOutputStream(tempMainDex)
        val fbytes = Utils.getBytes(aarDex)
        fos.write(fbytes)
        fos.flush()
        fos.close()
//        aarDex.copyTo(File(tempDir, "classes.dex"))


        val newApkFile = File("result/apk-unsigned.apk")
        if (!newApkFile.parentFile.exists()) {
            newApkFile.parentFile.mkdirs()

        }
        Zip.zip(tempDir, newApkFile)

        val finalApkFile = File("result/apk.apk")

        Signature.signature(newApkFile, finalApkFile)

    }
}