package cz.k2.eshop.TemplateFunction

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ProcessingContext
import cz.k2.eshop.Base.findAllChildrenFiles
import cz.k2.eshop.Base.findFolder

/**
 * Created by Daniel Zvir on 26.4.17.
 */
class CompletionProvider : CompletionProvider<CompletionParameters>() {
    public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
        val project = parameters.editor.project
        if (project != null) {
            val files = ProjectRootManager.getInstance(project).contentSourceRoots.asList()

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
    }

    fun findElement(file: VirtualFile): LookupElementBuilder {
        val presentableUrl = file.presentableUrl.replace("\\", "/")
        val result = presentableUrl.substringAfter("views/").substringBefore('.')

        return LookupElementBuilder.create(result)
    }
}