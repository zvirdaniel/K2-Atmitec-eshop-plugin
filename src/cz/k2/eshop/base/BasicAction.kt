package cz.k2.eshop.base

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.io.IOException

abstract class BasicAction : AnAction() {
	abstract fun getSpecialPathFromStandard(selectedFile: VirtualFile, project: Project): String?

	abstract fun isActionVisibleInFile(selectedFile: VirtualFile, project: Project): Boolean

	abstract fun getBaseFileToCopy(selectedFile: VirtualFile, project: Project): VirtualFile?

	/**
	 * Depending on the existence of the special file, opens the existing one or creates a new file and opens it
	 */
	override fun actionPerformed(e: AnActionEvent) {
		val psiFile = CommonDataKeys.PSI_FILE.getData(e.dataContext) ?: return
		val project = psiFile.project

		if (isSpecialFileExisting(psiFile.virtualFile, project)) {
			getSpecialFile(psiFile.virtualFile, project)?.let { openFileEditor(it, project) }
			return
		}

		ApplicationManager.getApplication().runWriteAction {
			val special = createSpecial(e)
			special?.let { openFileEditor(it, project) }
		}
	}

	/**
	 * Creates a special file from an action event
	 */
	private fun createSpecial(e: AnActionEvent): VirtualFile? {
		val psiFile = CommonDataKeys.PSI_FILE.getData(e.dataContext) ?: return fail()

		return createSpecial(psiFile)
	}

	/**
	 * Creates the directory structure for the given file, if required, and copies the given file
	 */
	fun createSpecial(psiFile: PsiFile): VirtualFile? {
		val selectedFile = psiFile.virtualFile

		val outputSpecialFilePath =
				getSpecialPathFromStandard(selectedFile, psiFile.project) ?: return fail()

		val outputSpecialFilePathRelative = relativePathInProject(outputSpecialFilePath, psiFile.project)
		val baseFileToCopy = getBaseFileToCopy(selectedFile, psiFile.project) ?: return fail()

		// Creating the directory tree
		val directoryToCopy = buildSpecialDirectoryTree(outputSpecialFilePathRelative, psiFile.project.baseDir)
				?: return fail("Creation of the required directory structure has failed.")


		return try {
			val fileName = outputSpecialFilePath.substring(outputSpecialFilePath.lastIndexOf("/") + 1)
			baseFileToCopy.copy(null, directoryToCopy, fileName)
		} catch (e1: IOException) {
			fail("Creation of the special file has failed.")
		}

	}

	/**
	 * Finds the file using its relative path and returns it
	 */
	private fun getSpecialFile(virtualFile: VirtualFile, project: Project): VirtualFile? {
		val specialPath = getSpecialPathFromStandard(virtualFile, project)
				?: return fail("Internal error while retrieving the special file.")

		val path = relativePathInProject(specialPath, project)

		return project.baseDir.findFileByRelativePath(path)
	}

	/**
	 * Attempts to find the file using its relative path and returns whether it exists
	 */
	private fun isSpecialFileExisting(virtualFile: VirtualFile, project: Project): Boolean {
		return getSpecialFile(virtualFile, project) != null
	}

	/**
	 * Attempts to find the file using its relative path and returns whether it exists
	 */
	fun isSpecialFileExisting(file: PsiFile): Boolean {
		return isSpecialFileExisting(file.virtualFile, file.project)
	}

	/**
	 * Shows and hides the context menu action to create a special, uses the abstract function above
	 */
	override fun update(event: AnActionEvent) {
		val psiFile = CommonDataKeys.PSI_FILE.getData(event.dataContext)

		if (psiFile == null) {
			event.presentation.isEnabledAndVisible = false
			return
		}

		val project = psiFile.project
		val virtualFile = psiFile.virtualFile
		val visibility = isActionVisibleInFile(virtualFile, project)

		event.presentation.isEnabledAndVisible = visibility

		if (visibility && isSpecialFileExisting(virtualFile, project)) {
			event.presentation.text = "Go to the special file"
		}
	}

	/**
	 * Show an error message dialog and returns null
	 * @param message String will be shown in the message dialog, implicitly: "Internal error while creating the special file."
	 */
	private fun fail(message: String = "Internal error while creating the special file."): Nothing? {
		Messages.showMessageDialog(message, "Error", Messages.getErrorIcon())
		return null
	}
}