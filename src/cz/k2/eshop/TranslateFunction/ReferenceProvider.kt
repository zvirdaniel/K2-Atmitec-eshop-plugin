package cz.k2.eshop.TranslateFunction

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import cz.k2.eshop.Base.BasicReference

class ReferenceProvider : PsiReferenceProvider() {
	override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
		val references = mutableListOf<PsiReference>()

		listOf("standard", "special").forEach { folder ->
			ProjectRootManager.getInstance(element.project).contentRoots[0].
					findChild(folder)?.findChild("pages")?.findChild("language")?.children
					?.mapNotNullTo(references) { generateReference(element, it) }
		}

		return references.toTypedArray()
	}

	private fun generateReference(psiElement: PsiElement, file: VirtualFile): PsiReference? {
		val project = psiElement.project
		val languageCode = psiElement.text.substring(1, psiElement.textLength - 1)
		val psiFile = PsiManager.getInstance(project).findFile(file)
		var reference: BasicReference? = null

		if (psiFile != null && psiFile.text.contains(languageCode)) {
			val offset = psiFile.text.indexOf(languageCode, ignoreCase = true)
			val elementAtOffset = psiFile.findElementAt(offset)
			reference = elementAtOffset?.let { BasicReference(psiElement, it) }
		}

		return reference
	}
}