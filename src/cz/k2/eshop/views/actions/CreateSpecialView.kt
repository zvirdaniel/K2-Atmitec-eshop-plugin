package cz.k2.eshop.views.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import cz.k2.eshop.base.BasicAction
import cz.k2.eshop.base.relativePathInProject

class CreateSpecialView : BasicAction() {
	override fun getSpecialPathFromStandard(selectedFile: VirtualFile, project: Project): String {
		return selectedFile.path.replace("/standard/", "/special/")
	}

	override fun isActionVisibleInFile(selectedFile: VirtualFile, project: Project): Boolean {
		val relPath = relativePathInProject(selectedFile.path, project)
		return relPath.startsWith("/standard/views") && relPath.endsWith(".phtml")
	}

	override fun getBaseFileToCopy(selectedFile: VirtualFile, project: Project): VirtualFile {
		return selectedFile
	}
}