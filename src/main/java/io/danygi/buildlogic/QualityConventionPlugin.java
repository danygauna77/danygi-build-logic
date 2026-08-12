package io.danygi.buildlogic;

import io.danygi.buildlogic.checkstyle.CheckstyleConfigurer;
import io.danygi.buildlogic.jacoco.JaCoCoConfigurer;
import io.danygi.buildlogic.spotbugs.SpotBugsConfigurer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class QualityConventionPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        CheckstyleConfigurer.configure(project);
        SpotBugsConfigurer.configure(project);
        JaCoCoConfigurer.configure(project);
    }
}