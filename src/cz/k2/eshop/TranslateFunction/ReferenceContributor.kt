package cz.k2.eshop.TranslateFunction

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiNamePatternCondition
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.FunctionReference

class ReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
		val translateFunctionName = object : PsiNamePatternCondition<PsiElement>(
				"withFunctionName", StandardPatterns.string().matches("Translate")) {
			override fun getPropertyValue(o: Any): String? {
				return (o as? FunctionReference)?.name
			}
		}

		val elementWithParameterList = psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
		val elementWithFunctionCall = psiElement().withElementType(PhpElementTypes.FUNCTION_CALL)

		val pattern = psiElement().withParent(
				elementWithParameterList.withParent(
						elementWithFunctionCall.with(
								translateFunctionName
						)
				)
		)

		registrar.registerReferenceProvider(pattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
	}
}