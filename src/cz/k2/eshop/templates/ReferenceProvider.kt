package cz.k2.eshop.templates

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression
import cz.k2.eshop.base.BasicReference

class ReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
		val references: MutableList<PsiReference> = mutableListOf()
		val parent = element.parent

		if (parent is ConcatenationExpression) {
			references.addAll(generateReferences(element))
		} else {
			references.addAll(generateReferences(element, true))
		}

		if (references.isEmpty()) return PsiReference.EMPTY_ARRAY
		return references.toTypedArray()
	}

	private fun generateReferences(psiElement: PsiElement, single: Boolean = false): List<PsiReference> {
		val references = mutableListOf<PsiReference>()
		val project = psiElement.project
		val parameterData = psiElement.text.substring(1, psiElement.textLength - 1)
		val paths = listOf("standard/views/$parameterData", "special/views/$parameterData")

		for (path in paths) {
			if (single) {
				val targetFile = project.baseDir.findFileByRelativePath("$path.phtml")
				val psiFile = targetFile?.let { PsiManager.getInstance(project).findFile(it) }
				val reference = psiFile?.let { BasicReference(psiElement, it) }
				reference?.let { references.add(it) }
			} else {
				project.baseDir.findFileByRelativePath("$path.phtml")?.parent?.children // parent folder with phtml files
						?.filter { !it.isDirectory && it.extension == "phtml" && it.path.contains(path) } // filter the data
						?.map { PsiManager.getInstance(project).findFile(it) } // returns PsiFile
						?.mapNotNullTo(references) {
							// adds BasicReferences of PsiFiles to references list
							it?.let { BasicReference(psiElement, it) }
						}
			}
		}

		return references.toList()
	}
}