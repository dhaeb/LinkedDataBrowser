package de

import java.io.File

/**
 * Created by dhaeb on 28.05.15.
 */
package object aksw {

  def deleteRecursively(f: File): Unit = {
    if (f.exists()) {
      if (f.isDirectory()) {
        f.listFiles().foreach {
          deleteRecursively(_)
        }
      }
      f.delete()
    }
  }
}
