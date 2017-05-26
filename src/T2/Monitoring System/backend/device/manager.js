var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http, {'pingInterval': 2500, 'pingTimeout': 10000});
var models = require('../database/models.js');

var clients = [];

io.use(function (socket, next) {
    return next();
});

/* Handles the connection of a device */
io.sockets.on('connection', function (socket) {

    socket.on('newDevice', function (data) {

        /* if client is non-existent store it in the clients array (the object you store is up to you) the id of the socket is
         obtainable in the socket object : socket.id */
        var i = 0;

        while (i < clients.length && clients[i].name != data.name) {
            i++;
        }
        if (i >= clients.length) {
            // if client is non-existent store it in clients array and perform rest of logic
            var client = { // creates new client object to store socket id and username of this connection
                id: socket.id,
                name: data.name
            };
            clients.push(client); //update database user entry to user logged. Might be done more than once for the same user
            //check database to see if user already logged
            models.Device.findOne({name: data.name}, function (err, profile) {
                if (err) {
                    console.error(err);
                } else {
                    console.log("Socket Device Registered Successfully");
                }
            });
        }
        console.log("new device event received for existing user");
    });

    socket.on('value', function (data) {

        var i = 0;
        var deviceName;

        for (i = 0; i < clients.length; i++) {
            if (clients[i].id == socket.id) deviceName = clients[i].name;
        }
        i = 0;
        while (i < clients.length && clients[i].name != deviceName) {
            i++;
        }
        if (i < clients.length) {
            models.Device.findOne({name: deviceName}, function (err, device) {
                if (device != null) {
                    device.readings.push(data.value);
                    models.Device.update({name: deviceName}, {$set: {readings: device.readings}}, function (err) {
                        if (err) {
                            console.log("Error while updating device readings");
                        } else {
                            console.log("Device Reading Added Successfully");
                        }
                    });
                } else {
                    console.log("Device not found");
                }
            });
        }
        console.log("New device value event received for existing user");
    });

    socket.on('disconnect', function () {
        var i = 0;
        var device;
        for (i = 0; i < clients.length; i++) {
            if (clients[i].id == socket.id) device = clients[i].name;
        }
        i = 0;
        while (i < clients.length && clients[i].name != device) {
            i++;
        }

        if (i < clients.length) {
            console.log("Device (" + clients[i].name + ") was disconnected");
            //remove client from array of clients
            clients.splice(i, 1);
        }
    });

});

http.listen(8080, function () {
    console.log('[Device Manager API] Ready.');
});

module.exports = this;

