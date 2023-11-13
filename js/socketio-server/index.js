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
  console.log(
    "a user connected to the socket.io server: " +
      socket.id +
      " Connected Users: " +
      io.of("/").sockets.size
  );

  socket.join("room1");

  const rooms = io.of("/").adapter.rooms;
  const sids = io.of("/").adapter.sids;

  console.log("rooms: ", rooms);
  console.log("sids: ", sids);

  socket.on("message", (msg) => {
    console.log("message: " + msg);
    io.emit("message", "they say: " + msg);
  });

  socket.on("disconnect", () => {
    console.log(
      "user disconnected: " +
        socket.id +
        " Connected Users: " +
        io.of("/").sockets.size
    );
  });
});

const namespace = io.of("/admin");

namespace.on("connection", (socket) => {
  console.log("someone connected to the admin namespace: " + socket.id);

  socket.on("message", (msg) => {
    console.log("message: " + msg);
    namespace.emit("message", "admin they say: " + msg);
  });

  socket.on("disconnect", () => {
    console.log("someone disconnected from the admin namespace: " + socket.id);
  });
});

io.listen(3000);
