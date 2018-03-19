package cz.k2.eshop.TranslateFunction

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class TranslateDisplay : StartupActivity {
	override fun runActivity(project: Project) {
		EditorFactory.getInstance().eventMulticaster.addCaretListener(generateCaretListener(), project)
	}

	private fun generateCaretListener(): CaretListener {
		return object : CaretListener {
			override fun caretAdded(e: CaretEvent?) {
				if (e != null) caretPositionChanged(e)
			}

			override fun caretRemoved(e: CaretEvent?) {
				if (e != null) caretPositionChanged(e)
			}

			override fun caretPositionChanged(e: CaretEvent) {
				val editor = e.editor
				val offset = editor.logicalPositionToOffset(e.newPosition)
				val project = editor.project
				if (project != null) {
					val file = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
					val element = file?.let { PsiTreeUtil.findElementOfClassAtOffset(it, offset, StringLiteralExpression::class.java, false) }
					if (element != null && element.language.displayName.toLowerCase() == "php" &&
							element.parent.parent.node.firstChildNode.text.toLowerCase() == "translate") {
						getAndDisplayTranslation(element.contents, project)
					} else {
						hideStatusBarText(project)
					}
				}
			}
		}
	}

	private fun getAndDisplayTranslation(translationCode: String, project: Project) {
		val specialFile = ProjectRootManager.getInstance(project).contentRoots[0].findChild("special")
				?.findChild("pages")?.findChild("language")?.findChild("texts_cs.php")
		val standardFile = ProjectRootManager.getInstance(project).contentRoots[0].findChild("standard")
				?.findChild("pages")?.findChild("language")?.findChild("texts_cs.php")

		if (specialFile != null) {
			val specialTranslation = getTranslation(translationCode, specialFile)
			if (specialTranslation != null) {
				val text = "CS Special: $specialTranslation"
				displayStatusBarText(text, project)
				return
			}
		}

		if (standardFile != null) {
			val standardTranslation = getTranslation(translationCode, standardFile)
			val text = "CS Standard: $standardTranslation"
			displayStatusBarText(text, project)
			return
		}

		hideStatusBarText(project)
	}

	private fun getTranslation(translationCode: String, file: VirtualFile): String? {
		val document = FileDocumentManager.getInstance().getDocument(file)

		if (document != null && document.text.contains(translationCode)) {
			val translationCodeOffset = document.text.indexOf(translationCode, ignoreCase = true)
			val lineOffset = document.getLineNumber(translationCodeOffset)
			val lineStart = document.getLineStartOffset(lineOffset)
			val lineEnd = document.getLineEndOffset(lineOffset)
			val line = document.getText(TextRange.create(lineStart, lineEnd))

			// If line starts with double slash, it means the code is commented out, so the translation should not be shown
			if (line.trimStart().startsWith("//") || !line.contains("=")) {
				return null
			}

			val stringAfterEquals = line.substringAfter('=').trim()
			if (stringAfterEquals.isNotEmpty()) {
				return if (stringAfterEquals.endsWith("';") && stringAfterEquals.startsWith("'")) {
					stringAfterEquals.removeSuffix("';").removePrefix("'")
				} else {
					stringAfterEquals.removeSuffix("\";").removePrefix("\"")
				}
			}
		}

		return null
	}

	private fun hideStatusBarText(project: Project) {
		WindowManager.getInstance().getStatusBar(project).info = null
	}

	private fun displayStatusBarText(translation: String, project: Project) {
		WindowManager.getInstance().getStatusBar(project).info = translation
	}
}