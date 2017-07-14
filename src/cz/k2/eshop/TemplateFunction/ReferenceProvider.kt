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
        val specialReference = SpecialReference(element)
        val standardReference = StandardReference(element)
        val references: MutableList<PsiReference> = mutableListOf<PsiReference>()

        if (specialReference.notNull()) references.add(specialReference)
        if (standardReference.notNull()) references.add(standardReference)

        if (references.isEmpty()) return PsiReference.EMPTY_ARRAY
        return references.toTypedArray()
    }
}