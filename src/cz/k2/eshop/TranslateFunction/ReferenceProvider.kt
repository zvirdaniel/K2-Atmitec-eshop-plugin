package cz.k2.eshop.TranslateFunction

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import cz.k2.eshop.Base.findFolder

/**
 * Created by Daniel Zvir on 14.7.17.
 */
class ReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val files = ProjectRootManager.getInstance(element.project).contentSourceRoots.asList()
        val standardFolder = findFolder("standard", files)?.findChild("pages")?.findChild("language")?.children
        val specialFolder = findFolder("special", files)?.findChild("pages")?.findChild("language")?.children
        val languagePaths = mutableListOf<String>()

        if (standardFolder == null && specialFolder == null) return PsiReference.EMPTY_ARRAY
        if (standardFolder != null) languagePaths.addAll(extractLanguages(standardFolder))
        if (specialFolder != null) languagePaths.addAll(extractLanguages(specialFolder))
        if (languagePaths.isEmpty()) return PsiReference.EMPTY_ARRAY

        val references = mutableListOf<PsiReference>()
        for (path in languagePaths) {
            val reference = LanguageReference(element, path)
            if (reference.notNull()) references.add(reference)
        }

        if (references.isEmpty()) return PsiReference.EMPTY_ARRAY

        return references.toTypedArray()
    }

    private fun extractLanguages(folder: Array<VirtualFile>): MutableList<String> {
        val list = mutableListOf<String>()
        for (file in folder) list.add(file.path)
        return list
    }
}