package cz.k2.eshop

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ProcessingContext

/**
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 26.4.17.
 */
class K2CompletionProvider : CompletionProvider<CompletionParameters>() {
    public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        val files = ProjectRootManager.getInstance(parameters.editor.project!!).contentSourceRoots.asList()

        if (files.isEmpty()) return

        val standard = findFolder("standard", files)?.findChild("views")
        val special = findFolder("special", files)?.findChild("views")

        if (standard == null && special == null) return

        val standardChildren = standard?.let { findAllChildrenFiles(it) }
        val specialChildren = special?.let { findAllChildrenFiles(it) }

        standardChildren?.forEach {
            resultSet.addElement(findElement(it))
        }

        specialChildren?.forEach {
            resultSet.addElement(findElement(it))
        }
    }

    private fun findAllChildrenFiles(directory: VirtualFile): List<VirtualFile> {
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

    private fun findElement(file: VirtualFile) = LookupElementBuilder.create(file.presentableUrl.substringAfter("views/").substringBefore('.'))

    private fun findFolder(name: String, files: List<VirtualFile>): VirtualFile? {
        var result: VirtualFile? = null

        for (file in files) {
            if (file.isDirectory) {
                if (file.name == name && file.isDirectory) {
                    result = file
                }
            }
        }
        return result
    }
}