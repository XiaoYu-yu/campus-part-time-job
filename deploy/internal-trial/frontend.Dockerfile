ARG NODE_IMAGE=docker.io/library/node:20-bookworm-slim
ARG NGINX_IMAGE=docker.io/library/nginx:1.27-alpine
ARG NPM_REGISTRY=https://registry.npmmirror.com

FROM ${NODE_IMAGE} AS build

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json ./

RUN npm config set registry "${NPM_REGISTRY}"
RUN npm ci

COPY frontend .

ARG VITE_API_BASE_URL=/api
ARG VITE_TENCENT_MAP_KEY=replace-with-tencent-map-key

ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_TENCENT_MAP_KEY=${VITE_TENCENT_MAP_KEY}

RUN npm run build

FROM ${NGINX_IMAGE}

COPY deploy/internal-trial/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/frontend/dist /usr/share/nginx/html

EXPOSE 80
