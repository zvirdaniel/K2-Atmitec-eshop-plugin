package cz.k2.eshop.TemplateFunction.References

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

/**
 * Created by Daniel Zvir on 14.7.17.
 */
open class BasicReference(e: PsiElement, var target: PsiElement? = null) : PsiReferenceBase<PsiElement>(e) {
	fun notNull(): Boolean = target != null

	override fun resolve(): PsiElement? = target

    override fun getVariants(): Array<Any?> = arrayOfNulls(0)
}