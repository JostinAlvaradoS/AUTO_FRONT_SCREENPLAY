package com.sofka.automation.runners;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@ReservaValida or @ReservaInvalida")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.sofka.automation")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-report/cucumber.html,net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel")
public class EventRunner {
}
