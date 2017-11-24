package cz.k2.eshop.TemplateFunction

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ConcatenationExpression
import cz.k2.eshop.TemplateFunction.References.ReferenceGenerator

/**
 * Created by Daniel Zvir on 26.4.17.
 */
class ReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val references: MutableList<PsiReference> = mutableListOf<PsiReference>()
	    val generator = ReferenceGenerator(element)

	    val parent = element.parent
	    if (parent is ConcatenationExpression) {
		    val standardReferences = generator.getStandardReferences()
		    references.addAll(standardReferences)
	    } else {
		    val singleReference = generator.getStandardReference()
		    references.add(singleReference)
	    }

        if (references.isEmpty()) return PsiReference.EMPTY_ARRAY
        return references.toTypedArray()
    }
}