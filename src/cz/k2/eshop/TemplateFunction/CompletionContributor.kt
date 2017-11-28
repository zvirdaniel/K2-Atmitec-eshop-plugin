package cz.k2.eshop.TemplateFunction

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiNamePatternCondition
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class CompletionContributor : CompletionContributor() {
	init {
		val getTemplateCall = object :
				PsiNamePatternCondition<PsiElement>("withFunctionName", StandardPatterns.string().matches("GetTemplate")) {
			override fun getPropertyValue(o: Any): String? {
				return if (o is FunctionReference) o.name else null
			}
		}

		val elementWithParentString = psiElement().withParent(StringLiteralExpression::class.java)
		val elementWithParameterList = psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
		val elementWithGetTemplateCall = psiElement().withElementType(PhpElementTypes.FUNCTION_CALL).with(getTemplateCall)
		val elementWithConcatenationExpression = psiElement().withElementType(PhpElementTypes.CONCATENATION_EXPRESSION)

		val noSuffixPattern = elementWithParentString.withSuperParent(2,
				elementWithParameterList.withParent(
						elementWithGetTemplateCall
				)
		)

		val someSuffixPattern = elementWithParentString.withSuperParent(2,
				elementWithConcatenationExpression.withParent(
						elementWithParameterList.withParent(
								elementWithGetTemplateCall
						)
				)
		)

		extend(CompletionType.BASIC, noSuffixPattern, CompletionProvider())
		extend(CompletionType.BASIC, someSuffixPattern, CompletionProvider())
	}
}