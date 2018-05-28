package cz.k2.eshop.classes.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import cz.k2.eshop.base.BasicAction
import cz.k2.eshop.base.relativePathInProject

class CreateSpecialClassInApp : BasicAction() {
	override fun getSpecialPathFromStandard(selectedFile: VirtualFile, project: Project): String? {
		return getBaseFileToCopy(selectedFile, project)?.path?.replace("/standard/", "/special/")
	}

	override fun isActionVisibleInFile(selectedFile: VirtualFile, project: Project): Boolean {
		val relativePath = relativePathInProject(selectedFile.path, project)
		return relativePath.startsWith("/standard/App/")
	}

	override fun getBaseFileToCopy(selectedFile: VirtualFile, project: Project): VirtualFile? {
		val projectBaseDirectory = project.baseDir
		val fileName = selectedFile.name

		val basePath: String
		if (fileName.startsWith("Cache") || fileName.startsWith("WsCache")) {
			basePath = selectedFile.path.replace("/cache/WsCache", "/Cache")
		} else if (fileName.startsWith("Ws")) {
			basePath = selectedFile.path.replace("/core/Wsc", "/Ws")
		} else {
			basePath = selectedFile.path.replace("/core/", "/").replace("Core.php", ".php")
		}

		return projectBaseDirectory.findFileByRelativePath(relativePathInProject(basePath, project))
	}
}