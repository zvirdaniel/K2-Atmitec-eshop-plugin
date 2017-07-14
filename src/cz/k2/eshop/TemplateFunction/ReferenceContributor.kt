package cz.k2.eshop.TemplateFunction

import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiNamePatternCondition
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.FunctionReference

/**
 * Created by Daniel Zvir on 25.4.17.
 */
class ReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val templateFunctionPattern = object : PsiNamePatternCondition<PsiElement>("withFunctionName", StandardPatterns.string().matches("GetTemplate")) {
            override fun getPropertyValue(o: Any): String? {
                return if (o is FunctionReference) o.name else null
            }
        }

        val elementPattern = psiElement().withElementType(PhpElementTypes.STRING).
                withParent(psiElement().withElementType(PhpElementTypes.PARAMETER_LIST).withParent(psiElement().
                        withElementType(PhpElementTypes.FUNCTION_CALL).with(templateFunctionPattern)))

        registrar.registerReferenceProvider(elementPattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
    }
}