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
        val project = parameters.editor.project
        val file = parameters.originalFile.virtualFile
        val root: Array<out VirtualFile>? = project?.let { ProjectRootManager.getInstance(it).getContentSourceRoots() };
        val fileList = root?.asList()

        resultSet.addElement(LookupElementBuilder.create("aaa_auto"))
    }
}