package cz.k2.eshop.TemplateFunction

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import cz.k2.eshop.Base.BasicReference

/**
 * Created by Daniel Zvir on 26.4.17.
 */
class SpecialReference(element: PsiElement) : BasicReference(element) {
    init {
        val project = myElement.project
        val parameterData = myElement.text.substring(1, myElement.textLength - 1)
        val destinationString = "special/views/$parameterData.phtml"
        val virtualFile = project.baseDir.findFileByRelativePath(destinationString)
        result = virtualFile?.let { PsiManager.getInstance(project).findFile(it) }
    }
}