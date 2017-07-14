package cz.k2.eshop.TemplateFunction

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase

/**
 * Created by Daniel Zvir on 26.4.17.
 */
class SpecialReference internal constructor(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    val project = myElement.project
    val parameterData = myElement.text.substring(1, myElement.textLength - 1)
    val destinationString = "special/views/$parameterData.phtml"
    val virtualFile = project.baseDir.findFileByRelativePath(destinationString)
    val psiFile = virtualFile?.let { PsiManager.getInstance(project).findFile(it) }

    override fun resolve(): PsiElement? = psiFile

    override fun getVariants(): Array<Any?> {
        return arrayOfNulls(0)
    }
}