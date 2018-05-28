package cz.k2.eshop.views

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
		val getTemplateFunctionName = object : PsiNamePatternCondition<PsiElement>(
				"withFunctionName", StandardPatterns.string().matches("GetTemplate")) {
			override fun getPropertyValue(o: Any): String? {
				return (o as? FunctionReference)?.name
			}
		}

		val elementWithGetTemplateCall = psiElement().withElementType(PhpElementTypes.FUNCTION_CALL)
		val elementWithString = psiElement().withElementType(PhpElementTypes.STRING)
		val elementWithParameterList = psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
		val elementWithConcatenationExpression = psiElement().withElementType(PhpElementTypes.CONCATENATION_EXPRESSION)

		val noSuffixPattern = elementWithString.withParent(
				elementWithParameterList.withParent(
						elementWithGetTemplateCall.with(
								getTemplateFunctionName
						)
				)
		)

		val someSuffixPattern = elementWithString.withParent(
				elementWithConcatenationExpression.withParent(
						elementWithParameterList.withParent(
								elementWithGetTemplateCall.with(
										getTemplateFunctionName
								)
						)
				)
		)

		registrar.registerReferenceProvider(noSuffixPattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
		registrar.registerReferenceProvider(someSuffixPattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
	}
}