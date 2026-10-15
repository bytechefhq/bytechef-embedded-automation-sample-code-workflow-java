package org.sample.codeworkflow.workflow;

import static com.bytechef.workflow.definition.WorkflowDsl.connection;
import static com.bytechef.workflow.definition.WorkflowDsl.output;
import static com.bytechef.workflow.definition.WorkflowDsl.task;
import static com.bytechef.workflow.definition.WorkflowDsl.trigger;
import static com.bytechef.workflow.definition.WorkflowDsl.workflow;

import com.bytechef.workflow.definition.TaskContext;
import com.bytechef.workflow.definition.WorkflowDsl.ModifiableWorkflowDefinition;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchRecentEmailsWorkflow {

    public static final ModifiableWorkflowDefinition WORKFLOW_DEFINITION = workflow("fetchRecentEmails")
        .label("Fetch Recent Emails")
        .description("Returns the connected user's most recent emails, optionally only those from one sender.")
        .triggers(trigger("request", "request/v1/awaitWorkflowAndRespond"))
        .outputs(
            output("emails")
                .task("fetchEmails"))
        .tasks(
            task("fetchEmails")
                .label("Fetch Emails")
                // A task names the connections it uses. Provisioning binds each one to a connection of the connected
                // user on the same component.
                .connections(connection("googleMail", "gmail"))
                .perform(FetchRecentEmailsWorkflow::fetchEmails));

    private static Object fetchEmails(TaskContext context) throws Exception {
        Map<?, ?> request = asMap(context.input()
            .get("request"));
        Map<?, ?> body = asMap(request.get("body"));

        Map<String, Object> searchInput = new HashMap<>();

        if (body.get("from") != null) {
            searchInput.put("from", body.get("from"));
        }

        Object maxResults = body.get("maxResults");

        searchInput.put("maxResults", maxResults == null ? 10 : maxResults);

        Map<?, ?> result = asMap(context.component("googleMail", "searchEmail", searchInput, "gmail"));

        List<?> messages = result.get("messages") instanceof List<?> messageList ? messageList : List.of();

        context.log(TaskContext.LogLevel.INFO, "Found " + messages.size() + " emails");

        return messages;
    }

    private static Map<?, ?> asMap(Object value) {
        return value instanceof Map<?, ?> map ? map : Map.of();
    }
}
