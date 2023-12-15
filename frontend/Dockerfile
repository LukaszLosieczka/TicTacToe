# Stage 1: Build the Angular app
FROM node:20 AS build

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to install dependencies
COPY package.json package-lock.json /app/

# Install Angular CLI globally
RUN npm install -g @angular/cli

# Install app dependencies
RUN npm install

# Copy the entire application directory into the container
COPY . /app/

# Build the Angular app for production with the "prod" configuration
RUN ng build

# Stage 2: Create a lightweight container with a web server to serve the built app
FROM nginx:alpine

# Remove default nginx welcome page
RUN rm -rf /usr/share/nginx/html/*

COPY --from=build /app/nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built Angular app from the previous stage to the nginx public directory
COPY --from=build /app/dist/frontend/browser/* /usr/share/nginx/html/

# Expose port 80
EXPOSE 80

# Start nginx when the container starts
CMD ["nginx", "-g", "daemon off;"]
