const { Server } = require("socket.io");
const cors = require("cors");


const io = new Server({
    cors: {
        origin: "*",
        methods: ["GET", "POST"],
    },
});

io.on("connection", (socket) => {
    console.log("a user connected to the socket.io server: " + socket.id);
    // console.log(JSON.stringify(socket.handshake, null, 2));

    socket.use((packet, next) => {
        console.log("packet: " + packet);
        next();
    });

    socket.on("message", (msg) => {
        console.log("message: " + msg);
        io.emit("message", "they say: " + msg);
    });

    socket.on("disconnect", () => {
        console.log("user disconnected: " + socket.id);
    });
});

io.listen(3000);