# Cluster Controller

A web-based interface for managing and interacting with SLURM clusters.

## Overview

The main idea of this project is to minimize the effort required to create and administer users for SLURM clusters by providing the following tools:

1. **User Creation** — Creating users in both the `slurmdbd` database and on the head nodes of the compute cluster;
2. **Database-Level Restrictions** — Managing user quotas and limits within the `slurmdbd` database (stored in PostgreSQL);
3. **Disk Quota Management** — Enforcing user restrictions based on available disk space;
4. **Web Interface** — Providing users with access to the cluster via a web application (terminal, task queue, task launch/cancellation);
5. **User Grouping** — Grouping application users and mapping them to system-level profiles;

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.5.16
- **Language:** Java 25
- **Database:** PostgreSQL (with Liquibase for migrations)
- **Authentication:** JWT tokens (Nimbus JOSE+JWT)
- **Security:** Spring Security
- **Communication:** WebSocket, REST API
- **SSH/SFTP:** Apache SSHD (for terminal and file management)
- **Utilities:** Lombok, PTY4J (pseudo-terminal for Unix-like terminals)

### Frontend
- **Framework:** React 19 (Create React App)
- **Terminal:** xterm.js with addons (attach, fit, WebGL)
- **Charts:** Recharts (for cluster statistics)
- **Routing:** React Router DOM v7
- **HTTP Client:** Axios

## Architecture

For detailed architecture diagram, see [docs/README.md](docs/README.md).

## Features

### Admin Capabilities
- Manage clusters and connect them to the application
- Create, update, and delete user profiles with resource limits (CPU, RAM, disk quota, GPU)
- Manage user groups and assign users to clusters
- Monitor system statistics
- Submit, monitor, and cancel tasks on behalf of users
- Downloading user directories

### User Capabilities
- Submit, monitor, and cancel tasks
- Access an interactive terminal (WebSocket-based)
- View task queue and cluster statistics

## Prerequisites

Before deploying the application, ensure the following:

- **Administrator profile** on cluster head nodes and in `slurmdbd`, along with the group defined in the project's configuration file. This group is required for accessing created user directories;
- **(Optional)** The `quota` package installed and a pre-mounted quota-enabled partition;
- **slurmrestd** configured with JWT token authorization and TLS enabled;
- **PostgreSQL** database available (version 15+ recommended);

## Building the Project

### Backend

Navigate to the project directory and run the Gradle build script:

```bash
./gradlew build
```

> The application will produce a runnable `.jar` file in `build/libs/`.

```bash
docker build . -t ghcr.io/ambrosia0/slurm-controller:1.0.0
```

> Produces container image

### Frontend

Navigate to the frontend directory:

```bash
cd frontend
npm install
npm start        # Start development server (localhost:3000)
npm run build    # Build for production
```

The built frontend files are served statically by the Spring Boot application from `src/main/resources/static/`.

## Deployment

### Docker Compose (Development)

First, configure the environment variables in `docker-compose-dev.yaml`, then build and run:

```bash
docker compose up --build
```

## Configuration

All configuration is done via environment variables. Key variables include:

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | PostgreSQL connection URL | — |
| `DB_USER` | Database username | — |
| `DB_PASSWORD` | Database password | — |
| `PASSWORD_ENCRYPTION_KEY` | Key for password encryption | — |
| `PASSWORD_ENCRYPTION_SALT` | Salt for password encryption | — |
| `ADMIN_GROUP` | Default user group name | `webguiusers` |
| `SLURM_DEFAULT_ACCOUNT` | Default SLURM account | `default` |
| `SLURM_TOKEN_DURATION` | Duration of SLURM tokens (seconds) | `3600` |
| `QUOTA_SUPPORT` | Enable disk quota support | `true` |
| `ADMIN_USERNAME` | Initial admin username | — |
| `ADMIN_PASSWORD` | Initial admin password | — |
| `ACCESS_TOKEN_DURATION` | JWT access token expiration (seconds) | — |
| `REFRESH_TOKEN_DURATION` | JWT refresh token expiration (seconds) | — |
| `DISK_QUOTA_PATH` | Path to quota-enabled partition | `/data` |
| `HOME_DIRECTORY_PATH` | Path to user home directories | `/home` |

### Production Profile

When running with the `prod` profile, SSL is enabled automatically. Configure the following:

| Variable | Description |
|---|---|
| `KEY_STORE_PASSWORD` | Password for the PKCS12 keystore |

The keystore file (`keystore.p12`) should be placed in `src/main/resources/`.

## Running Tests

```bash
./gradlew test
```