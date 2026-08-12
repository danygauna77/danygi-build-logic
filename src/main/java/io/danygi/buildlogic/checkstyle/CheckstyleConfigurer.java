package io.danygi.buildlogic.checkstyle;

import org.gradle.api.Project;
import org.gradle.api.plugins.quality.Checkstyle;
import org.gradle.api.plugins.quality.CheckstyleExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class CheckstyleConfigurer {

    private CheckstyleConfigurer() {
    }

    public static void configure(Project project) {
        project.getPluginManager().apply("checkstyle");

        CheckstyleExtension extension =
                project.getExtensions().getByType(CheckstyleExtension.class);

        extension.setToolVersion("10.26.1");
        extension.setIgnoreFailures(false);
        extension.setConfigFile(getCheckstyleConfig(project));

        project.getTasks()
                .withType(Checkstyle.class)
                .configureEach(task -> {
                    task.getReports().getHtml().getRequired().set(true);
                    task.getReports().getXml().getRequired().set(true);
                });
    }

    private static File getCheckstyleConfig(Project project) {
        try {
            var resource = CheckstyleConfigurer.class
                    .getClassLoader()
                    .getResourceAsStream("checkstyle/checkstyle.xml");

            if (resource == null) {
                throw new IllegalStateException(
                        "checkstyle.xml not found in plugin resources"
                );
            }

            File configFile = new File(
                    project.getLayout()
                            .getBuildDirectory()
                            .get()
                            .getAsFile(),
                    "danygi/checkstyle.xml"
            );

            Files.createDirectories(configFile.getParentFile().toPath());

            Files.copy(
                    resource,
                    configFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return configFile;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load Checkstyle configuration",
                    e
            );
        }
    }
}