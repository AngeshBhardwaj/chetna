FROM node:24-slim

WORKDIR /design/prototype

COPY prototype/package.json prototype/package-lock.json ./
RUN npm install

COPY prototype/ .

EXPOSE 5173

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
