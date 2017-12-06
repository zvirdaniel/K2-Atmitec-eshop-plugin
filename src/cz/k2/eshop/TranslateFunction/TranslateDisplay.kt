package cz.k2.eshop.TranslateFunction

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.TextRange
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

	private fun getAndDisplayTranslation(id: String, project: Project): String? {
		val folders = listOf("standard", "special")
		for (folder in folders) {
			val file = ProjectRootManager.getInstance(project).contentRoots[0].findChild(folder)
					?.findChild("pages")?.findChild("language")?.findChild("texts_cs.php")

			val document = file?.let { FileDocumentManager.getInstance().getDocument(it) }
			if (file != null && document != null) {
				val lines = document.lineCount - 1
				for (lineNumber in 0..lines) {
					val lineStart = document.getLineStartOffset(lineNumber)
					val lineEnd = document.getLineEndOffset(lineNumber)
					val line = document.getText(TextRange(lineStart, lineEnd)).trim()

					if (line.contains("\$Lng['$id']", true)) {
						val separated = line.split("=")
						var translation = separated[1].removeSuffix(";")
						translation = translation.removeRange(0..1)
						val length = translation.length - 1
						translation = translation.removeRange(length..length)
						val text = "CS $folder: $translation"
						displayStatusBarText(text, project)
						break
					} else {
						hideStatusBarText(project)
					}
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