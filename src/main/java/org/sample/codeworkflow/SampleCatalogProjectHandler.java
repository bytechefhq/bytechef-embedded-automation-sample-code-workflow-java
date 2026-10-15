package org.sample.codeworkflow;

import static com.bytechef.automation.project.definition.ProjectDsl.project;

import com.bytechef.automation.project.ProjectHandler;
import com.bytechef.automation.project.definition.ProjectDefinition;
import com.google.auto.service.AutoService;
import org.sample.codeworkflow.workflow.FetchRecentEmailsWorkflow;
import org.sample.codeworkflow.workflow.GreetOnRequestWorkflow;
import org.sample.codeworkflow.workflow.SummarizeNewEmailWorkflow;

/**
 * An embedded catalog project is an ordinary automation code project - the same ProjectHandler - imported under
 * Embedded &rarr; Automation Workflows instead of Automation &rarr; Projects.
 */
@AutoService(ProjectHandler.class)
public class SampleCatalogProjectHandler implements ProjectHandler {

    private static final ProjectDefinition PROJECT_DEFINITION = project("Sample Catalog Code Project Java")
        .description("A sample embedded catalog code project.")
        .version("1")
        .workflows(
            GreetOnRequestWorkflow.WORKFLOW_DEFINITION, SummarizeNewEmailWorkflow.WORKFLOW_DEFINITION,
            FetchRecentEmailsWorkflow.WORKFLOW_DEFINITION);

    @Override
    public ProjectDefinition getDefinition() {
        return PROJECT_DEFINITION;
    }
}
