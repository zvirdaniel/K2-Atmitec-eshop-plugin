package cz.k2.eshop.base

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class BasicReference(origin: PsiElement, private val target: PsiElement) : PsiReferenceBase<PsiElement>(origin) {
	override fun getVariants(): Array<Any?> {
		return arrayOfNulls(0)
	}

	override fun resolve(): PsiElement {
		return target
	}
}
