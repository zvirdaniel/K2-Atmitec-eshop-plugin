package cz.k2.eshop.TranslateFunction

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.startup.StartupActivity

/**
 * Created by Daniel Zvir on 12.7.17.
 */
class Startup : StartupActivity {
    override fun runActivity(project: Project) {
        val files = ProjectRootManager.getInstance(project).contentSourceRoots.asList()
        val multicaster = EditorFactory.getInstance().eventMulticaster
        multicaster.addCaretListener(CaretListener(files), project)
    }
}