package io.danygi.buildlogic.spotbugs;

import com.github.spotbugs.snom.Confidence;
import com.github.spotbugs.snom.Effort;
import com.github.spotbugs.snom.SpotBugsTask;
import org.gradle.api.Project;

public final class SpotBugsConfigurer {

    private SpotBugsConfigurer() {
    }

    public static void configure(Project project) {
        project.getPluginManager().apply("com.github.spotbugs");

        project.getTasks()
                .withType(SpotBugsTask.class)
                .configureEach(task -> {

                    task.setIgnoreFailures(false);
                    task.getEffort().set(Effort.MAX);
                    task.getReportLevel().set(Confidence.LOW);
                });
    }
}