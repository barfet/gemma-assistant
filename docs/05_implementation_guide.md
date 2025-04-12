**Implementation Guide: Gemma 3 Mobile AI Assistant**

**Version:** 1.0
**Date:** April 12, 2025
**Based On:** Research v1.0, PRD v1.0, TDD v1.0, Project Plan v1.0

**Table of Contents**

1.  Introduction
2.  Overall Technical Approach Summary
3.  Phase 0: Foundation & Planning Implementation Steps
4.  Phase 1: MVP Development Implementation Steps
5.  Phase 2: Feature Expansion Implementation Steps
6.  Phase 3: Beta Testing & Refinement Implementation Steps
7.  Phase 4: GA & Continuous Improvement Implementation Steps
8.  Cross-Cutting Implementation Concerns
    8.1. Testing Strategy Implementation
    8.2. CI/CD Pipeline Implementation
    8.3. Security Practices Implementation
9.  Key Considerations

---

**1. Introduction**

This document serves as the high-level implementation guide for the Gemma 3 Mobile AI Assistant project. It translates the requirements and designs specified in the PRD and TDD into actionable steps, following the roadmap outlined in the Project Plan. The goal is to provide sufficient context and architectural direction for development teams or AI models tasked with implementing each component and feature.

Assume access to and familiarity with the preceding documents (Research, PRD, TDD, Project Plan).

**2. Overall Technical Approach Summary**

The implementation will follow a **hybrid, on-device first architecture**:

*   **Core Logic:** A LangChain agent, running primarily on the mobile device (iOS/Android), orchestrates interactions.
*   **AI Model:** Google's Gemma 3 (instruction-tuned variants). A smaller, quantized version (e.g., 1B/4B int4) runs on-device using TF Lite (Android) or Core ML (iOS) via converters, potentially leveraging Google AI Edge SDKs or llama.cpp wrappers if beneficial. A larger variant (e.g., 12B/27B) runs in the cloud served via vLLM for complex tasks or fallback.
*   **Personalization:** User settings (name, voice, avatar, preferences) stored securely on-device and injected into the agent's prompt context.
*   **Integrations:** Device features (Calendar, Health) accessed via LangChain Tools calling native platform APIs through bridges/channels.
*   **Improvement:** A Model Customization Pipeline (MCP) using PEFT (LoRA/QLoRA) and potentially DPO/RLHF(PPO) will continuously fine-tune the model based on anonymized user data and feedback.
*   **Observability:** LangSmith will be crucial for tracing agent behavior, debugging, and evaluation. Standard monitoring tools (Prometheus, Grafana, CloudWatch) for infrastructure and application performance.
*   **Technology Stack Highlights:** Native Mobile (Swift/Kotlin), Python (FastAPI for backend), LangChain, Hugging Face (Transformers, PEFT, TRL), TF Lite / Core ML, vLLM, Docker, Kubernetes/Vertex AI, Terraform/Pulumi, Git.

**3. Phase 0: Foundation & Planning Implementation Steps**

*   **Goal:** Set up environments, finalize architecture choices through prototyping, establish core project structures.
*   **Steps:**
    1.  **Environment Setup:**
        *   Provision cloud project/accounts (e.g., GCP).
        *   Set up source control repository (Git).
        *   Configure developer environments (IDE extensions, SDKs for iOS/Android, Python environment).
        *   Establish communication channels (e.g., Slack, Teams).
    2.  **Infrastructure Scaffolding (IaC):**
        *   Use Terraform/Pulumi to define basic network infrastructure (VPC), initial Kubernetes cluster setup (GKE/EKS/AKS) or Vertex AI configurations, basic Cloud Storage buckets for logs/models.
        *   Set up initial CI/CD pipeline structure (e.g., GitHub Actions workflows) for basic build/lint checks.
    3.  **Gemma 3 Model Evaluation & Conversion:**
        *   Download target Gemma 3 variants (e.g., 1B-IT, 4B-IT).
        *   Prototype quantization (int4 using `bitsandbytes` or similar tools).
        *   Prototype conversion to target mobile formats:
            *   **TF Lite:** Use TensorFlow converters (e.g., `TFLiteConverter.from_keras_model` or via ONNX).
            *   **Core ML:** Use `coremltools` Python package to convert TensorFlow/PyTorch/ONNX models.
        *   Build minimal iOS/Android apps to load and run inference with the converted models.
        *   Benchmark latency and memory usage on target device simulators and representative physical devices. Document results to inform final model selection for on-device.
    4.  **Core Project Setup:**
        *   Initialize native iOS (Xcode project) and Android (Android Studio project) application shells.
        *   Initialize backend Python project structure using FastAPI. Set up basic dependency management (e.g., Poetry, Pipenv).
        *   Integrate basic logging frameworks.

**4. Phase 1: MVP Development Implementation Steps**

*   **Goal:** Build the core chat experience with text I/O, basic on-device model, name personalization, and Calendar integration. Deliver Internal Alpha.
*   **Steps by Epic:**

    *   **Epic: Core Chat Functionality**
        1.  **UI Implementation (Mobile):**
            *   iOS (SwiftUI/UIKit): Implement a `View` with a `List` or `ScrollView` for messages, a `TextField` for input, and a `Button` for sending. Use data binding to update the message list.
            *   Android (Compose/XML): Implement a `Composable` screen or `Activity`/`Fragment` with a `LazyColumn` or `RecyclerView` for messages, an `OutlinedTextField` or `EditText` for input, and a `Button`. Use `ViewModel` and `LiveData`/`StateFlow` for UI updates.
        2.  **Message Handling Logic (Mobile):**
            *   On Send button tap: capture text, add user message to UI list, trigger call to `Agent Core` (async). Disable input/button while processing.
            *   Implement callback/listener/flow collector to receive streaming response tokens from `Agent Core`. Append tokens to the latest assistant message in the UI list incrementally. Re-enable input/button upon completion.
        3.  **Conversation History (Mobile):**
            *   Maintain an in-memory list or `ViewModel` state representing the current session's messages. Ensure UI components correctly display this list.

    *   **Epic: On-Device AI Model Integration**
        1.  **Model Loading (Mobile):**
            *   Bundle the selected quantized `.tflite` (Android) or `.mlmodelc` (compiled Core ML, iOS) file within the app assets.
            *   Implement native code (Kotlin/Swift) to load the model file into the respective runtime (`Interpreter` for TF Lite, `MLModel` for Core ML). Handle potential loading errors.
        2.  **Inference Wrapper (Mobile):**
            *   Create a wrapper class (e.g., `OnDeviceLlmService`) with a method like `generateResponse(prompt: String, context: String?, callback: (String) -> Unit)`.
            *   Inside this method:
                *   Preprocess the input prompt/context into the required tensor format (tokenization - requires bundling tokenizer config/vocab, padding/truncation). *Note: Investigate if AI Edge SDK or llama.cpp wrappers simplify this.*
                *   Run inference using the loaded model (`interpreter.run()` or `model.prediction()`). Ensure this runs on a background thread.
                *   Postprocess the output tensor (decode tokens back to text). *Note: Handling token streaming requires careful implementation within the runtime loop.*
                *   Use the `callback` to stream generated tokens back to the caller (`Agent Core`).
        3.  **Connect to Agent Core (Mobile):** Provide an instance of this `OnDeviceLlmService` to the `Agent Core` component.

    *   **Epic: Agent Orchestration (Basic)**
        1.  **LangChain Setup (Conceptual - Mobile Layer or Embedded):**
            *   Determine how LangChain logic runs: Full Python runtime (e.g., Chaquopy/Beeware - potentially heavy), dedicated mobile SDK (if available), or porting core agent loop logic to Swift/Kotlin using wrappers around the LLM service and Tool calls. *Initial assumption: Port core logic or use a minimal embedded runtime.*
            *   Instantiate the LLM wrapper corresponding to `OnDeviceLlmService`.
            *   Initialize `ConversationBufferMemory`.
            *   Create a basic System Prompt string (initially hardcoded, including a placeholder for assistant name).
            *   Set up a simple `AgentExecutor` or `LLMChain` for initial non-tool interactions, using the LLM wrapper and memory. Define the input/output parsing.
        2.  **Agent Core Interface (Mobile):** Define a clear interface (e.g., `AgentCore.processQuery(query: String): Flow<String>`) for the Mobile UI to interact with, abstracting the underlying LangChain complexity.

    *   **Epic: Personalization Engine (Name)**
        1.  **Storage (Mobile):**
            *   iOS: Use `KeychainWrapper` or `UserDefaults` (if non-critical) to store the `assistant_name` string.
            *   Android: Use `EncryptedSharedPreferences` to store the `assistant_name` string.
        2.  **Settings UI (Mobile):** Add a simple screen with a `TextField`/`EditText` allowing the user to input and save the assistant's name, updating the stored value.
        3.  **Prompt Injection (Agent Core):** Modify the System Prompt creation logic to dynamically fetch the stored `assistant_name` and insert it into the prompt template (e.g., "Your name is {assistant_name}. You are helping the user.").

    *   **Epic: Calendar Integration**
        1.  **Native Bridge Implementation (Mobile):**
            *   iOS (Swift): Use `EventKit` framework. Implement functions like `requestCalendarAccess()`, `queryEvents(startDate: Date, endDate: Date): [EventInfo]`, `createEvent(title: String, startDate: Date, endDate: Date?): Bool`. Handle asynchronous callbacks and errors. Expose these via Platform Channels (if Flutter/RN) or directly (if Native).
            *   Android (Kotlin): Use `CalendarContract` Content Provider. Implement functions like `requestCalendarPermission()`, `queryEvents(startTimeMillis: Long, endTimeMillis: Long): List<EventInfo>`, `insertEvent(title: String, startTimeMillis: Long, endTimeMillis: Long): Boolean`. Handle ContentResolver queries and inserts on background threads. Handle permissions using `registerForActivityResult`. Expose via Platform Channels or directly.
            *   Define a common `EventInfo` data structure (e.g., title, start, end, id) returned across platforms.
        2.  **Tool Definition (Agent Core):**
            *   Define LangChain `Tool` classes: `CalendarReader` and `CalendarWriter`.
            *   Provide clear descriptions for the agent (e.g., "Use this tool to read events from the user's calendar for specific dates.").
            *   Define input schemas (`args_schema`) using Pydantic models (e.g., `CalendarReadInput(start_date: str, end_date: str)`).
        3.  **Tool Implementation (Agent Core):**
            *   Implement the `_run` method for each tool. This method will:
                *   Parse input arguments.
                *   Trigger the corresponding Native Bridge function call (passing parameters).
                *   Await the asynchronous result from the native side.
                *   Format the result (e.g., list of events or success/failure message) as a string to be returned to the agent. Handle errors returned from the native bridge.
        4.  **Agent Update (Agent Core):** Add the instantiated `CalendarReader` and `CalendarWriter` tools to the list of tools available to the `AgentExecutor`. Update the system prompt to mention the agent's ability to manage the calendar.

**5. Phase 2: Feature Expansion Implementation Steps**

*   **Goal:** Add voice I/O, voice/avatar personalization, Health integration, improve context, and set up MCP data collection. Deliver Closed Beta.
*   **Steps by Epic:**

    *   **Epic: Voice Interaction (STT/TTS)**
        1.  **STT Integration (Mobile):**
            *   Add Microphone button to UI.
            *   On tap: Request microphone permission if needed. Initialize native STT (`SFSpeechRecognizer` on iOS, `SpeechRecognizer` on Android). Start listening. Provide visual feedback (e.g., animating mic icon).
            *   Handle STT results (partial/final). On final result, pass the transcribed text to the `Agent Core`'s `processQuery` method. Handle STT errors. Stop listening on button tap again or end-of-speech.
        2.  **TTS Integration (Mobile):**
            *   Get the `voice_id` selected by the user (from Phase 2 Personalization).
            *   Initialize native TTS engine (`AVSpeechSynthesizer` on iOS, `TextToSpeech` on Android) with the selected voice ID (mapping our IDs to platform voice identifiers). Handle engine initialization status.
            *   When the `Agent Core` streams response tokens:
                *   Option A (Simpler): Buffer the full response text and synthesize once complete.
                *   Option B (Better UX): Synthesize sentences or chunks as they become available from the streaming response. Manage the TTS queue (`speakUtterance` / `tts.speak`).
            *   Provide UI controls to stop/interrupt TTS playback. Manage audio session/focus.

    *   **Epic: Personalization Engine (Voice/Avatar)**
        1.  **Storage (Mobile):** Add `voice_id` (String) and `avatar_id` (String/Int) to the secure on-device storage.
        2.  **Settings UI (Mobile):**
            *   Voice Selection: Add a list/picker populated with predefined voice options (e.g., "Female UK", "Male US"). Store the identifier of the selected voice. Add preview buttons that synthesize sample text using the selected voice via the TTS engine.
            *   Avatar Selection: Add a grid/list displaying predefined avatar images/icons. Store the identifier of the selected avatar.
        3.  **Integration (Mobile):**
            *   TTS: Pass the stored `voice_id` when initializing the TTS engine (as implemented in Voice Interaction).
            *   UI: In the chat message view, use the stored `avatar_id` to load and display the corresponding image asset next to assistant messages.

    *   **Epic: Health Tracker Integration**
        1.  **Native Bridge Implementation (Mobile):**
            *   iOS (Swift): Use `HealthKit`. Implement functions `requestHealthDataAccess(readTypes: Set<HKObjectType>)`, `queryHealthData(type: HKQuantityTypeIdentifier, startDate: Date, endDate: Date): [HealthSample]`. Handle permissions granularly for steps, sleep, heart rate.
            *   Android (Kotlin): Use `Health Connect SDK`. Implement functions `requestHealthConnectPermissions(readTypes: Set<String>)`, `readHealthRecords(type: RecordType, startTime: Instant, endTime: Instant): List<Record>`. Handle permissions and Health Connect availability checks.
            *   Define common `HealthSample` data structure. Ensure all calls respect privacy and run primarily on-device.
        2.  **Tool Definition (Agent Core):** Define `HealthDataReader` tool with description and schema (e.g., `HealthReadInput(metric: str, start_date: str, end_date: str)`).
        3.  **Tool Implementation (Agent Core):** Implement `_run` method calling the native bridge, passing parameters (metric type, dates), handling results/errors. Emphasize returning only aggregated data/summaries where appropriate, not raw logs unless necessary.
        4.  **Agent Update (Agent Core):** Add `HealthDataReader` tool to the agent. Update system prompt.

    *   **Epic: Agent Orchestration (Advanced)**
        1.  **Context Enhancement (Agent Core):** Implement logic to load key learned user preferences (e.g., "prefers concise answers", favorite topics - placeholder for now) from local storage and add them to the system prompt or context.
        2.  **Memory Management (Agent Core):** If conversations get long, implement `ConversationSummaryBufferMemory` or a sliding window approach to keep context within model limits (esp. relevant for smaller on-device models, less so for Gemma 3's 128K if cloud is used).
        3.  **Prompt Engineering (Agent Core):** Refine system prompts based on initial testing, improving persona consistency and tool usage accuracy. Use few-shot examples in prompts if needed for tool invocation.

    *   **Epic: Cloud AI Model Integration (Basic Setup)**
        1.  **Backend API Service (Python/FastAPI):**
            *   Implement `/v1/chat/cloud` endpoint. Accepts POST with JSON body (query, history, profile snippets).
            *   Add basic authentication (e.g., require a static API key in headers initially).
            *   Implement placeholder logic: either return a canned response or (better) proxy the request to a managed cloud LLM API (like Vertex AI Prediction using a standard Gemma 3 model) for initial functionality. Parse the response and stream it back.
        2.  **Mobile Cloud Connector (Mobile):**
            *   Implement networking client (e.g., using URLSession/Ktor/Retrofit) to call the backend API endpoint.
            *   Add logic in `Agent Core` or a routing layer to decide when to call the cloud (e.g., based on query complexity heuristic, user setting, or on-device failure). Handle network requests securely (HTTPS) and manage errors/timeouts. Integrate the received stream into the response flow.

    *   **Epic: MCP (Data Collection)**
        1.  **Consent Mechanism (Mobile):** Implement a clear UI dialog asking for user consent to collect anonymized interaction data for improving the assistant. Store consent status securely.
        2.  **Logging Endpoint (Backend):** Create a simple backend endpoint (e.g., `/v1/logs`) that accepts POST requests with anonymized log data (e.g., `{'interaction_id': '...', 'anonymized_query': '...', 'anonymized_response': '...', 'tool_used': '...', 'rating': '...', 'timestamp': '...'}`).
        3.  **Secure Logging Client (Mobile):** If consent given, implement logic to:
            *   Anonymize user query and assistant response (strip PII - challenging, start with simple heuristics or placeholders).
            *   Collect interaction metadata (tool calls, timestamps, user feedback ratings if available).
            *   Batch and send these logs securely (HTTPS) to the backend logging endpoint. Handle offline caching and retry logic.
        4.  **Log Storage (Cloud):** Configure the backend logging endpoint to write received logs directly to a designated Cloud Storage bucket (e.g., GCS/S3) in a structured format (e.g., JSONL files partitioned by date). Ensure bucket policies enforce encryption and strict access control.

**6. Phase 3: Beta Testing & Refinement Implementation Steps**

*   **Goal:** Stabilize, optimize, implement the model improvement loop (MCP), and prepare for potential GA. Deliver Release Candidate.
*   **Steps by Epic:**

    *   **Epic: Performance Optimization**
        1.  **Mobile Profiling:** Use Xcode Instruments (iOS) and Android Studio Profiler (Android) to identify bottlenecks in UI rendering, model loading, inference time, memory usage, and battery consumption.
        2.  **On-Device Inference Tuning:** Experiment with runtime delegates (NNAPI/Core ML Neural Engine), potentially different quantization levels (if int4 is slow), or investigate platform-specific optimization libraries (e.g., optimize TF Lite graph). If using llama.cpp, tune threading/batching parameters.
        3.  **Backend Optimization:** Profile FastAPI application (e.g., using `py-spy`, `cProfile`). Optimize database queries (if any), caching strategies (e.g., Redis for repeated external API calls made by potential future tools). Ensure efficient streaming from vLLM.
        4.  **Network Optimization:** Ensure efficient payload sizes for API calls. Use appropriate background task execution for log uploads/model downloads.

    *   **Epic: MCP (Training Loop)**
        1.  **Data Processing Pipeline:** Implement automated jobs (e.g., using Cloud Dataflow/Spark running on Vertex AI Pipelines or Kubeflow) to read raw logs from Cloud Storage, perform robust cleaning/anonymization, filter low-quality interactions, and format data into training files (e.g., instruction-following format for SFT, preference pairs `{'chosen': ..., 'rejected': ...}` for DPO).
        2.  **PEFT Training Script:** Develop a training script using Hugging Face `transformers`, `datasets`, and `peft` libraries.
            *   Load base Gemma 3 model (e.g., 1B-IT).
            *   Apply `LoraConfig` or `QLoraConfig`.
            *   Load processed training data.
            *   Use `Trainer` or custom training loop to fine-tune the model (LoRA adapters) for a few epochs. Handle checkpointing.
            *   If implementing DPO: Use `DPOTrainer` from TRL library with preference pairs data.
        3.  **Evaluation Script:** Develop script to load the fine-tuned adapter, merge with base model (or load separately), and evaluate on a holdout set (perplexity, task-specific metrics using ROUGE/BLEU/custom metrics) and potentially run LLM-as-a-judge evals using LangSmith. Include safety/bias checks.
        4.  **Pipeline Orchestration:** Define the end-to-end pipeline using Vertex AI Pipelines or Kubeflow DSL, chaining the data processing, training, evaluation, and model registration steps. Trigger manually initially, schedule later.
        5.  **Model Deployment Integration:**
            *   Pipeline should register the validated LoRA adapter file (e.g., `adapter_model.bin`) to a Model Registry or versioned path in Cloud Storage.
            *   Implement logic in the mobile app to periodically check a known location (e.g., a manifest file) for new adapter versions. If a new version exists and conditions are met (e.g., on Wi-Fi), download the adapter file securely.
            *   Implement logic to load the downloaded adapter alongside the base model at runtime (requires runtime support or merging offline - investigate best approach for TF Lite/Core ML).

    *   **Epic: Cloud AI Model Integration (Scaling & vLLM)**
        1.  **vLLM Deployment:**
            *   Containerize vLLM with the target large Gemma 3 model (e.g., 12B/27B). Use official vLLM Docker images as base.
            *   Deploy to Kubernetes cluster (GKE/EKS/AKS) using Deployments/StatefulSets with GPU node pools, OR deploy to Vertex AI Endpoints configuring vLLM as the serving container.
            *   Configure vLLM parameters for optimal throughput/latency (e.g., `gpu_memory_utilization`, `max_num_batched_tokens`). Expose via a ClusterIP/Internal LoadBalancer service.
        2.  **API Integration:** Update the backend `/v1/chat/cloud` endpoint to call the internal vLLM service instead of the placeholder/managed API. Handle streaming responses correctly from vLLM.
        3.  **Autoscaling:** Configure Horizontal Pod Autoscaler (HPA) for the vLLM deployment based on GPU utilization or custom metrics, OR configure Vertex AI Endpoint autoscaling parameters. Configure scaling for the FastAPI backend service based on CPU/request count.

    *   **Epic: Observability & Monitoring**
        1.  **LangSmith Integration:** Ensure LangChain agent calls (both on-device if possible, definitely backend) are wrapped with LangSmith tracing context managers or callback handlers. Log prompts, tool calls/results, final outputs. Tag runs with user IDs (anonymized), session IDs, model versions.
        2.  **Metrics & Logging Setup:**
            *   Backend: Integrate Prometheus client library with FastAPI. Expose standard metrics (request count, latency, errors) and custom metrics (e.g., vLLM inference latency). Configure structured logging (JSON).
            *   Mobile: Integrate Firebase Performance Monitoring or similar SDK for app performance metrics. Add targeted logging for key events/errors.
            *   Infrastructure: Set up agents (e.g., Cloud Monitoring agent, Prometheus node-exporter) to collect system metrics from VMs/nodes/pods.
        3.  **Dashboarding & Alerting:**
            *   Set up Grafana or Cloud Monitoring dashboards showing key metrics (API latency/errors, inference latency, resource usage, MCP pipeline status).
            *   Configure alerts (e.g., using Alertmanager, Cloud Monitoring Alerts) for critical conditions (high 5xx error rate, high latency, resource saturation, pipeline failures). Route alerts to the appropriate team channels.

    *   **Epic: (Optional) Messaging Integration**
        *   Follow similar implementation pattern as Calendar/Health integration: Define tool, implement native bridge (using SMS/Contacts APIs - requires careful permission handling!), implement tool logic, update agent. *Note: This has significant privacy implications and platform restrictions.*

**7. Phase 4: GA & Continuous Improvement Implementation Steps**

*   **Goal:** Launch publicly, operate reliably, and iterate based on data.
*   **Steps:**
    1.  **GA Release Process:**
        *   Final QA testing and sign-off.
        *   Prepare App Store / Google Play listing materials.
        *   Submit builds for review.
        *   Execute phased rollout strategy via app store consoles.
    2.  **Production Monitoring:** Actively monitor dashboards and alerts established in Phase 3. Triage and address issues promptly. Analyze LangSmith traces for common failure patterns or poor responses.
    3.  **MCP Operation:** Run the MCP pipeline regularly (e.g., weekly/bi-weekly). Analyze evaluation results before deploying updated models/adapters. Track model performance improvement over time using longitudinal metrics.
    4.  **User Feedback Loop:** Implement mechanisms within the app for users to easily rate responses (thumbs up/down) and optionally provide free-text feedback. Feed this data back into the MCP (e.g., ratings drive preference pair generation for DPO). Analyze feedback themes to guide future development priorities.
    5.  **Iterative Development:** Plan and execute sprints for V1.x features based on user feedback, monitoring data, and strategic priorities.

**8. Cross-Cutting Implementation Concerns**

*   **8.1. Testing Strategy Implementation:**
    *   **Unit Tests:** Implement alongside feature code for mobile UI logic (ViewModel tests), backend utility functions/API logic (PyTest with mocks), tool native bridge helpers, MCP data processing steps. Aim for high coverage of critical logic.
    *   **Integration Tests:** Develop tests for mobile-backend API interaction, agent-tool invocation (using mocked tools/bridges initially), and component interactions within the backend. Run these in CI.
    *   **E2E Tests:** Use Appium/Espresso/XCUITest frameworks to automate key user flows (chat, personalization settings, invoking calendar/health tools). Run periodically, potentially nightly.
    *   **Model QA:** Integrate evaluation scripts into the MCP pipeline. Use LangSmith for manual inspection of agent traces and potentially for running programmatic evaluations (e.g., LLM-as-a-judge). Define benchmark conversation sets.
    *   **Performance Tests:** Integrate performance profiling (on-device) and load testing (backend - using k6/Locust) into pre-release testing cycles.
*   **8.2. CI/CD Pipeline Implementation:**
    *   **CI (GitHub Actions/Jenkins/etc.):** Configure workflows triggered on commits/PRs to: Lint code -> Run unit tests -> Run integration tests -> Build mobile app binaries -> Build backend Docker image -> Push image to registry.
    *   **CD (Argo CD/Spinnaker/Cloud Deploy/etc.):**
        *   Backend: Configure automated deployment of Docker images to Staging environment upon merge to main branch. Implement manual approval gate or automated checks before Blue/Green or Canary deployment to Production.
        *   Mobile: Automate builds push to TestFlight/Firebase App Distribution for internal/beta releases. Manual submission process for App Store/Google Play production releases.
        *   MCP: Deployment of updated models handled within the MCP pipeline itself (registers artifact, potentially triggers cloud deployment update or publishes mobile adapter).
*   **8.3. Security Practices Implementation:**
    *   **Code Reviews:** Enforce mandatory code reviews with a checklist item for security considerations (input validation, output encoding, permission handling, secure storage usage).
    *   **Dependency Scanning:** Integrate tools like Snyk/Dependabot into CI pipeline to automatically scan for vulnerabilities in third-party libraries.
    *   **Secrets Management:** Use secure secrets management solutions (e.g., HashiCorp Vault, Cloud KMS/Secrets Manager) for API keys, database credentials, etc. Do not commit secrets to Git.
    *   **Infrastructure Security:** Configure firewall rules, network policies (in K8s), IAM policies following least privilege principle. Regularly review permissions.
    *   **Regular Audits:** Schedule periodic security audits and penetration tests (internal or external) before major releases and annually.
    *   **Privacy Compliance:** Ensure all data handling aligns with PRD/TDD privacy requirements and consent mechanisms are correctly implemented and enforced.

**9. Key Considerations**

*   **Iterative Approach:** This guide outlines a comprehensive plan, but implementation should remain iterative. Prioritize core functionality and stability, gathering feedback early and often.
*   **Technology Evolution:** The LLM and mobile AI landscape evolves rapidly. Stay updated on new versions of Gemma, LangChain, mobile runtimes, and optimization techniques. Be prepared to adapt the implementation based on new developments.
*   **Collaboration:** Effective communication and collaboration between Mobile, ML, Backend, QA, and Product teams are crucial for success, especially given the tight integration between components.
*   **User Experience:** Continuously evaluate the user experience, particularly latency, responsiveness, and the perceived intelligence/helpfulness of the assistant. Technical implementation must always serve the end-user goal.