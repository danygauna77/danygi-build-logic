package io.danygi.buildlogic.pmd;

import org.gradle.api.Project;
import org.gradle.api.plugins.quality.Pmd;
import org.gradle.api.plugins.quality.PmdExtension;

public final class PmdConfigurer {

    private static final String PMD_VERSION = "7.16.0";

    private PmdConfigurer() {
    }

    public static void configure(Project project) {
        project.getPluginManager().apply("pmd");

        PmdExtension extension =
                project.getExtensions().getByType(PmdExtension.class);

        extension.setToolVersion(PMD_VERSION);
        extension.setConsoleOutput(true);
        extension.setIgnoreFailures(false);

        extension.setRuleSets(
                java.util.List.of(
                        "category/java/bestpractices.xml",
                        "category/java/codestyle.xml",
                        "category/java/design.xml",
                        "category/java/errorprone.xml",
                        "category/java/performance.xml"
                )
        );

        project.getTasks().withType(Pmd.class).configureEach(task -> {
            task.getReports().getHtml().getRequired().set(true);
            task.getReports().getXml().getRequired().set(true);
        });
    }
}