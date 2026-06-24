# Standalone Podman deployment

This deployment path is for an isolated validation host where Docker Compose is
not installed. It does not join or modify Kubernetes, Hadoop, HBase, or any
other cluster.

## Isolation boundary

- All containers, images, networks, and volumes use the `campus-standalone-`
  prefix.
- Frontend is exposed on `18080` by default.
- Backend is bound to `127.0.0.1:18081`.
- MySQL is bound to `127.0.0.1:13306`.
- Existing host MySQL, Java, Hadoop, HBase, and cluster configuration are not
  read, stopped, or modified.
- `stop.sh` preserves database and upload volumes.

## Prepare

```bash
cd /opt/campus-part-time-job-standalone
chmod +x deploy/standalone-podman/*.sh
PUBLIC_ORIGIN=http://your-host:18080 \
  bash deploy/standalone-podman/init-env.sh
```

The generated `.env` is private, mode `0600`, and ignored by Git.

## Deploy

```bash
bash deploy/standalone-podman/deploy.sh
```

The first build pulls Maven, Eclipse Temurin, Node, Nginx, and MySQL images.
Later runs reuse the Podman cache and preserve named data volumes. The script
uses China-friendly mirror defaults that can be overridden when needed:

```bash
MAVEN_IMAGE=docker.io/library/maven:3.9.9-eclipse-temurin-17 \
JRE_IMAGE=docker.io/library/eclipse-temurin:17-jre \
NODE_IMAGE=docker.io/library/node:20-alpine \
NGINX_IMAGE=docker.io/library/nginx:1.27-alpine \
MYSQL_IMAGE=docker.io/library/mysql:8.4 \
NPM_REGISTRY=https://registry.npmjs.org \
  bash deploy/standalone-podman/deploy.sh
```

Container secrets are inherited from the private `.env` as environment names,
so password values are not placed directly in the `podman run` command line.

## H2 smoke fallback

If the validation host can pull the build images but cannot pull a MySQL image,
you can start a non-persistent smoke-only stack:

```bash
bash deploy/standalone-podman/deploy-h2-smoke.sh
```

This starts the backend with Spring `test` profile and in-memory H2 seed data.
Use it only for LAN smoke verification. It is not a production or persistent
deployment.

## Check

```bash
bash deploy/standalone-podman/status.sh
curl http://127.0.0.1:18080/api/campus/public/health
```

From a trusted LAN client:

```text
http://your-host:18080/
```

## Stop

```bash
bash deploy/standalone-podman/stop.sh
```

This stops only the three `campus-standalone-*` containers and keeps their
volumes. It does not touch cluster services.
