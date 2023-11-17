const io = require("socket.io");
const ioc = require("socket.io-client");
const customParser = require("@skgdev/socket.io-msgpack-javascript");

const socket = ioc("ws://localhost:" + 5555, {
  parser: customParser.build({
    encoder: {
      ignoreUndefined: true,
    },
  }),
});

setTimeout(() => {
  socket.emit("message", "Hi");
}, 2000);

socket.on("broadcast", (msg) => {
  console.log(msg);
});
