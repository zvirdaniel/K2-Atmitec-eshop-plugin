package cz.k2.eshop.views

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ProcessingContext

class CompletionProvider : CompletionProvider<CompletionParameters>() {
	public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
		val project = parameters.editor.project
		if (project != null) {
			listOf("standard", "special").forEach {
				ProjectRootManager.getInstance(project).contentRoots[0]
						.findChild(it)
						?.findChild("views")
						?.let { findAllChildrenFiles(it) }
						?.forEach { resultSet.addElement(generateLookupElement(it)) }
			}
		}
	}

	private fun generateLookupElement(file: VirtualFile): LookupElementBuilder {
		val presentableUrl = file.presentableUrl.replace("\\", "/")
		val result = presentableUrl.substringAfter("views/").substringBefore('.')

		return LookupElementBuilder.create(result)
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
}