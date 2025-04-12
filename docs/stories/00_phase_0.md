**Phase 0 Detailed Implementation Plan: Foundation & Planning (~2 Months)**

**Goal:** Establish the necessary technical foundations, validate core assumptions (especially model performance), set up development infrastructure, and ensure the team is ready to begin MVP development efficiently.

**Key Personnel Involved:** Tech Leads (ML, Mobile, Backend), DevOps/Infrastructure Engineer, Project Manager, potentially all engineers for environment setup.

---

**Step 1: Environment & Project Setup (Target: Week 1-2)**

*   **1.1. Cloud Environment Provisioning:**
    *   **Action (DevOps/Infra):** Create the designated cloud project (e.g., GCP Project).
    *   **Action (DevOps/Infra):** Set up initial Identity and Access Management (IAM) roles and permissions for team members (e.g., Project Editor, Viewer, Storage Admin, Compute Admin roles). Grant access via team groups if possible.
    *   **Action (DevOps/Infra):** Document access procedures and basic cloud project structure.
    *   **Outcome:** Team members have necessary cloud access; project structure initiated.

*   **1.2. Source Control Setup:**
    *   **Action (Tech Lead/DevOps):** Create the Git repository (e.g., on GitHub, GitLab).
    *   **Action (Tech Lead):** Define branching strategy (e.g., Gitflow, GitHub Flow - recommend simple Feature Branch flow initially).
    *   **Action (Tech Lead):** Initialize the repository with a basic structure:
        *   `README.md` (project overview, setup instructions).
        *   `.gitignore` (common ignores for Python, iOS, Android, IDE files).
        *   `docs/` directory (for PRD, TDD, etc.).
        *   Placeholder directories: `mobile/ios`, `mobile/android`, `backend/`, `ml/` (for ML-specific scripts/notebooks), `infra/` (for IaC code).
    *   **Action (All Engineers):** Clone the repository locally.
    *   **Outcome:** Centralized code repository established; branching strategy defined; basic project structure exists.

*   **1.3. Developer Environment Configuration:**
    *   **Action (All Engineers):** Install necessary IDEs: Xcode (latest stable) for iOS, Android Studio (latest stable) for Android, VS Code or PyCharm for Python/Backend.
    *   **Action (All Engineers):** Install required SDKs: iOS SDK (via Xcode), Android SDK (via Android Studio), specific API levels targeted by PRD.
    *   **Action (All Engineers):** Install Python (e.g., 3.10+). Set up preferred Python environment management (e.g., `venv`, `conda`, recommend `poetry` or `pipenv` for dependency locking). Install basic Python tools (`pip`, `git`).
    *   **Action (All Engineers):** Install Docker Desktop (or alternative).
    *   **Action (All Engineers):** Install Cloud SDK CLI (e.g., `gcloud`). Authenticate with cloud account.
    *   **Action (All Engineers):** Install Infrastructure as Code tool (e.g., Terraform CLI).
    *   **Action (Tech Leads):** Document required versions and setup steps in the project `README.md`.
    *   **Outcome:** All engineers have a consistent local development environment capable of building mobile apps, running backend services (locally via Docker), and interacting with cloud infrastructure.

*   **1.4. Communication Channel Setup:**
    *   **Action (Project Manager/Tech Lead):** Set up project channels in Slack/Teams (e.g., `#proj-gemma-assistant-general`, `#proj-gemma-assistant-backend`, `#proj-gemma-assistant-mobile`, `#proj-gemma-assistant-ml`).
    *   **Action (Project Manager):** Schedule recurring project meetings (e.g., daily stand-ups, sprint planning/review).
    *   **Outcome:** Clear communication pathways established.

---

**Step 2: Infrastructure Scaffolding with IaC (Target: Week 2-3)**

*   **2.1. Initialize IaC Project:**
    *   **Action (DevOps/Infra):** Initialize a Terraform project within the `infra/` directory of the Git repository.
    *   **Action (DevOps/Infra):** Configure Terraform backend for state management (e.g., using GCS bucket).
    *   **Outcome:** Terraform project structure ready.

*   **2.2. Define Core Network Infrastructure:**
    *   **Action (DevOps/Infra):** Write Terraform configuration (`.tf` files) to define:
        *   Virtual Private Cloud (VPC) network.
        *   Subnets in relevant regions.
        *   Basic firewall rules (e.g., allow SSH/RDP from specific IPs, deny all ingress by default).
    *   **Action (DevOps/Infra):** Apply the configuration (`terraform apply`).
    *   **Outcome:** Core network foundation exists in the cloud, managed by code.

*   **2.3. Define Placeholder Compute & Storage:**
    *   **Action (DevOps/Infra):** Add Terraform resources for:
        *   **Kubernetes Cluster (e.g., GKE):** Define a *minimal* cluster configuration (e.g., single node pool, smallest machine type). Focus is on resource definition, not full production setup. *Alternatively, define placeholder Vertex AI Endpoint/Training job configurations if using managed services.*
        *   **Cloud Storage Buckets:** Define buckets for:
            *   `[project-id]-gemma-assistant-logs` (for anonymized logs later).
            *   `[project-id]-gemma-assistant-models` (for storing base models, adapters).
            *   Configure buckets with private access, versioning (optional), encryption at rest (default).
    *   **Action (DevOps/Infra):** Apply the configuration.
    *   **Outcome:** Essential storage buckets created; basic K8s/Vertex AI resource definitions exist.

*   **2.4. Basic CI/CD Pipeline Structure:**
    *   **Action (DevOps/Backend Lead):** Create initial CI workflow file (e.g., `.github/workflows/ci.yml`).
    *   **Action (DevOps/Backend Lead):** Configure the workflow to trigger on pushes/PRs to main/develop branches.
    *   **Action (DevOps/Backend Lead):** Add basic steps:
        *   Checkout code.
        *   Set up Python.
        *   Install backend dependencies (`poetry install` or `pip install -r requirements.txt`).
        *   Run linters (`flake8`, `black --check`).
        *   (Placeholder) Run backend unit tests (will fail initially).
        *   (Optional Placeholder) Steps for setting up Java/Kotlin for Android build checks, Swift for iOS build checks.
    *   **Outcome:** A basic CI pipeline runs automatically, providing initial code quality checks.

---

**Step 3: Gemma 3 Model Evaluation & Conversion (Target: Week 3-6)**

*   **3.1. Obtain Gemma 3 Models:**
    *   **Action (ML Engineer):** Identify target models on Hugging Face Hub:
        *   **Android/TF Lite:** `litert-community/Gemma3-1B-IT` (provides pre-quantized `dynamic_int4` `.tflite`)
        *   **iOS/Core ML:** `google/gemma-3-1b-it` (will require quantization/conversion)
        *   *(Optional)* Consider `google/gemma-3-4b-it` variants if 1B proves insufficient later.
    *   **Action (ML Engineer):** Ensure access (accept license/terms on HF). Download model artifacts (`.tflite` for Android, weights/config for iOS) locally or to a shared cloud environment. Reference: [https://huggingface.co/litert-community/Gemma3-1B-IT](https://huggingface.co/litert-community/Gemma3-1B-IT)
    *   **Outcome:** Candidate base models (including pre-converted TF Lite) available.

*   **3.2. Prototype Quantization (Primarily for Core ML):**
    *   **Action (ML Engineer):** Research quantization for Core ML target (e.g., using `coremltools` native capabilities or converting from a quantized intermediate format). Goal: `int4` or similar low-bit precision.
    *   **Action (ML Engineer):** If necessary, write/adapt Python scripts for quantizing the base `google/gemma-3-1b-it` model for Core ML conversion.
    *   **Outcome:** Process for Core ML quantization defined; Quantized model for Core ML prepared (if not handled by converter). *Note: TF Lite quantization is provided by the downloaded `litert-community` model.*

*   **3.3. Prototype Conversion to Mobile Formats:**
    *   **Action (ML Engineer):** Write/adapt Python conversion scripts:
        *   **TF Lite:**
            *   **Primary:** Use the downloaded `.tflite` from `litert-community/Gemma3-1B-IT`.
            *   *(Optional)* Keep TF Lite conversion script (`tf.lite.TFLiteConverter`) handy if modifications or different base models are needed later.
        *   **Core ML:**
            *   Convert the quantized (or original) `google/gemma-3-1b-it` model (PyTorch/TF/ONNX) using `coremltools.convert()`. Specify target iOS version and `compute_units`. Aim for `.mlpackage`.
            *   *Challenge: `coremltools` compatibility with Gemma 3 architecture and ops.*
    *   **Outcome:** `.tflite` (downloaded) and `.mlpackage` (converted) files generated/obtained. Conversion process for Core ML documented.

*   **3.4. Minimal Mobile App Inference Test:**
    *   **Action (Mobile Engineer - iOS):** Create a minimal Xcode project. Add the generated `.mlpackage` file. Write Swift code using Core ML APIs to load, prepare input (dummy/basic tokenization), execute, and log results.
    *   **Action (Mobile Engineer - Android):** Create a minimal Android Studio project. Add the downloaded `.tflite` file to `assets`. Write Kotlin code using the TF Lite Interpreter API (`org.tensorflow.lite`) to load (consider NNAPI delegate), prepare input `ByteBuffer` (dummy/basic tokenization), execute, and log results.
    *   **Outcome:** Confirmed ability to load and run *basic inference* with the respective models on target platforms.

*   **3.5. Benchmarking Performance:**
    *   **Action (Mobile Engineers w/ ML Support):** Refine minimal apps for performance measurement.
    *   **Action (Mobile Engineers):** Use platform tools:
        *   **iOS:** Xcode Instruments (Time Profiler, Core ML instrument, Memory Graph Debugger).
        *   **Android:** Android Studio Profiler (CPU, Memory, Energy).
    *   **Action (Mobile Engineers):** Measure on target hardware (representative physical devices, low/mid/high tier if possible) and latest simulators:
        *   **Latency:** Average time per inference (token generation loop if possible, otherwise full sequence). P50, P90, P99 latencies.
        *   **Memory:** Peak RAM usage during model load and inference.
        *   **CPU/GPU/NPU Usage:** Utilization percentages during inference.
        *   **Battery Impact:** (Longer term test) Energy impact reported by profilers.
    *   **Action (ML/Mobile Leads):** Compile results for different models (1B vs 4B) and quantization levels. Compare against PRD latency targets (<1.5s).
    *   **Decision Point:** Based on benchmarks, select the primary on-device model variant (e.g., Gemma 3 1B int4) for the Phase 1 MVP. Identify if fallback to cloud will likely be needed often. Document the decision and rationale.
    *   **Outcome:** Performance characteristics understood; primary on-device model selected; potential risks identified.

---

**Step 4: Core Project Setup (Target: Week 7-8)**

*   **4.1. Initialize Mobile Application Shells:**
    *   **Action (Mobile Engineers):** Flesh out the basic structure within the `mobile/ios` and `mobile/android` directories created in Step 1.2.
    *   **iOS:** Set up main application target, basic `AppDelegate`/`SceneDelegate`, initial `ContentView` (SwiftUI) or `ViewController` (UIKit). Add core dependencies (e.g., navigation, potentially logging framework like `CocoaLumberjack`).
    *   **Android:** Set up application module, `MainActivity`, basic layout/composable structure. Add core dependencies (e.g., Jetpack libraries - ViewModel, LiveData/Flow, Navigation, Material Components; potentially logging framework like `Timber`). Define basic app theme.
    *   **Outcome:** Runnable (empty) mobile apps with basic structure and dependencies.

*   **4.2. Initialize Backend Project Structure:**
    *   **Action (Backend Engineer):** Flesh out the `backend/` directory structure using FastAPI best practices:
        *   `app/main.py` (FastAPI app instance).
        *   `app/api/v1/endpoints/` (for API route definitions).
        *   `app/core/` (for config, core logic).
        *   `app/models/` (for Pydantic models).
        *   `app/services/` (for business logic).
        *   `requirements.txt` or `pyproject.toml` (using Poetry/Pipenv). Include `fastapi`, `uvicorn[standard]`, `pydantic`.
        *   Basic `Dockerfile` for containerizing the backend.
    *   **Action (Backend Engineer):** Implement a simple health check endpoint (e.g., `/health`).
    *   **Outcome:** Structured backend project ready for API endpoint implementation. Runnable locally via `uvicorn` or Docker.

*   **4.3. Integrate Basic Logging:**
    *   **Action (Backend Engineer):** Configure standard Python `logging` in `app/core/config.py` or similar. Output basic logs to console.
    *   **Action (Mobile Engineers):** Integrate chosen logging frameworks (`Timber`/`CocoaLumberjack`) and add basic log statements at app startup/key points.
    *   **Outcome:** Consistent logging approach initiated across components.

---

**End of Phase 0 - Expected State:**

*   All environments (Cloud, Git, Local Dev) are set up and accessible.
*   Basic cloud infrastructure (Network, Storage, placeholder Compute) is defined via IaC (Terraform).
*   A rudimentary CI pipeline provides basic checks.
*   Viability of running target Gemma 3 models (quantized, converted) on mobile is confirmed through prototyping and benchmarking.
*   A primary on-device model variant is selected for the MVP.
*   Skeleton projects for iOS, Android, and Backend are created with basic structure and dependencies.
*   The team has a shared understanding of the architecture (from TDD) and the initial plan (from Project Plan).
*   **Ready to start Phase 1: MVP Development.**