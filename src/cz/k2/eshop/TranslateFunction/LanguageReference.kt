package cz.k2.eshop.TranslateFunction

import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement
import cz.k2.eshop.Base.BasicReference

// TODO: check if works
class LanguageReference(element: PsiElement, path: String) : BasicReference(element) {
    init {
        val project = myElement.project
        val languageCode = myElement.text.substring(1, myElement.textLength - 1)
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(path)
	    val psiFile = virtualFile?.let { PsiManager.getInstance(project).findFile(it) }

	    psiFile?.let {
		    val text = it.text
		    if (text.contains(languageCode)) {
			    val langCodeOffset = text.indexOf(languageCode, ignoreCase = true)
			    val elementAtOffset = LanguagePsiElement(psiFile.virtualFile, langCodeOffset)
			    target = elementAtOffset
            }
        }
    }
}

class LanguagePsiElement(val file: VirtualFile, val offset: Int) : FakePsiElement() {
	/**
	 * Returns the parent of the PSI element.

	 * @return the parent of the element, or null if the element has no parent.
	 */
	override fun getParent(): PsiFile? {
		return null
	}

	override fun navigate(requestFocus: Boolean) {
		PsiNavigationSupport.getInstance().createNavigatable(project, file, offset).navigate(requestFocus)
	}
}