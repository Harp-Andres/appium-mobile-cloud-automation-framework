package com.automatizacion.moderna.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner dedicado para el health check del framework.
 * Ejecuta únicamente el feature @framework-health sin necesidad de pasar
 * filtros por línea de comandos (lo que evita problemas con el carácter '@'
 * en PowerShell/Gradle).
 *
 * <p>Uso:
 * <pre>
 *   # Solo health check (unitarios + health feature):
 *   .\gradlew.bat test -DENV=local -Dtest.runner=health
 *
 *   # O con el task de test filtrando la clase:
 *   .\gradlew.bat test -DENV=local --tests "com.automatizacion.moderna.runner.RunFrameworkHealthTest"
 * </pre>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
        value = "com.automatizacion.moderna.steps,com.automatizacion.moderna.hooks")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME,
        value = "@framework-health")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,summary,io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
                "html:target/cucumber-reports/health-cucumber.html," +
                "json:target/cucumber-reports/health-cucumber.json"
)
public class RunFrameworkHealthTest {
}

