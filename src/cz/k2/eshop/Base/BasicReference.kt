package cz.k2.eshop.Base

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

/**
 * Created by Daniel Zvir on 14.7.17.
 */
open class BasicReference(e: PsiElement) : PsiReferenceBase<PsiElement>(e) {
    var result: PsiElement? = null

    fun notNull(): Boolean = result != null

    override fun resolve(): PsiElement? = result

    override fun getVariants(): Array<Any?> = arrayOfNulls(0)
}