# Advanced Search Deployment Documentation

This document describes the steps and requirements for deploying **Advanced Search**. The service depends on several external and internal services, which must be configured correctly before deployment.

---

## Prerequisites

* **Runtime Environment**:

    * Java 11+
    * Docker & Docker Compose
* **Other Services Dependencies**:
 
    * PostgreSQL (>=15)
    * CADC/CCDA Web Site 
    * Registry
    * Access (for authentication)
    * TAP
    * DownloadManager
    * CAOM2-UI

---

## Deployment Architecture

The service is composed of the following components:

* **Web API** (main application)
* **Database** (PostgreSQL)
* **Backend** TAP service for querying CAOM-2 data
* **Discovery Service** (`reg` for registry lookups)
* **Authentication Provider** (`access` web service for optional authenticated access to proprietary data)

---

## Step-by-Step Deployment

Using Docker Compose.
As Advanced Search is a read-only application, the Production `reg` and TAP (`argus`) services can be used.  The local `docker-compose.yaml` file will deploy the
necessary local services only.

### 1. Configure Environment

#### Create the `advancedsearch_config` Docker volume to hold Advanced Search configuration files

```bash
docker volume create advancedsearch_config
```

Copy the following expected files into the volume:
* `RsaSignaturePub.key`
* `cadcproxy.pem` (the `servops` user)
* `org.opencadc.search.properties`
* `cadc-log.properties` (to configure logging)
* `cadc-registry.properties` (to configure registry service access)
* `tmpops.pem` (to store temporary files)

The `org.opencadc.search.properties` contains the TAP Service lookup:
```properties
org.opencadc.search.tap-service-id=ivo://cadc.nrc.ca/argus
```

```bash
docker run --rm -v advancedsearch_config:/data -w /data alpine sh -c "echo 'org.opencadc.search.tap-service-id=ivo://cadc.nrc.ca/argus' > org.opencadc.search.properties"
```

The `pem` certificates and the `RsaSignaturePub.key` should be provided by the CADC Development (or Ops) team.

#### Create the `access_config` Docker volume to hold the Access configuration files

```bash
docker volume create access_config
```

Copy the following expected files into the volume:
* `access.properties`
* `RsaSignaturePriv.key`
* `RsaSignaturePub.key`
* `cacerts` (Folder containing CA certificates)
* `cadc-log.properties`
* `cadc-registry.properties`
* `cadcproxy.pem` (the `servops` user)

See the [access service documentation](https://github.com/opencadc/ac/tree/main/access#aai-helper-service-for-web-site-access) for details on these files.

Alternatively, create a `ca_certs` Docker volume and mount it at `/config/cacerts`.

### 2. Run Service

If you run your own Proxy server, this can be skipped.

Create the `server_certs` Docker volume to hold the proxy server's server certificate and key

```bash
docker volume create server_certs
```

After the Proxy Service is configured, run the services:

```bash
docker-compose up -d
```

---

## Troubleshooting

* **Database connection error**: Verify `DATABASE_URL` and ensure PostgreSQL is running.
* **Redis unavailable**: Ensure Redis container/service is up.
* **Auth errors**: Confirm OAuth credentials and callback URLs.
* **Port conflicts**: Check if port `3000` (or configured port) is already in use.
