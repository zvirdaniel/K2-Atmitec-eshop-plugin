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

/**
 * Created by Daniel Zvir on 26.4.17.
 */
class CompletionContributor : CompletionContributor() {
    init {
        val templateFunctionPattern = object : PsiNamePatternCondition<PsiElement>("withFunctionName", StandardPatterns.string().matches("GetTemplate")) {
            override fun getPropertyValue(o: Any): String? {
                return if (o is FunctionReference) o.name else null
            }
        }

        val psiWithParentString = psiElement().withParent(StringLiteralExpression::class.java)
        val psiWithTypeParamList = psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
        val psiWithTemplateCall = psiElement().withElementType(PhpElementTypes.FUNCTION_CALL).with(templateFunctionPattern)

        val elementPattern = psiWithParentString.withSuperParent(2, psiWithTypeParamList).withSuperParent(3, psiWithTemplateCall)

        extend(CompletionType.BASIC, elementPattern, CompletionProvider())
    }
}