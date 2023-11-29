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

  socket.on("room:join", (event, data) => {
    console.log(event);
    console.log(data);
  });

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

  socket.on("request", (msg) => {
    console.log("request message: " + msg);
    socket.emit("response", "on message" + msg);
  });

  socket.on("disconnect", () => {
    console.log("someone disconnected from the admin namespace: " + socket.id);
  });
});

const live = io.of("/events/live/en");
live.on("connection", (socket) => {
  console.log("someone connected to the live namespace: " + socket.id);

  socket.on("room:join", (msg1, msg2) => {
    console.log(typeof msg2);
    console.log("room:join: " + msg1 + " " + JSON.stringify(msg2));
    if (msg1 === "odd") {
      console.log(msg2.oddId, msg2.eventId, msg2.marketId);
    }
  });

  socket.on("room:leave", (msg) => {
    console.log("room:leave: " + msg);
  });

  socket.on("disconnect", () => {
    console.log("someone disconnected from the live namespace: " + socket.id);
  });
});

io.listen(3333);

console.log("socket.io server listening on port 3333");
