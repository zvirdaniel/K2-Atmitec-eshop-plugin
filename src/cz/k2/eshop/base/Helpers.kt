package cz.k2.eshop.base

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

/**
 * Creates the required file structure for the given file.
 * @param filePath String relative path of the new file, e.g. /special/views/common/abc.phtml
 * @param projectBase VirtualFile the current project directory
 * @return VirtualFile of the last folder in the tree
 */
fun buildSpecialDirectoryTree(filePath: String, projectBase: VirtualFile): VirtualFile? {
	val pathDirs = filePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
	var currentDirectory = projectBase

	// Index 0 is an empty string, because the path begins with a slash, and the last index is the name of the file
	for (i in 1 until pathDirs.size - 1) {
		var child = currentDirectory.findChild(pathDirs[i])

		if (child == null) {
			try {
				child = currentDirectory.createChildDirectory(null, pathDirs[i])
			} catch (e1: IOException) {
				return null
			}
		}

		currentDirectory = child
	}

	return currentDirectory
}

/**
 * Cuts out the project path from the absolute one, which generates a relative path
 */
fun relativePathInProject(absolutePath: String, project: Project): String {
	val basePath = project.basePath ?: return absolutePath

	return absolutePath.substring(basePath.length)
}

/**
 * Opens the given file in the file editor
 */
fun openFileEditor(targetFile: VirtualFile, project: Project): Array<out FileEditor> {
	return FileEditorManager.getInstance(project).openFile(targetFile, true, true)
}
