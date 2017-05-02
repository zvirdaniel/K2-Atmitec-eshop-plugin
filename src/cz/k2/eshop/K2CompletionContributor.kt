package cz.k2.eshop

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
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 26.4.17.
 */
class K2CompletionContributor : CompletionContributor() {
    init {
        val templateMethodPattern = object : PsiNamePatternCondition<PsiElement>("withFunctionName", StandardPatterns.string().matches("GetTemplate")) {
            override fun getPropertyValue(o: Any): String? {
                return if (o is FunctionReference) o.name else null
            }
        }

        val psiWithParentString = psiElement().withParent(StringLiteralExpression::class.java)
        val psiWithTypeParamList = psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
        val psiWithGetTemplateCall = psiElement().withElementType(PhpElementTypes.FUNCTION_CALL).with(templateMethodPattern)

        val elementPattern = psiWithParentString.withSuperParent(2, psiWithTypeParamList).withSuperParent(3, psiWithGetTemplateCall)

        extend(CompletionType.BASIC, elementPattern, K2CompletionProvider())
    }
}