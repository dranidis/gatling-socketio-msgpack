// https://socket.io/get-started/chat

const express = require("express");
const http = require("http");
const cors = require("cors");

const app = express();
app.use(cors()); // Enable CORS for all routes

const server = http.createServer(app);
const { Server } = require("socket.io");

const io = new Server(server, {
  transports: ["websocket", "polling"],
  // path: "/raw", // This specifies the namespace or path);
});

app.get("/", (req, res) => {
  res.sendFile(__dirname + "/index.html");
});

io.on("connection", (socket) => {
  console.log("a user connected");

  socket.on("chat message", (msg) => {
    console.log("message: " + msg);
  });
});

server.listen(3000, () => {
  console.log("listening on *:3000");
});
