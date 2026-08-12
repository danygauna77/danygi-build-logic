package io.danygi.buildlogic.jacoco;

import java.math.BigDecimal;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public final class JaCoCoConfigurer {

    private static final String JACOCO_VERSION = "0.8.13";
    private static final double MINIMUM_COVERAGE = 0.85;

    private JaCoCoConfigurer() {
    }

    public static void configure(Project project) {
        project.getPluginManager().apply("jacoco");

        configureJaCoCoVersion(project);
        configureReports(project);
        configureCoverageVerification(project);
    }

    private static void configureJaCoCoVersion(Project project) {
        JacocoPluginExtension extension =
                project.getExtensions().getByType(JacocoPluginExtension.class);

        extension.setToolVersion(JACOCO_VERSION);
    }

    private static void configureReports(Project project) {
        TaskProvider<JacocoReport> jacocoTestReport =
                project.getTasks().named("jacocoTestReport", JacocoReport.class);

        jacocoTestReport.configure(task -> {
            task.getReports().getHtml().getRequired().set(true);
            task.getReports().getXml().getRequired().set(true);
            task.getReports().getCsv().getRequired().set(false);
        });

        project.getTasks().named("test").configure(task ->
                task.finalizedBy(jacocoTestReport)
        );
    }

    private static void configureCoverageVerification(Project project) {
        TaskProvider<JacocoCoverageVerification> verification =
                project.getTasks().named(
                        "jacocoTestCoverageVerification",
                        JacocoCoverageVerification.class
                );

        verification.configure(task ->
                task.violationRules(rules ->
                        rules.rule(rule -> {
                            rule.setElement("BUNDLE");

                            rule.limit(limit -> {
                                limit.setCounter("LINE");
                                limit.setValue("COVEREDRATIO");
                                limit.setMinimum(
                                        BigDecimal.valueOf(MINIMUM_COVERAGE)
                                );
                            });
                        })
                )
        );

        project.getTasks().named("check").configure(task ->
                task.dependsOn(verification)
        );
    }
}