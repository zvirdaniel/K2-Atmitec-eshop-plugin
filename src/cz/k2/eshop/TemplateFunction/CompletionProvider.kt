package cz.k2.eshop.TemplateFunction

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ProcessingContext
import cz.k2.eshop.Base.findAllChildrenFiles

class CompletionProvider : CompletionProvider<CompletionParameters>() {
	public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
		val project = parameters.editor.project
		if (project != null) {
			listOf("standard", "special").forEach {
				ProjectRootManager.getInstance(project).contentRoots[0]
						.findChild(it)
						?.findChild("views")
						?.let { findAllChildrenFiles(it) }
						?.forEach { resultSet.addElement(generateElement(it)) }
			}
		}
	}

	private fun generateElement(file: VirtualFile): LookupElementBuilder {
		val presentableUrl = file.presentableUrl.replace("\\", "/")
		val result = presentableUrl.substringAfter("views/").substringBefore('.')

		return LookupElementBuilder.create(result)
	}
}