FROM node:20-alpine AS build

WORKDIR /app/frontend

COPY frontend/package.json frontend/package-lock.json ./

RUN npm ci

COPY frontend .

ARG VITE_API_BASE_URL=/api
ARG VITE_TENCENT_MAP_KEY=replace-with-tencent-map-key

ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_TENCENT_MAP_KEY=${VITE_TENCENT_MAP_KEY}

RUN npm run build

FROM nginx:1.27-alpine

COPY deploy/internal-trial/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/frontend/dist /usr/share/nginx/html

EXPOSE 80
