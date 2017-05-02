package cz.k2.eshop

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase

/**
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 26.4.17.
 */
class StandardGetTemplateReference internal constructor(element: PsiElement) : PsiReferenceBase<PsiElement>(element) {
    val project = myElement.project
    val parameterData = myElement.text.substring(1, myElement.textLength - 1)
    val destinationString = "standard/views/$parameterData.phtml"
    val virtualFile = project.baseDir.findFileByRelativePath(destinationString)
    val psiFile: PsiFile? = virtualFile?.let { PsiManager.getInstance(project).findFile(it) }

    override fun resolve(): PsiElement? = psiFile

    // Used for autocompletion
    override fun getVariants(): Array<Any?> {
        return arrayOfNulls(0)
    }
}
