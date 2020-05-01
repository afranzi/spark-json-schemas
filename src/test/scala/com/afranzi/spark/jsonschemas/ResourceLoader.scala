package com.afranzi.spark.jsonschemas

import java.io.File
import java.net.URL
import java.nio.file.FileSystems

object ResourceLoader {

  // ToDo - change to tailrec
  def getFileTree(path: String): Stream[File] = {
    def getFileTree(f: File): Stream[File] = {
      if (f.isDirectory) {
        f.listFiles().toStream.flatMap(getFileTree)
      } else {
        Stream(f)
      }
    }

    val dir = FileSystems.getDefault.getPath(path).toFile
    getFileTree(dir)
  }

  def getFileResources(path: String): Stream[File] = {
    val schemasFolder: URL = getClass.getResource(path)
    getFileTree(schemasFolder.getPath).sortBy(_.getPath)
  }

}
