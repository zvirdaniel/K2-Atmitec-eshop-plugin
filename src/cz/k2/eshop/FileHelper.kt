package cz.k2.eshop

import com.intellij.openapi.vfs.VirtualFile

/**
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 12.7.17.
 */
fun findFolder(name: String, files: List<VirtualFile>): VirtualFile? {
    var result: VirtualFile? = null
    for (file in files) {
        if (file.isDirectory) {
            if (file.name == name && file.isDirectory) {
                result = file
                break
            }
        }
    }
    return result
}

fun findAllChildrenFiles(directory: VirtualFile): List<VirtualFile> {
    val result = mutableListOf<VirtualFile>()
    for (item in directory.children) {
        if (item.isDirectory) {
            result.addAll(findAllChildrenFiles(item))
        } else {
            result.add(item)
        }
    }

    return result
}