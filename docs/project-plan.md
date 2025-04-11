**Project Plan & User Stories: Gemma 3 Mobile AI Assistant**

**Version:** 1.0
**Date:** April 12, 2025
**Author:** Roman Borysenok
**Status:** Draft

**Table of Contents**

1.  Introduction & Scope
2.  Project Plan / Roadmap
    2.1. Project Phases
    2.2. Milestones & Deliverables
    2.3. Dependencies & Constraints
3.  Epics & Tasks
4.  User Stories / Use Cases
    4.1. Epic: Core Chat Functionality
    4.2. Epic: Personalization Engine
    4.3. Epic: Calendar Integration
    4.4. Epic: Health Tracker Integration
    4.5. Epic: Voice Interaction
5.  Test Cases & Validation
6.  Timeline & Resources
    6.1. Roadmap Visualization (High-Level)
    6.2. Resource Allocation by Epic (Illustrative)
7.  Risk Assessment & Mitigation (Project Risks)
8.  Approval & Review Process
9.  Future Enhancements & Next Steps

---

**1. Introduction & Scope**

**1.1. Overview**
This document outlines the project plan, roadmap, and detailed user requirements (in the form of epics, user stories, and use cases) for the development of the Gemma 3 Mobile AI Assistant. The goal is to create a personalized, context-aware, and responsive AI assistant for iOS and Android, leveraging Google's Gemma 3 model for on-device and cloud capabilities. The project aims for an iterative release strategy, starting with core functionalities and progressively adding features and refinements.

**1.2. Scope**
*   **Project Plan / Roadmap:** Covers the major phases, milestones, deliverables, dependencies, and high-level timeline for the development lifecycle of Version 1.0 of the AI Assistant.
*   **User Stories / Use Cases:** Details the key functional requirements derived from the PRD, broken down into epics and user stories with specific acceptance criteria. It focuses on the core features planned for V1, including chat, personalization (name, voice, avatar), and integration with device Calendar and Health data. Test cases are provided to guide validation efforts.

**Out of Scope for this Document:**
*   Detailed financial budget breakdown.
*   Minute-by-minute task scheduling (Sprint planning handled separately).
*   Low-level technical design choices (Refer to TDD).
*   Marketing, sales, or support plans.

**2. Project Plan / Roadmap**

**2.1. Project Phases**
*(Aligned with PRD Sec 13 & TDD Sec 7)*

*   **Phase 0: Foundation & Planning (Duration: ~2 Months)**
    *   **Goals:** Finalize architecture, set up development/ML environments, initial Gemma 3 model evaluation (performance on target devices), core team onboarding, detailed backlog grooming for Phase 1.
    *   **Activities:** Tech stack selection confirmation, environment provisioning (cloud, CI/CD), base model benchmarking (latency, memory), TDD refinement, initial user story definition.
*   **Phase 1: MVP Development (Duration: ~2 Months)**
    *   **Goals:** Develop core chatbot functionality (text I/O), integrate basic on-device Gemma 3 model, implement basic personalization (name), integrate Calendar read/write tools. Deliver an internal Alpha build.
    *   **Activities:** Mobile UI skeleton, core chat logic, on-device model integration (TF Lite/Core ML), LangChain agent setup, Calendar tool implementation (native bridges + LangChain wrapper), basic name personalization storage/retrieval.
*   **Phase 2: Feature Expansion & Beta Prep (Duration: ~3 Months)**
    *   **Goals:** Implement voice interaction (STT/TTS), add voice & avatar personalization, integrate Health Tracker (read-only), improve context handling, set up initial MCP (data collection pipeline). Deliver a Closed Beta build.
    *   **Activities:** STT/TTS integration, voice/avatar selection UI & logic, HealthKit/Health Connect integration & tool, enhance LangChain agent memory/prompting, deploy logging infrastructure for MCP, refine UI/UX based on early feedback.
*   **Phase 3: Beta Testing & Refinement (Duration: ~2 Months)**
    *   **Goals:** Gather user feedback from Closed/Open Beta, iterate on features, optimize performance (latency, battery), implement model improvements (initial PEFT fine-tuning via MCP), potentially add optional integrations (e.g., basic Messaging summary - scope TBD). Deliver a stable build ready for GA consideration.
    *   **Activities:** Beta user feedback collection & analysis, performance profiling & optimization, MCP pipeline execution & model updates, bug fixing, potentially add optional feature (e.g., Messaging tool), usability testing, documentation refinement.
*   **Phase 4: General Availability (GA) & Continuous Improvement (Ongoing)**
    *   **Goals:** Launch V1.0 publicly, monitor performance and user feedback, continuously improve the model via the MCP, plan and develop V1.x features.
    *   **Activities:** App Store/Google Play submission, production monitoring setup, ongoing MCP runs, backlog grooming for next iterations, A/B testing features/models.

**2.2. Milestones & Deliverables**

| Phase   | Key Milestones                                    | Key Deliverables                           |
| :------ | :------------------------------------------------ | :----------------------------------------- |
| Phase 0 | Environment Setup Complete, Arch Finalized        | TDD v1.0, Initial Model Benchmarks, Env Access |
| Phase 1 | Core Chat Functional, On-Device Model Integrated  | Internal Alpha Build (iOS/Android)        |
|         | Calendar Tool Ready, Name Personalization Done    | Phase 1 Test Report                       |
| Phase 2 | Voice I/O Integrated, Voice/Avatar Personalization | Closed Beta Build (iOS/Android)           |
|         | Health Tool Ready, MCP Data Collection Active     | Initial MCP Pipeline Deployed             |
| Phase 3 | Beta Feedback Analyzed, Performance Optimized   | Release Candidate Build (iOS/Android)     |
|         | First Model Update via MCP Deployed (Internal)  | Beta Test Report, Performance Report      |
| Phase 4 | Public Launch Approved                            | GA Release on App Stores                  |
|         | Production Monitoring Established               | Monitoring Dashboards, Ongoing MCP Reports |

**2.3. Dependencies & Constraints**
*   **Dependencies:**
    *   Availability and stability of Google Gemma 3 models and associated tooling (e.g., quantization tools, official runtimes/APIs like AI Edge).
    *   Stability and documentation of LangChain, PEFT, TRL, vLLM libraries.
    *   Access to device APIs (Calendar, HealthKit, Health Connect, STT, TTS) and potential permission changes in OS updates.
    *   Cloud platform service availability (Compute, Storage, Pipelines, Monitoring).
    *   App Store / Google Play review timelines.
*   **Constraints:**
    *   Mobile device hardware limitations (CPU, RAM, NPU capabilities, battery).
    *   Strict adherence to data privacy regulations (GDPR, CCPA, potentially HIPAA).
    *   Budget allocated for cloud resources (GPU instances for training/serving).
    *   Team size and skill availability across ML, Mobile, Backend, QA.
    *   Timeline expectations from stakeholders.

**3. Epics & Tasks**

*(Mapping high-level tasks to epics and phases - Not exhaustive)*

| Epic                                       | High-Level Tasks                                                                                                | Target Phase(s) | Priority (V1) | Owner Team(s)        |
| :----------------------------------------- | :-------------------------------------------------------------------------------------------------------------- | :-------------- | :------------ | :------------------- |
| **Core Chat Functionality**                | Design chat UI, Implement text input/output, Integrate conversation history, Basic context management           | Phase 1         | High          | Mobile, UX           |
| **On-Device AI Model Integration**         | Select & quantize Gemma 3 variant, Integrate TF Lite/Core ML runtime, Implement model invocation logic, Benchmark | Phase 1         | High          | ML, Mobile           |
| **Agent Orchestration & Tool Framework**   | Setup LangChain agent, Define system prompts, Implement tool invocation logic, Manage agent memory            | Phase 1, 2      | High          | ML, Backend/Mobile |
| **Personalization Engine**                 | Design UI for settings, Implement local storage (secure), Integrate name/voice/avatar selection, Profile learning | Phase 1, 2      | High          | Mobile, UX, ML       |
| **Calendar Integration**                   | Design tool API, Implement native bridge (iOS/Android), Implement LangChain tool wrapper, Handle permissions      | Phase 1         | High          | Mobile, ML           |
| **Health Tracker Integration**             | Design tool API, Implement native bridge (iOS/Android), Implement LangChain tool wrapper, Handle permissions      | Phase 2         | High          | Mobile, ML           |
| **Voice Interaction (STT/TTS)**            | Integrate native STT, Integrate native/custom TTS, Manage audio focus, Handle real-time streaming             | Phase 2         | High          | Mobile               |
| **Cloud AI Model Integration (Hybrid)**    | Setup cloud inference (vLLM), Develop backend API proxy, Implement client-side cloud connector logic        | Phase 2, 3      | Medium        | Backend, ML, Mobile  |
| **Model Customization Pipeline (MCP)**     | Setup data logging/storage, Build cleaning pipeline, Implement PEFT training script, Automate pipeline (Vertex/KF) | Phase 2, 3      | High          | ML, Backend          |
| **Infrastructure & Deployment**            | Provision cloud resources (IaC), Setup CI/CD pipelines (Mobile/Backend), Configure K8s/Vertex AI            | Phase 0, 1, 2   | High          | Backend/DevOps     |
| **Observability & Monitoring**             | Integrate LangSmith, Setup logging/metrics (Mobile/Backend), Create dashboards, Configure alerts            | Phase 2, 3      | High          | Backend/DevOps, ML |
| **(Optional) Messaging Integration**       | Design tool API, Implement native bridge, Implement LangChain tool, Handle permissions                        | Phase 3         | Low           | Mobile, ML           |

**4. User Stories / Use Cases**

**4.1. Epic: Core Chat Functionality**

*   **US001: Send Text Query**
    *   **As a** user, **I want** to type a question or command into a text input field, **so that** I can communicate my request to the AI assistant.
    *   **Acceptance Criteria:**
        *   A text input field is visible and accessible on the main chat screen.
        *   User can type text into the field.
        *   A "Send" button or action is available.
        *   Pressing "Send" submits the text to the assistant's processing logic.
        *   The sent message appears in the conversation history UI.
*   **US002: Receive Text Response**
    *   **As a** user, **I want** to see the assistant's text response displayed clearly in the chat interface, **so that** I can understand the answer to my query.
    *   **Acceptance Criteria:**
        *   Assistant responses appear in the conversation history UI, visually distinct from user messages.
        *   Responses are displayed promptly after generation begins (streaming).
        *   Long responses are scrollable or handled appropriately within the UI.
        *   Response text is legible and correctly formatted.
*   **US003: View Conversation History**
    *   **As a** user, **I want** to see the history of my current conversation session, **so that** I can recall the context of the interaction.
    *   **Acceptance Criteria:**
        *   The chat UI displays previous user messages and assistant responses in chronological order.
        *   The history is scrollable.
        *   History persists for the duration of the current session (definition of session TBD, potentially until app is fully closed).

**4.2. Epic: Personalization Engine**

*   **US101: Set Assistant Name**
    *   **As a** user, **I want** to assign a custom name to my AI assistant in the settings, **so that** the interaction feels more personalized and I can address it by name.
    *   **Acceptance Criteria:**
        *   A setting option allows entering a custom name for the assistant.
        *   The entered name is saved securely on the device.
        *   The assistant uses the chosen name in greetings or self-references (e.g., "Hi [User Name], I'm [Assistant Name]. How can I help?").
        *   The chosen name persists across app sessions.
*   **US102: Choose Assistant Voice**
    *   **As a** user, **I want** to select the assistant's voice from a list of predefined options (e.g., different genders, accents), **so that** I can choose a voice I find pleasant or suitable.
    *   **Acceptance Criteria:**
        *   A setting option presents a list of available voices with previews (if possible).
        *   User selection is saved securely on the device.
        *   The selected voice is used for all subsequent TTS output from the assistant.
        *   The chosen voice persists across app sessions.
*   **US103: Choose Assistant Avatar**
    *   **As a** user, **I want** to select a visual avatar for the assistant from a predefined set, **so that** the assistant has a visual representation in the chat interface.
    *   **Acceptance Criteria:**
        *   A setting option displays available avatars for selection.
        *   User selection is saved securely on the device.
        *   The chosen avatar is displayed alongside assistant messages in the chat UI.
        *   The chosen avatar persists across app sessions.
*   **US104: Assistant Learns User Name** (Assuming user provides it or it's retrieved from device profile with permission)
    *   **As a** user interacting with the assistant, **I want** the assistant to address me by my name (if known), **so that** the conversation feels more personal and direct.
    *   **Acceptance Criteria:**
        *   If the user's name is known (e.g., from settings or profile), the assistant uses it appropriately in greetings or responses (e.g., "Okay, [User Name], I've added that.").
        *   The mechanism for obtaining the user's name respects privacy and includes necessary permissions.

**4.3. Epic: Calendar Integration**

*   **US201: Query Calendar Events**
    *   **As a** user, **I want** to ask the assistant about my schedule (e.g., "What's on my calendar today?", "Do I have any meetings tomorrow morning?"), **so that** I can quickly get my agenda without opening the calendar app.
    *   **Acceptance Criteria:**
        *   Assistant correctly parses the date/time range from the user query.
        *   Assistant requests calendar permission if not already granted.
        *   Assistant successfully invokes the `CalendarReader` tool.
        *   The tool retrieves correct event information (time, title, participants if available) from the device calendar for the specified range.
        *   Assistant presents the retrieved events clearly in the response.
        *   If no events are found, the assistant states this clearly.
        *   Response is generated within acceptable latency (e.g., < 3 seconds for on-device query).
*   **US202: Schedule Calendar Event**
    *   **As a** user, **I want** to ask the assistant to schedule a meeting or event (e.g., "Schedule a meeting with John Doe tomorrow at 3 PM about Project X"), **so that** I can quickly add events to my calendar via voice/text.
    *   **Acceptance Criteria:**
        *   Assistant parses key event details (title, date, time, attendees if possible) from the user request.
        *   Assistant requests calendar write permission if not already granted.
        *   Assistant confirms the details with the user before proceeding (e.g., "Okay, I'll schedule 'Meeting with John Doe' tomorrow at 3 PM. Is that correct?").
        *   Upon confirmation, assistant successfully invokes the `CalendarWriter` tool with the correct parameters.
        *   The tool successfully creates the event in the device calendar.
        *   Assistant confirms the successful creation of the event to the user.
        *   Handles cases where details are missing (prompts user for more info).
        *   Handles potential conflicts or errors gracefully.
*   **Detailed Use Case: Schedule Meeting**
    *   **Trigger:** User says/types "Schedule a team sync for Friday at 10 AM".
    *   **Preconditions:** App has Calendar write permission. Assistant name is "Alex".
    *   **Main Flow:**
        1.  STT transcribes the request.
        2.  LangChain Agent receives text: "Schedule a team sync for Friday at 10 AM".
        3.  Agent identifies intent to schedule and extracts parameters: `title="team sync"`, `date="upcoming Friday"`, `time="10:00 AM"`.
        4.  Agent formulates confirmation prompt: "Okay, I can schedule 'team sync' for this Friday at 10 AM. Should I go ahead?"
        5.  Assistant speaks/displays the confirmation.
        6.  User confirms ("Yes").
        7.  Agent invokes `CalendarWriter` tool with extracted parameters.
        8.  Tool calls native Calendar API (e.g., `insertEvent`).
        9.  Native API returns success.
        10. Tool returns success message to Agent.
        11. Agent formulates final response: "Alright, I've added 'team sync' to your calendar for Friday at 10 AM."
        12. Assistant speaks/displays the final confirmation.
    *   **Alternative Flow (Missing Info):** If user says "Schedule a meeting Friday", Agent asks "What time should I schedule it for, and what should I call it?".
    *   **Alternative Flow (No Permission):** If permission is missing, Agent responds "I need permission to access your calendar first. Can I ask for permission now?".

**4.4. Epic: Health Tracker Integration**

*   **US301: Query Health Data**
    *   **As a** user, **I want** to ask the assistant about my recent health data (e.g., "How many steps did I take yesterday?", "What was my sleep duration last night?"), **so that** I can easily access key health metrics.
    *   **Acceptance Criteria:**
        *   Assistant correctly identifies the type of health data requested (steps, sleep, heart rate).
        *   Assistant requests relevant health data permissions (e.g., HealthKit read access for Steps) if not already granted.
        *   Assistant successfully invokes the `HealthDataReader` tool.
        *   The tool retrieves the correct data for the specified metric and time period from HealthKit/Health Connect.
        *   Assistant presents the retrieved data clearly in the response (e.g., "You took 8,530 steps yesterday.").
        *   If data is unavailable or permission denied, the assistant states this clearly.
        *   Interaction happens primarily on-device to protect health data privacy.

**4.5. Epic: Voice Interaction**

*   **US401: Initiate Voice Query**
    *   **As a** user, **I want** to tap a microphone button to start speaking my query, **so that** I can interact with the assistant hands-free.
    *   **Acceptance Criteria:**
        *   A microphone icon/button is available in the UI.
        *   Tapping the button activates the device's STT service.
        *   Clear visual/audio indication that the app is listening.
        *   User's speech is captured.
        *   Tapping the button again (or automatic end-of-speech detection) stops recording and initiates processing.
*   **US402: Hear Spoken Response**
    *   **As a** user, **I want** to hear the assistant's response spoken aloud using the selected voice, **so that** I can receive information without looking at the screen.
    *   **Acceptance Criteria:**
        *   Assistant text responses are automatically sent to the TTS engine.
        *   The TTS engine uses the voice selected in the personalization settings.
        *   Speech output starts promptly (ideally streaming alongside text generation).
        *   Audio playback is clear and understandable.
        *   User can interrupt or stop the speech playback.

**5. Test Cases & Validation**

*(Examples based on selected User Stories)*

| User Story | Test Case ID | Description                                                | Steps                                                                                                   | Test Data / Preconditions                | Expected Result                                                                                                |
| :--------- | :----------- | :--------------------------------------------------------- | :------------------------------------------------------------------------------------------------------ | :--------------------------------------- | :------------------------------------------------------------------------------------------------------------- |
| US101      | TC101.1      | Verify saving a new assistant name                       | 1. Go to Settings. 2. Enter "Sparky" in assistant name field. 3. Save. 4. Close & reopen settings.       | Assistant name field initially empty.    | "Sparky" is displayed in the settings field after reopening.                                                 |
| US101      | TC101.2      | Verify assistant uses the custom name in greeting          | 1. Set assistant name to "Sparky". 2. Start a new chat session. 3. Observe initial greeting.            | User name "Test User" known.             | Assistant response starts with something like "Hi Test User, I'm Sparky..."                                |
| US201      | TC201.1      | Query calendar for a day with known events                 | 1. Ensure calendar has >0 events for tomorrow. 2. Ask "What's on my calendar tomorrow?". 3. Observe response. | Calendar permission granted.           | Assistant lists the correct events (time, title) for tomorrow. Response within 3s.                           |
| US201      | TC201.2      | Query calendar for a day with no events                    | 1. Ensure calendar has 0 events for day after tomorrow. 2. Ask "Any events the day after tomorrow?". | Calendar permission granted.           | Assistant responds indicating no events found for that day.                                                  |
| US201      | TC201.3      | Query calendar without permission                          | 1. Revoke/ensure no calendar permission. 2. Ask "What's my schedule today?".                             | Calendar permission NOT granted.       | Assistant responds stating it needs calendar permission and prompts the user to grant it. Does not crash.    |
| US202      | TC202.1      | Schedule a valid event with confirmation                   | 1. Ask "Schedule Dentist appt Friday 2 PM". 2. Confirm "Yes" when prompted. 3. Check device calendar.  | Calendar write permission granted.       | Assistant asks for confirmation. After "Yes", assistant confirms success. Event appears correctly in calendar. |
| US202      | TC202.2      | Attempt to schedule event with missing details             | 1. Ask "Schedule a meeting tomorrow".                                                                   | Calendar write permission granted.       | Assistant responds asking for missing details (e.g., time, title).                                           |
| US301      | TC301.1      | Query step count with permission                           | 1. Walk > 100 steps today. 2. Ask "How many steps today?".                                               | Health read permission granted (Steps). | Assistant responds with the step count retrieved from HealthKit/Connect (should be > 100).                   |
| US401      | TC401.1      | Verify voice input activation and transcription            | 1. Tap microphone button. 2. Speak "Hello assistant". 3. Stop recording.                              | Microphone permission granted.         | UI indicates listening. Text "Hello assistant" appears as user input in chat history after stopping.         |
| US402      | TC402.1      | Verify TTS output uses selected voice                      | 1. Set assistant voice to "Voice Option B". 2. Ask "What time is it?". 3. Listen to response.          | Multiple voices available.             | Assistant speaks the time using the characteristics of "Voice Option B".                                     |

**6. Timeline & Resources**

**6.1. Roadmap Visualization (High-Level)**

*   **Month 1-2 (Phase 0):** Foundation & Planning
    *   *Milestone:* Env Setup, Arch Finalized
*   **Month 3-4 (Phase 1):** MVP Development
    *   *Epics:* Core Chat, On-Device Model, Agent Framework (basic), Personalization (name), Calendar Integration
    *   *Milestone:* Internal Alpha Build
*   **Month 5-7 (Phase 2):** Feature Expansion
    *   *Epics:* Voice Interaction, Personalization (voice/avatar), Health Integration, Agent Framework (adv), Cloud Backend (basic), MCP (data collection)
    *   *Milestone:* Closed Beta Build
*   **Month 8-9 (Phase 3):** Beta Testing & Refinement
    *   *Epics:* Performance Tuning, MCP (training loop), Cloud Backend (scaling), (Opt) Messaging Integration, Bug Fixing
    *   *Milestone:* Release Candidate Build
*   **Month 10+ (Phase 4):** GA & Continuous Improvement
    *   *Milestone:* Public Launch, Ongoing Monitoring
    *   *Activities:* Continuous MCP cycles, V1.x planning

**(Note: This is illustrative; detailed sprint planning will refine task allocation.)**

**6.2. Resource Allocation by Epic (Illustrative)**

*   **ML Engineers:** On-Device Model, Cloud Model, Agent Orchestration, MCP, Health/Calendar Tool (ML aspects).
*   **Mobile Engineers (iOS/Android):** Core Chat UI, Personalization UI/Storage, Voice Interaction (STT/TTS), Native Bridge implementation for Calendar/Health/Messaging tools, On-Device Model Runtime integration.
*   **Backend Engineers:** Cloud API Service, Cloud Inference Deployment (w/ ML), Infrastructure (IaC), CI/CD, MCP pipeline infrastructure, Observability setup.
*   **UX/UI Designer:** UI mockups, interaction flows, avatar assets, personalization settings design.
*   **QA Engineers:** Test plan development, manual testing, automated test implementation (UI, API, integration), performance testing, bug tracking.
*   **Product Manager:** Backlog grooming, prioritization, stakeholder communication, requirements clarification.

**7. Risk Assessment & Mitigation (Project Risks)**

*   **Risk:** Delay in Gemma 3 model availability or required features (e.g., optimized mobile runtimes).
    *   **Mitigation:** Monitor Google AI updates closely. Have contingency to start with alternative open models if necessary (though Gemma 3 is preferred). Build abstraction layers around model interaction.
*   **Risk:** Underestimation of effort for on-device ML integration and optimization.
    *   **Mitigation:** Allocate sufficient time in Phase 1/2 for benchmarking and integration. Prioritize native performance expertise in mobile team. Start with simpler model/quantization if needed.
*   **Risk:** User adoption of personalization features is low.
    *   **Mitigation:** Conduct usability testing on personalization flows. Highlight benefits clearly in onboarding/UI. Start with simple options and iterate based on feedback.
*   **Risk:** Difficulty in obtaining reliable, anonymized data for MCP while respecting privacy.
    *   **Mitigation:** Design robust anonymization from the start. Make user consent clear and granular. Rely initially on synthetic data or publicly available datasets for tuning. Explore federated learning concepts for future versions.
*   **Risk:** Scope creep introducing unmanageable complexity.
    *   **Mitigation:** Strict adherence to V1 scope defined in PRD. Use phased approach. Defer non-essential features (like complex integrations) to V1.x. Regular backlog grooming and prioritization by PM.

**8. Approval & Review Process**

*   **Sprint Reviews:** Demo completed user stories at the end of each sprint for feedback from PM and stakeholders.
*   **Phase Gate Reviews:** Formal review at the end of each major phase (Alpha, Beta, RC) involving PM, Tech Leads, QA Lead, and key stakeholders. Approval required to proceed to the next phase.
*   **QA Sign-Off:** QA team provides sign-off on builds based on test execution results and bug thresholds before Alpha/Beta/RC/GA releases.
*   **Code Reviews:** All code changes require peer review before merging, focusing on quality, standards, security, and test coverage.
*   **Version Control:** This document will be maintained in a version control system (e.g., Git repository alongside code/docs). Changes will be proposed via pull requests or tracked in a shared document (e.g., Confluence/Wiki) with clear version history. Major updates communicated in project meetings.

**9. Future Enhancements & Next Steps**

*   **Post-GA:** Focus on monitoring, user feedback analysis, and continuous improvement via the MCP.
*   **V1.x Roadmap:** Prioritize backlog items deferred from V1 (e.g., more integrations like Email, advanced personality settings, proactive suggestions).
*   **Long-Term Vision:** Explore multilingual support, multimodal input (images), deeper third-party app integrations, advanced AR/VR avatar representations, potentially on-device fine-tuning options.
