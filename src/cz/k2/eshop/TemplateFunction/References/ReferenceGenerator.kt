package cz.k2.eshop.TemplateFunction.References

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference

/**
 * Created by Daniel Zvir on 24.11.17.
 */
class ReferenceGenerator(private val e: PsiElement) {
	fun getStandardReferences(): List<PsiReference> {
		val standardReferences = mutableListOf<PsiReference>()
		val project = e.project

		val parameterData = e.text.substring(1, e.textLength - 1)
		val targetPath = "standard/views/$parameterData"
		val targetFolder = project.baseDir.findFileByRelativePath(targetPath + ".phtml")?.parent

		if (targetFolder != null) {
			for (file in targetFolder.children) {
				if (!file.isDirectory && file.extension == "phtml" && file.path.contains(targetPath)) {
					val psiFile = PsiManager.getInstance(project).findFile(file)
					val reference = BasicReference(e, psiFile)
					standardReferences.add(reference)
				}
			}
		}

		return standardReferences.toList()
	}

	fun getStandardReference(): PsiReference {
		val project = e.project
		val parameterData = e.text.substring(1, e.textLength - 1)
		val targetFilePath = "standard/views/$parameterData.phtml"
		val targetFile = project.baseDir.findFileByRelativePath(targetFilePath)
		val psiFile = targetFile?.let { PsiManager.getInstance(project).findFile(it) }
		val reference = BasicReference(e, psiFile)
		return reference
	}
}