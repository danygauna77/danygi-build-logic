package io.danygi.buildlogic.checkstyle;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstyleExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class CheckstyleConfigurer {

    private static final String CHECKSTYLE_TASK_NAME = "copyDanygiCheckstyleConfig";
    private static final String CHECKSTYLE_CONFIG_PATH = "danygi/checkstyle.xml";

    private CheckstyleConfigurer() {
    }

    public static void configure(Project project) {
        project.getPluginManager().apply("checkstyle");

        File configFile = getCheckstyleConfigFile(project);

        Task copyCheckstyleConfig = project.getTasks().register(
                CHECKSTYLE_TASK_NAME,
                task -> configureCopyTask(task, configFile)
        ).get();

        CheckstyleExtension extension =
                project.getExtensions().getByType(CheckstyleExtension.class);

        extension.setToolVersion("10.26.1");
        extension.setIgnoreFailures(false);
        extension.setConfigFile(configFile);

        project.getTasks()
                .withType(Checkstyle.class)
                .configureEach(task -> {
                    task.dependsOn(copyCheckstyleConfig);

                    task.getReports().getHtml().getRequired().set(true);
                    task.getReports().getXml().getRequired().set(true);
                });
    }

    private static File getCheckstyleConfigFile(Project project) {
        return new File(
                project.getLayout()
                        .getBuildDirectory()
                        .get()
                        .getAsFile(),
                CHECKSTYLE_CONFIG_PATH
        );
    }

    private static void configureCopyTask(Task task, File configFile) {
        task.getOutputs().file(configFile);

        task.doLast(ignored -> copyCheckstyleConfig(configFile));
    }

    private static void copyCheckstyleConfig(File configFile) {
        try {
            var resource = CheckstyleConfigurer.class
                    .getClassLoader()
                    .getResourceAsStream("checkstyle/checkstyle.xml");

            if (resource == null) {
                throw new IllegalStateException(
                        "checkstyle.xml not found in plugin resources"
                );
            }

            try (resource) {
                Files.createDirectories(configFile.getParentFile().toPath());

                Files.copy(
                        resource,
                        configFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to copy Checkstyle configuration",
                    e
            );
        }
    }
}