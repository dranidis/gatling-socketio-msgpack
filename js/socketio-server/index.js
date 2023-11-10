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

io.listen(3000);
