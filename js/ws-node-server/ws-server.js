// https://socket.io/get-started/chat

const express = require("express");
const http = require("http");
const cors = require("cors");

const app = express();
app.use(cors()); // Enable CORS for all routes

const httpServer = http.createServer(app);
const { Server } = require("socket.io");

const io = new Server(httpServer, {
  transports: ["websocket", "polling"],
  // path: "/raw", // This specifies the namespace or path);
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
  }
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

httpServer.listen(3000, () => {
  console.log("listening on *:3000");
});
