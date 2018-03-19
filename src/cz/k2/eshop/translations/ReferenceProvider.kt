package cz.k2.eshop.translations

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.util.ProcessingContext
import cz.k2.eshop.base.BasicReference

class ReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
		val references = mutableListOf<PsiReference>()

		listOf("special", "standard").forEach { folder ->
			ProjectRootManager.getInstance(element.project).contentRoots[0].findChild(folder)
					?.findChild("pages")?.findChild("language")?.children
					?.mapNotNullTo(references) { generateReference(element, it) }
		}

		return references.toTypedArray()
	}

	private fun generateReference(psiElement: PsiElement, file: VirtualFile): PsiReference? {
		val project = psiElement.project
		val translationCode = psiElement.text.substring(1, psiElement.textLength - 1)
		val psiFile = PsiManager.getInstance(project).findFile(file)

		if (psiFile != null && psiFile.text.contains(translationCode)) {
			val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
			val translationCodeOffset = psiFile.text.indexOf(translationCode, ignoreCase = true)

			if (document != null) {
				val lineOffset = document.getLineNumber(translationCodeOffset)
				val lineStart = document.getLineStartOffset(lineOffset)
				val lineEnd = document.getLineEndOffset(lineOffset)
				val line = document.getText(TextRange.create(lineStart, lineEnd))

				// If line starts with double slash, it means the code is commented out, so the translation should not be shown
				if (line.trimStart().startsWith("//") || !line.contains("=")) {
					return null
				}

				val stringAfterEquals = line.substringAfter('=').trim()
				if (stringAfterEquals.isNotEmpty()) {
					val translation = if (stringAfterEquals.endsWith("';") && stringAfterEquals.startsWith("'")) {
						stringAfterEquals.removeSuffix("';").removePrefix("'")
					} else {
						stringAfterEquals.removeSuffix("\";").removePrefix("\"")
					}

					val translationOffset = psiFile.text.indexOf(translation)
					val elementAtTranslation = psiFile.findElementAt(translationOffset)
					return elementAtTranslation?.let { BasicReference(psiElement, it) }
				}
			}
		}

		return null
	}
}