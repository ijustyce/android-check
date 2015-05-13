/*
 * Copyright (c) 2015 Noveo Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Except as contained in this notice, the name(s) of the above copyright holders
 * shall not be used in advertising or otherwise to promote the sale, use or
 * other dealings in this Software without prior written authorization.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.noveogroup.android.check.findbugs

import com.noveogroup.android.check.CheckExtension
import com.noveogroup.android.check.common.CommonCheck
import com.noveogroup.android.check.common.CommonConfig
import edu.umd.cs.findbugs.anttask.FindBugsTask
import org.apache.tools.ant.Location
import org.apache.tools.ant.types.Path
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.plugins.quality.FindBugsPlugin

class FindbugsCheck extends CommonCheck {

    FindbugsCheck() { super('findbugs', 'androidFindbugs', 'Runs Android FindBugs') }

    @Override
    protected CommonConfig getConfig(CheckExtension extension) { return extension.findbugs }

    @Override
    protected void performCheck(Project project, List<File> sources,
                                File configFile, File xmlReportFile) {

//        FindBugsPlugin plugin = new FindBugsPlugin()
//        plugin.apply(project)
//        FindBugs task = project.task([type: FindBugs], 'androidFindbugsXXX') {
//            ignoreFailures = true
//
//            effort = 'max'
//            reportLevel = 'low'
//            excludeFilter = configFile
//
//            reports {
//                xml {
//                    destination = xmlReportFile
//                    xml.withMessages = true
//                }
//            }
//
//            classes = project.files("$project.buildDir/intermediates/classes")
//            classpath = project.files()
//            source = sources
//        } as FindBugs
//        task.execute()
//        throw new GradleException("FINDBUGS")

//        https://github.com/gradle/gradle/blob/master/subprojects/code-quality/src/main/groovy/org/gradle/api/plugins/quality/FindBugs.groovy

//        FindBugs findBugs = new FindBugs()
//        findBugs.ignoreFailures = true
//        findBugs.effort = 'max'
//        findBugs.reportLevel = 'low'
//        findBugs.excludeFilter = configFile
//        findBugs.reports.xml.destination = xmlReportFile
//        findBugs.reports.xml.withMessages = true
//        findBugs.classes = project.files("$project.buildDir/intermediates/classes")
//        findBugs.classpath = files()
//        findBugs.source = sources
//        findBugs.execute()
//        throw new GradleException("FINDBUGS")

        FindBugsTask findBugsTask = new FindBugsTask()

        findBugsTask.project = project.ant.antProject
        findBugsTask.workHard = true
        findBugsTask.excludeFilter = configFile
        findBugsTask.output = "xml:withMessages"
        findBugsTask.outputFile = xmlReportFile
        findBugsTask.failOnError = false

        Path sourcePath = findBugsTask.createSourcePath()
        sources.findAll { it.exists() }.each {
            sourcePath.addFileset(project.ant.fileset(dir: it))
        }

        // todo
        def path = new Path(project.ant.antProject)
        path.location = new Location("/home/pstepanov/.gradle/caches/modules-2/files-2.1/com.google.code.findbugs/findbugs/3.0.1/7ae69957c437fd71628d3904572170cf80c01551/")
        findBugsTask.setClasspath(path)

        println "XXX"
        project.buildscript.configurations.classpath.each {
            println it
        }
        println project.buildscript.configurations.classpath.asPath
        println "XXX"

//        findBugsTask.setHome(new File('/home/pstepanov/.gradle/caches/modules-2/files-2.1/com.google.code.findbugs/findbugs/3.0.1/7ae69957c437fd71628d3904572170cf80c01551/'))
//        findBugsTask.createClass().location = new File("$project.buildDir/intermediates/classes")
        findBugsTask.createClass().location = new File("/home/pstepanov/.gradle/caches/modules-2/files-2.1/com.google.code.findbugs/bcel-findbugs/6.0/7a7563ba41dceff3da4af4927b7c09908bd5132c/bcel-findbugs-6.0.jar")
        // todo

        // edu.umd.cs.findbugs.FindBugs2.main()

        findBugsTask.perform()

        println "FILE (${xmlReportFile.length()}) : $xmlReportFile"
        throw new GradleException("FINDBUGS")
    }

    @Override
    protected int getErrorCount(File xmlReportFile) {
//        GPathResult xml = new XmlSlurper().parseText(xmlReportFile.text)
//        return xml.file.inject(0) { count, file -> count + file.error.size() }
        return 0
    }

    @Override
    protected String getErrorMessage(int errorCount, File htmlReportFile) {
        return "$errorCount FindBugs rule violations were found. See the report at: ${htmlReportFile.toURI()}"
    }

}
/*
class CheckExtension {
    boolean checkstyleAbortOnError = true
    boolean findbugsAbortOnError = true
    boolean pmdAbortOnError = true
}

project.extensions.add("checkConfig", new CheckExtension())
List<File> androidSources = android.sourceSets.inject([]) { dirs, sourceSet -> dirs + sourceSet.java.srcDirs }

apply plugin: 'findbugs'
task findbugs(type: FindBugs) {
    ignoreFailures = true
    effort = 'max'
    reportLevel = 'low'
    excludeFilter = rootProject.file('gradle/config/findbugs/exclude.xml')

    classes = files("$project.buildDir/intermediates/classes")
    classpath = files()

    source androidSources

    reports {
        xml {
            destination "$project.buildDir/outputs/findbugs/findbugs.xml"
            xml.withMessages true
        }
    }
}
check.dependsOn 'findbugs'
tasks.getByName('findbugs') << {
    CheckExtension checkExtension = project.extensions.getByType(CheckExtension)

    File xslTemplate = rootProject.file('gradle/config/findbugs/fancy.xsl')
    File xmlReport = tasks.getByName('findbugs').reports.xml.destination
    File htmlReport = xmlReport.absolutePath.replaceFirst(~/\.[^\.]+$/, '.html') as File
    ant.xslt(in: xmlReport, style: xslTemplate, out: htmlReport)

    def xml = new XmlSlurper().parseText(xmlReport.text)
    int count = xml.BugInstance.size()
    String message = "$count FindBugs rule violations were found. See the report at: ${htmlReport.toURI()}"
    if (count) {
        if (checkExtension.findbugsAbortOnError) {
            throw new GradleException(message)
        } else {
            logger.warn message
        }
    }
}
 */
