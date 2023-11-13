const { Server } = require("socket.io");
const cors = require("cors");

const io = new Server({
  transports: ["websocket", "polling"],
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
  },
});

io.on("connection", (socket) => {
  console.log("a user connected to the socket.io server: " + socket.id);

  socket.on("message", (msg) => {
    console.log("message: " + msg);
    io.emit("broadcast", "they say: " + msg);
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

  socket.on("disconnect", () => {
    console.log("someone disconnected from the admin namespace: " + socket.id);
  });
});

io.listen(3000);

console.log("socket.io server listening on port 3000");
