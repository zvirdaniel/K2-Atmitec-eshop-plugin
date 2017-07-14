package cz.k2.eshop.TranslateFunction

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import cz.k2.eshop.Base.BasicReference

/**
 * Created by Daniel Zvir on 14.7.17.
 */
class LanguageReference(element: PsiElement, path: String) : BasicReference(element) {
    init {
        val project = myElement.project
        val languageCode = myElement.text.substring(1, myElement.textLength - 1)
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(path)
        val psiFile = virtualFile?.let { PsiManager.getInstance(element.project).findFile(it) }

        if (psiFile != null) {
            if (psiFile.text.contains(languageCode)) {
                result = virtualFile.let { PsiManager.getInstance(project).findFile(it) }
            }
        }
    }
}