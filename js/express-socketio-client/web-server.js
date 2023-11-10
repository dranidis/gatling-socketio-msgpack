// https://socket.io/get-started/chat

const express = require("express");
const cors = require("cors");

const app = express();

app.use(cors()); // Enable CORS for all routes

// const server = http.createServer(app);


app.get("/", (req, res) => {
  res.sendFile(__dirname + "/index.html");
});


app.listen(5000, () => {
  console.log("listening on *:5000");
});
