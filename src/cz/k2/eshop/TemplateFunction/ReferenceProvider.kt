package cz.k2.eshop.TemplateFunction

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

/**
 * Created by Daniel Zvir on 26.4.17.
 */
class ReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val specialGetTemplateReference = SpecialReference(element)
        val standardGetTemplateReference = StandardReference(element)
        val result: MutableList<PsiReference> = mutableListOf<PsiReference>()

        if (specialGetTemplateReference.psiFile != null) {
            result.add(specialGetTemplateReference)
        }

        if (standardGetTemplateReference.psiFile != null) {
            result.add(standardGetTemplateReference)
        }

        return if (result.size > 0) result.toTypedArray() else PsiReference.EMPTY_ARRAY
    }
}