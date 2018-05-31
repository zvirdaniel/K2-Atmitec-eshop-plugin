package cz.k2.eshop.translations

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
		val elementWithTranslateFunctionName = object : PsiNamePatternCondition<PsiElement>(
				"withFunctionName", StandardPatterns.string().matches("Translate")) {
			override fun getPropertyValue(o: Any): String? {
				return (o as? FunctionReference)?.name
			}
		}

		val elementParameterList = psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
		val elementFunctionCall = psiElement().withElementType(PhpElementTypes.FUNCTION_CALL)

		val translatePattern = psiElement().withParent(
				elementParameterList.withParent(
						elementFunctionCall.with(
								elementWithTranslateFunctionName
						)
				)
		)


		val elementString = psiElement().withElementType(PhpElementTypes.STRING)
		val elementArrayIndex = psiElement().withElementType(PhpElementTypes.ARRAY_INDEX)
		val elementArrayAccessExpression = psiElement().withElementType(PhpElementTypes.ARRAY_ACCESS_EXPRESSION)

		val globalsLngPattern = elementString.withParent(
				elementArrayIndex.withParent(
						elementArrayAccessExpression.withFirstChild(
								elementArrayAccessExpression.withText("\$GLOBALS['Lng']")
						)
				)
		)

		registrar.registerReferenceProvider(translatePattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
		registrar.registerReferenceProvider(globalsLngPattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
	}
}