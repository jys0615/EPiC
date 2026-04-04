FROM node:20 AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .
COPY ./vite.config.js ./

RUN if [ "$HOST" = "docker" ]; then \
    sed -i 's|http://localhost:8090|http://host.docker.internal:8090|g' src/config.js && \
    sed -i 's|http://localhost:8000|http://host.docker.internal:8000|g' src/config.js; \
    fi

RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]
