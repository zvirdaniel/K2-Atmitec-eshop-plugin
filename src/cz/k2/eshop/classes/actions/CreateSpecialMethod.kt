package cz.k2.eshop.classes.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.impl.ParameterImpl
import com.jetbrains.php.lang.psi.elements.impl.ParameterListImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpClassImpl
import cz.k2.eshop.base.BasicAction
import cz.k2.eshop.base.openFileEditor
import cz.k2.eshop.base.relativePathInProject
import cz.k2.eshop.views.actions.CreateSpecialView

class CreateSpecialMethod : AnAction() {
	override fun actionPerformed(e: AnActionEvent) {
		ApplicationManager.getApplication().runWriteAction { copyMethod(e) }
	}

	override fun update(event: AnActionEvent?) {
		val psiFile = event?.dataContext?.let { CommonDataKeys.PSI_FILE.getData(it) }

		if (psiFile == null) {
			event?.presentation?.isEnabledAndVisible = false
			return
		}

		val isMethodExisting = findPsiElementFromActionEvent(event) != null
		val isClassInStandardApp = psiFile.virtualFile.path.contains("/standard/App/")
		val isClassInStandardPages = psiFile.virtualFile.path.contains("/standard/pages/") &&
				!psiFile.virtualFile.path.contains("/standard/pages/languages")

		event.presentation.isEnabledAndVisible = (isClassInStandardApp || isClassInStandardPages) && isMethodExisting
	}

	private fun copyMethod(e: AnActionEvent) {
		// Initializing the PsiElement on the cursor position, loading the method around the curosr
		val psi = CommonDataKeys.PSI_FILE.getData(e.dataContext) ?: return
		val method = findPsiElementFromActionEvent(e) ?: return
		val project = psi.project
		val selectedFile = psi.virtualFile

		// A special file must exist in order to copy the method into it
		val action = findFileAction(selectedFile, project) ?: return
		if (!action.isSpecialFileExisting(psi)) {
			action.createSpecial(psi)
		}

		// Loading the paths to the special file
		val path = action.getSpecialPathFromStandard(psi.virtualFile, project)
		val relativePath = path?.let { relativePathInProject(it, project) }
		val specialFile = relativePath?.let { project.baseDir.findFileByRelativePath(it) } ?: return
		val psiFile = PsiManager.getInstance(project).findFile(specialFile)

		// Loading the class in the special file
		val phpClassImpl = PsiTreeUtil.findChildOfType(psiFile, PhpClassImpl::class.java) ?: return

		// Adding the method to the end of the file
		WriteCommandAction.runWriteCommandAction(project) {
			val newMethod = phpClassImpl.addBefore(method, phpClassImpl.lastChild)
			addCommentsToMethod(newMethod)
			openFileEditor(specialFile, project)
		}
	}

	/**
	 * Adds to-do comments to the method
	 */
	private fun addCommentsToMethod(method: PsiElement) {
		// Finding the element with the method name
		var child: PsiElement = method.firstChild ?: return
		while (child.javaClass.name != "com.intellij.psi.impl.source.tree.LeafPsiElement" || (child as LeafPsiElement).elementType.toString() != "identifier") {
			child = child.nextSibling
		}

		// Finding the parameter list of the method
		val parameters = StringBuilder()
		val methodName: PsiElement = child
		while ((child as PsiElement).javaClass.name != "com.jetbrains.php.lang.psi.elements.impl.ParameterListImpl") {
			child = (child as PsiElement).nextSibling
		}

		val params = child as ParameterListImpl
		for (psiElement in params.children) {
			parameters.append(", $")
			parameters.append((psiElement as ParameterImpl).name)
		}

		var parametersString = parameters.toString()
		if (parametersString.isNotEmpty()) {
			parametersString = parametersString.substring(2)
		}

		// Preparing the comments
		val comments = arrayOf("// TODO: Consider using parent::" + methodName.text + "(" + parametersString + ") instead of copying code.",
				"// TODO: Remember to describe the purpose of the special in a comment and how does the special modify the standard behavior.",
				"// TODO: Remember to add missing uses. If you are using PhpStorm press Alt + Enter on highlighted classes.")

		// Creating the comments
		for (i in comments.indices.reversed()) {
			val comment = PhpPsiElementFactory.createFromText(method.project, PsiComment::class.java, comments[i])
			comment?.let { method.lastChild.addAfter(it, method.lastChild.firstChild) }
			val enter = PhpPsiElementFactory.createFromText(method.project, PsiWhiteSpace::class.java, "\t\t")
			enter?.let { method.lastChild.addAfter(it, method.lastChild.firstChild) }
		}
	}

	/**
	 * Finds the correct class, which is able to create a special file for the given class
	 * @param file VirtualFile where should the special file be
	 */
	private fun findFileAction(file: VirtualFile, project: Project): BasicAction? {
		val actions = arrayOf(CreateSpecialClassInApp(), CreateSpecialClassInPages(), CreateSpecialView())
		for (action in actions) {
			if (action.isActionVisibleInFile(file, project)) {
				return action
			}
		}

		return null
	}

	private fun findPsiElementFromActionEvent(e: AnActionEvent): PsiElement? {
		val editor = CommonDataKeys.EDITOR.getData(e.dataContext) as EditorEx? ?: return null
		val project = editor.project ?: return null
		val psiFile = PsiManager.getInstance(project).findFile(editor.virtualFile) ?: return null
		val caretModel = editor.caretModel
		val psiElement = psiFile.findElementAt(caretModel.offset)

		return psiElement?.let { findMethod(it) }
	}

	/**
	 * Finds the method from any of the inner elements
	 */
	private fun findMethod(start: PsiElement): PsiElement? {
		while (start.parent != null) {
			if (start.javaClass.name == "com.jetbrains.php.lang.psi.elements.impl.MethodImpl") {
				return start
			}

			return findMethod(start.parent)
		}

		return null
	}
}