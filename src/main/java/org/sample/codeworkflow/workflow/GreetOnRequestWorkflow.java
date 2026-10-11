package org.sample.codeworkflow.workflow;

import static com.bytechef.workflow.definition.WorkflowDsl.output;
import static com.bytechef.workflow.definition.WorkflowDsl.task;
import static com.bytechef.workflow.definition.WorkflowDsl.trigger;
import static com.bytechef.workflow.definition.WorkflowDsl.workflow;

import com.bytechef.workflow.definition.TaskContext;
import com.bytechef.workflow.definition.WorkflowDsl.ModifiableWorkflowDefinition;

/**
 * A catalog workflow is only reachable through the embedded public endpoints when it declares a request trigger or an
 * app-event trigger. Without one the import still succeeds, but returns a warning for the workflow.
 */
public class GreetOnRequestWorkflow {

    public static final ModifiableWorkflowDefinition WORKFLOW_DEFINITION = workflow("greetOnRequest")
        .label("Greet on Request")
        .description("Answers a request with a greeting.")
        .triggers(trigger("request", "request/v1/autoRespondWithHTTP200"))
        .outputs(
            output("greeting")
                .task("greet"))
        .tasks(
            task("greet")
                .label("Greet")
                .perform(GreetOnRequestWorkflow::greet));

    private static Object greet(TaskContext context) {
        context.log(TaskContext.LogLevel.INFO, "Greeting from the embedded catalog");

        return "Hello from the embedded catalog";
    }
}
