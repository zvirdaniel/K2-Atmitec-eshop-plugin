package cz.k2.eshop.views.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import cz.k2.eshop.base.openFileEditor
import cz.k2.eshop.base.relativePathInProject

class GoToRenderingClass : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		val psi = CommonDataKeys.PSI_FILE.getData(e.dataContext) ?: return
		val project = psi.project
		val targetFile = getTargetFile(e) ?: return

		openFileEditor(targetFile, project)
	}

	override fun update(e: AnActionEvent) {
		val virtualFile = e.let { getVirtualFileFromAnActionEvent(it) }

		if (virtualFile == null) {
			e.presentation.isEnabledAndVisible = false
			return
		}

		val isVisible = virtualFile.path.contains("/views/") && getTargetFile(e) != null
		e.presentation.isEnabledAndVisible = isVisible
	}


	private fun getTargetFile(e: AnActionEvent): VirtualFile? {
		val virtualFile = getVirtualFileFromAnActionEvent(e) ?: return null
		val psi = CommonDataKeys.PSI_FILE.getData(e.dataContext) ?: return null
		val project = psi.project
		val fileName = removeSuffixesFromName(psi.name)

		val relativePath = relativePathInProject(virtualFile.path, project)
		val parts = relativePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		val specialPath = "/special/pages/" + parts[3] + "/" + fileName + ".php"
		val standardPath = "/standard/pages/" + parts[3] + "/core/" + fileName + "Core.php"
		val projectBaseDirectory = project.baseDir ?: return null

		for (x in arrayOf(specialPath, standardPath)) {
			return projectBaseDirectory.findFileByRelativePath(x) ?: continue
		}

		return null
	}

	private fun getVirtualFileFromAnActionEvent(e: AnActionEvent): VirtualFile? {
		val psi = CommonDataKeys.PSI_FILE.getData(e.dataContext)

		if (psi == null) {
			e.presentation.isEnabledAndVisible = false
			return null
		}

		return psi.virtualFile
	}

	private fun removeSuffixesFromName(originalName: String): String {
		// Example: standard/views/banners/BannerPictureRandomHead.phtml
		val replacements = arrayOf("Head", "End", "Item", "NoShopping", "Disabled", "Empty")

		var result = originalName
		for (replacement in replacements) {
			result = result.replace(replacement, "")
		}

		return result.replace(".phtml", "")
	}
}