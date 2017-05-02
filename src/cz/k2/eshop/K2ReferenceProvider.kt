package cz.k2.eshop

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext

/**
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 26.4.17.
 */
class K2ReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val specialGetTemplateReference = SpecialGetTemplateReference(element)
        val standardGetTemplateReference = StandardGetTemplateReference(element)
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