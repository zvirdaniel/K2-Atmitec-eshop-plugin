package cz.k2.eshop.TranslateFunction

import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiNamePatternCondition
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.FunctionReference

/**
 * Created by Daniel Zvir on 14.7.17.
 */
class ReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val translateFunctionPattern = object : PsiNamePatternCondition<PsiElement>("withFunctionName", StandardPatterns.string().matches("Translate")) {
            override fun getPropertyValue(o: Any): String? {
                return if (o is FunctionReference) o.name else null
            }
        }

        val pattern = PlatformPatterns.psiElement().withParent(PlatformPatterns.psiElement().withElementType(PhpElementTypes.PARAMETER_LIST)
                .withParent(PlatformPatterns.psiElement().withElementType(PhpElementTypes.FUNCTION_CALL).with(translateFunctionPattern)))

        registrar.registerReferenceProvider(pattern, ReferenceProvider(), PsiReferenceRegistrar.DEFAULT_PRIORITY)
    }
}