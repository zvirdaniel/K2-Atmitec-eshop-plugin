package cz.k2.eshop.classes.actions

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import cz.k2.eshop.base.BasicAction
import cz.k2.eshop.base.relativePathInProject

class CreateSpecialClassInPages : BasicAction() {
	override fun getSpecialPathFromStandard(selectedFile: VirtualFile, project: Project): String? {
		val file = getBaseFileToCopy(selectedFile, project)

		return file?.path?.replace("/standard/", "/special/")
	}

	override fun isActionVisibleInFile(selectedFile: VirtualFile, project: Project): Boolean {
		val relativePath = relativePathInProject(selectedFile.path, project)

		return relativePath.startsWith("/standard/pages/") && !relativePath.startsWith("/standard/pages/languages")
	}

	override fun getBaseFileToCopy(selectedFile: VirtualFile, project: Project): VirtualFile? {
		val relativePath = relativePathInProject(selectedFile.path, project)
		val pathParts = relativePath.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

		// Guide:
		// 0 => ""
		// 1 => "standard"
		// 2 => "pages"
		// 3 => "forms"
		// 4 => "core"
		// 5 => "AddressInvoiceCore.php"

		return if (pathParts[4] == "core") {
			project.baseDir.findFileByRelativePath("standard/pages/" + pathParts[3] + "/" + pathParts[5].replace("Core.php", ".php"))
		} else {
			project.baseDir.findFileByRelativePath("standard/pages/" + pathParts[3] + "/" + pathParts[4])
		}

	}
}