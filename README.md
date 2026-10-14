# bytechef-embedded-automation-sample-code-workflow-java

A sample ByteChef **embedded catalog code project** written in Java. It is a plain jar: a class implementing
`com.bytechef.automation.project.ProjectHandler`, registered through `META-INF/services` by `@AutoService`, that
describes the project and its workflows with `ProjectDsl` and `WorkflowDsl`. The server finds the handler with
`ServiceLoader` and runs each task's `perform` itself.

A catalog project is an ordinary automation code project - the same format the automation `ProjectHandler` path
parses - imported under Embedded → **Automations** (the Automation Workflows page) instead of Automation → Projects.
Importing publishes it to the embedded catalog straight away. A catalog workflow is only invocable through the embedded
public endpoints when it declares a **request** trigger or an **app-event** (`appEvent/newEvent`) trigger; a workflow
without one still imports, but the import returns a warning for it (the UI keeps the dialog open and shows the
warning). This sample declares `request/v1/autoRespondWithHTTP200`.

The project, "Sample Catalog Code Project Java", has one workflow, `greetOnRequest`, started by the request trigger. It
runs one task, `greet`, that returns a greeting, and exposes that as the `greeting` output.

## Build

Requires JDK 25 (Gradle's toolchain selects it).

```bash
./gradlew build
```

The jar to deploy is `build/libs/sample-catalog-code-project-java-1.0.0.jar`.

`libs/` holds the ByteChef SDK jars the code compiles against: `project-api-1.0.jar` and `workflow-api-1.0.jar`. They
are `compileOnly` - the server provides them at runtime, so they must not be packed into your jar. To refresh them, run
`./gradlew :sdks:backend:java:workflow-api:jar :sdks:backend:java:automation:project-api:jar` in the ByteChef repository
and copy the jars from each module's `build/libs/`.

## Deploy

Uploading a jar requires `bytechef.workflow.code-workflow.java-enabled: true` on the server (the default). When it is
off, the import dialog only offers `.js` and `.py` files and the API rejects jars.

Deploying a project with the same name again publishes a new version of that catalog project instead of creating a
second one.

### From the UI

Embedded → **Automations** → the arrow next to **New Project** → **Import Code Project**, then pick the jar.

### Through the public API

`POST /api/embedded/v1/automation-project-code-workflows/deploy` with a `multipart/form-data` body holding one file
part, `projectFile`: the jar. The file name must end in `.jar`; the server picks the language from the extension.
A successful deploy returns `200` with the import warnings, for example `{"warnings": []}`.
`GET /api/embedded/v1/automation-project-code-workflows` lists the published catalog projects.

Authenticate with `Authorization: Bearer <API key>`, using an **Embedded API key** (Embedded → Settings →
**API Keys**) that belongs to an administrator - deploying requires the admin role. This endpoint takes only a typed
API key: an organization admin key (Settings → Organization → **Admin API Keys**) is rejected with `401`. The key is
looked up in the environment named by the `X-ENVIRONMENT` header (`DEVELOPMENT`, `STAGING` or `PRODUCTION`;
`PRODUCTION` when the header is absent), so send the environment the key was created in. The request runs as the
key's own ByteChef user, not as a connected user.

```bash
curl -X POST "$BYTECHEF_URL/api/embedded/v1/automation-project-code-workflows/deploy" \
  -H "Authorization: Bearer $BYTECHEF_EMBEDDED_API_KEY" \
  -H "X-ENVIRONMENT: DEVELOPMENT" \
  -F "projectFile=@build/libs/sample-catalog-code-project-java-1.0.0.jar"
```

`BYTECHEF_URL` is the server's base URL, for example `http://localhost:9555` for a local development server.
