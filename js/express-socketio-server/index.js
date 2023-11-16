//
// Starts a web server at port 3333
// and a websocket server at the same port.
//
// The websocket server is used to send and receive messages
// from the client with the event name "message".
//
// root is redirected to index.html that provides a form
// to send messages to the server and display messages
// received from the server.
//

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
  },
});

app.get("/", (req, res) => {
  res.sendFile(__dirname + "/index.html");
});

io.on("connection", (socket) => {
  console.log("a user connected to the socket.io server: " + socket.id);

  socket.on("message", (msg) => {
    console.log("message: " + msg);
    io.emit("broadcast", "they say: " + msg);
  });


  socket.on("request", (msg) => {
    console.log("request message: " + msg);
    socket.emit("response", "on message" + msg);
  });


  socket.on("disconnect", () => {
    console.log("user disconnected: " + socket.id);
  });
});

const namespace = io.of("/admin");

namespace.on("connection", (socket) => {
  console.log("someone connected to the admin namespace: " + socket.id);

  socket.on("message", (msg) => {
    console.log("message: " + msg);
    namespace.emit("broadcast", "admin they say: " + msg);
  });

  socket.on("request", (msg) => {
    console.log("request message: " + msg);
    socket.emit("response", "on message" + msg);
  });

  socket.on("disconnect", () => {
    console.log("someone disconnected from the admin namespace: " + socket.id);
  });
});

httpServer.listen(3333, () => {
  console.log("listening on *:3333");
});
