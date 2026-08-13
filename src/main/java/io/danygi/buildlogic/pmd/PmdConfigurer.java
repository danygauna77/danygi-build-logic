package io.danygi.buildlogic.pmd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.quality.Pmd;
import org.gradle.api.plugins.quality.PmdExtension;

public final class PmdConfigurer {

    private static final String PMD_VERSION = "7.16.0";
    private static final String PMD_RULESET_PATH = "danygi/pmd/ruleset.xml";
    private static final String COPY_TASK_NAME = "copyDanygiPmdRuleset";

    private PmdConfigurer() {
    }

    public static void configure(Project project) {
        project.getPluginManager().apply("pmd");

        File rulesetFile = getRulesetFile(project);

        Task copyRulesetTask = project.getTasks()
                .register(
                        COPY_TASK_NAME,
                        task -> configureCopyTask(task, rulesetFile)
                )
                .get();

        PmdExtension extension =
                project.getExtensions().getByType(PmdExtension.class);

        extension.setToolVersion(PMD_VERSION);
        extension.setConsoleOutput(true);
        extension.setIgnoreFailures(false);
        extension.setRuleSetFiles(
                project.files(rulesetFile)
        );

        project.getTasks()
                .withType(Pmd.class)
                .configureEach(task -> {
                    task.dependsOn(copyRulesetTask);

                    task.getReports().getHtml().getRequired().set(true);
                    task.getReports().getXml().getRequired().set(true);
                });
    }

    private static File getRulesetFile(Project project) {
        return new File(
                project.getLayout()
                        .getBuildDirectory()
                        .get()
                        .getAsFile(),
                PMD_RULESET_PATH
        );
    }

    private static void configureCopyTask(
            Task task,
            File rulesetFile
    ) {
        task.getOutputs().file(rulesetFile);

        task.doLast(ignored -> copyRuleset(rulesetFile));
    }

    private static void copyRuleset(File rulesetFile) {
        try {
            var resource = PmdConfigurer.class
                    .getClassLoader()
                    .getResourceAsStream("pmd/ruleset.xml");

            if (resource == null) {
                throw new IllegalStateException(
                        "ruleset.xml not found in plugin resources"
                );
            }

            try (resource) {
                Files.createDirectories(
                        rulesetFile.getParentFile().toPath()
                );

                Files.copy(
                        resource,
                        rulesetFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to copy PMD ruleset",
                    e
            );
        }
    }
}