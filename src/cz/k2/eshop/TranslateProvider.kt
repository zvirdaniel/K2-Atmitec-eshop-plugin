package cz.k2.eshop

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager

/**
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 12.7.17.
 */
class TranslateProvider(val project: Project, val files: List<VirtualFile>) {
    fun hideTranslation() {
        WindowManager.getInstance().getStatusBar(project).setInfo(null)
    }

    fun displayTranslation(languageId: String) {
        val result = getTranslation(languageId)

        if (result == null) {
            hideTranslation()
            return
        }

        WindowManager.getInstance().getStatusBar(project).info = result
    }

    private fun getTranslation(id: String): String? {
        if (files.isEmpty()) return null

        val standardFile = findFolder("standard", files)?.findChild("pages")?.findChild("language")?.findChild("texts_cs.php")
        val specialFile = findFolder("special", files)?.findChild("pages")?.findChild("language")?.findChild("texts_cs.php")

        if (standardFile == null && specialFile == null) return null

        if (specialFile != null) {
            val document = FileDocumentManager.getInstance().getDocument(specialFile)
            if (document != null) {
                val lines = document.lineCount - 1
                for (lineNumber in 0..lines) {
                    val lineStart = document.getLineStartOffset(lineNumber)
                    val lineEnd = document.getLineEndOffset(lineNumber)
                    val line = document.getText(TextRange(lineStart, lineEnd)).trim()

                    if (line.contains("\$Lng['${id}']", true)) {
                        val splitted = line.split("=")
                        val translation = splitted.get(1).removeSurrounding("'")
                        return "CS Special: ${translation}"
                    }
                }
            }
        }

        if (standardFile != null) {
            val document = FileDocumentManager.getInstance().getDocument(standardFile)
            if (document != null) {
                val lines = document.lineCount - 1
                for (lineNumber in 0..lines) {
                    val lineStart = document.getLineStartOffset(lineNumber)
                    val lineEnd = document.getLineEndOffset(lineNumber)
                    val line = document.getText(TextRange(lineStart, lineEnd)).trim()

                    if (line.contains("\$Lng['${id}']", true)) {
                        val splitted = line.split("=")
                        var translation = splitted.get(1).removeSuffix(";")
                        translation = translation.removeRange(0..1)
                        val length = translation.length - 1
                        translation = translation.removeRange(length..length)
                        return "CS Standard: ${translation}"
                    }
                }
            }
        }

        return null
    }
}
