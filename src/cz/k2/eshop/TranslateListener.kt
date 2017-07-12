package cz.k2.eshop

import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Package: cz.k2.eshop
 * Created by Daniel Zvir on 12.7.17.
 */
class TranslateListener(val files: List<VirtualFile>) : CaretListener {
    override fun caretAdded(e: CaretEvent?) {}

    override fun caretRemoved(e: CaretEvent?) {}

    override fun caretPositionChanged(e: CaretEvent) {
        val position = e.newPosition
        val editor = e.editor
        val document = editor.document
        val offset = editor.logicalPositionToOffset(position)
        val project = editor.project
        if (project != null) {
            val provider = TranslateProvider(project, files)
            val file = PsiDocumentManager.getInstance(project).getPsiFile(document)
            val element = file?.let { PsiTreeUtil.findElementOfClassAtOffset(it, offset, StringLiteralExpression::class.java, false) }
            if (element?.language?.displayName?.toLowerCase() == "php") {
                val functionCall = element.parent.parent
                if (functionCall.node.firstChildNode.text.toLowerCase() == "translate") {
                    provider.displayTranslation(element.contents)
                } else {
                    provider.hideTranslation()
                }
            } else {
                provider.hideTranslation()
            }
        }
    }
}