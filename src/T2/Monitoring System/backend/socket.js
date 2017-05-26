var models = require('./database/models.js');
var clients = []; // array to store clients

var ioSocket = null; // global store object for websocket


var SocketController = {

    StartSocket: function StartSocket(io, socketioJwt, secret) {

        // Log out all the users when the server start
        models.User.find({isLogged: true}, {username: 1}, function (err, onlineUsers) {
            if (err) {
                console.error(err);
            } else {
                if (onlineUsers != null) {

                    for (var i = 0; i < onlineUsers.length; i++) {

                        models.User.update({username: onlineUsers[i].username}, {$set: {isLogged: false}}, function (err, User) {
                            //update user isLogged
                            if (err) {
                                console.error(err);
                            }
                        });
                    }
                }
            }
        });
        // Log out all the producers when the server start
        models.Producer.find({isLogged: true}, {username: 1}, function (err, onlineUsers) {
            if (err) {
                console.error(err);
            } else {
                if (onlineUsers != null) {

                    for (var i = 0; i < onlineUsers.length; i++) {

                        models.Producer.update({username: onlineUsers[i].username}, {$set: {isLogged: false}}, function (err, User) {
                            //update user isLogged
                            if (err) {
                                console.error(err);
                            }
                        });
                    }
                }
            }
        });

        io.set('authorization', socketioJwt.authorize({
            secret: secret,
            handshake: true
        }));

        io.sockets.on('connection', function (socket) {

            socket.on('newUser:username', function (data) {

                console.log("Entered Socket Login User");

                /* if client is non-existent store it in the clients array (the object you store is up to you) the id of the socket is
                 obtainable in the socket object : socket.id */
                var i = 0;

                while (i < clients.length && clients[i].username != data.username) {
                    i++;
                }
                if (i >= clients.length) {
                    // if client is non-existent store it in clients array and perform rest of logic
                    var client = { // creates new client object to store socket id and username of this connection
                        id: socket.id,
                        username: data.username,
                        role: "user"
                    };
                    clients.push(client); //update database user entry to user logged. Might be done more than once for the same user
                    //check database to see if user already logged
                    models.User.findOne({$and: [{username: data.username}, {isLogged: true}]}, function (err, profile) {
                        if (err) {
                            console.error(err);
                        } else {
                            if (profile == null) {
                                // user is still not logged in
                                models.User.update({username: data.username}, {$set: {isLogged: true}}, function (err, User) {
                                    //update user isLogged
                                    if (err) {
                                        console.error(err);
                                    }
                                });
                            }
                        }
                    });

                    models.User.findOne({username: data.username}, {devices: 1}, function (err, user) {

                        if (err) {
                            console.log(err);
                        } else {
                            socket.emit('list:userDevices', {devices: user.devices});
                        }
                    });

                    models.Producer.find({}, {name: 1, device_models: 1, device_types: 1}, function (err, Producers) {
                        if (err) {
                            console.error(err);
                        } else {
                            socket.emit('list:producers', {Producers: Producers});
                        }
                    });
                }
                console.log("New user event received for existing user");
            });

            socket.on('newProducer:username', function (data) {

                console.log("Entered Socket Login Producer");

                var i = 0;

                while (i < clients.length && clients[i].username != data.username) {
                    i++;
                }
                if (i >= clients.length) {
                    // if client is non-existent store it in clients array and perform rest of logic
                    var client = { // creates new client object to store socket id and username of this connection
                        id: socket.id,
                        username: data.username,
                        role: "producer"
                    };
                    clients.push(client); //update database user entry to user logged. Might be done more than once for the same user
                    //check database to see if user already logged
                    models.Producer.findOne({$and: [{username: data.username}, {isLogged: true}]}, function (err, profile) {
                        if (err) {
                            console.error(err);
                        } else {
                            if (profile == null) {
                                // user is still not logged in
                                models.Producer.update({username: data.username}, {$set: {isLogged: true}}, function (err, User) {
                                    //update user isLogged
                                    if (err) {
                                        console.error(err);
                                    }
                                });
                            }
                        }
                    });
                }
                console.log("New producer event received for existing producer");
            });

            socket.on('disconnect', function () {
                console.log('Got disconnect!');
                var i = 0;
                var user1;
                for (i = 0; i < clients.length; i++) {
                    if (clients[i].id == socket.id) user1 = clients[i].username;
                }
                i = 0;
                while (i < clients.length && clients[i].username != user1) {
                    i++;
                }

                if (i < clients.length) {
                    if (clients[i].role == "user") {
                        console.log("disconnect user:" + clients[i].username);
                        models.User.update({username: user1}, {$set: {isLogged: false}}, function (err, User) {
                            if (err) {
                                console.error(err);
                            }
                        });
                    }
                    if (clients[i].role == "producer") {
                        console.log("disconnect producer:" + clients[i].username);

                        models.Producer.update({username: user1}, {$set: {isLogged: false}}, function (err, User) {
                            if (err) {
                                console.error(err);
                            }
                        });
                    }
                    //remove client from array of clients
                    clients.splice(i, 1);
                }
            });

            socket.on('logout_user', function (data) {
                console.log("Received logout request");

                var i = 0;
                while (i < clients.length && clients[i].username != data.username) {
                    i++;
                }
                if (i < clients.length) {
                    //remove client from array of clients
                    clients.splice(i, 1);
                }

                models.User.update({username: data.username}, {$set: {isLogged: false}}, function (err, User) {
                    if (err) {
                        console.error(err);
                    } else {
                        console.log("User Successfully Logged Out");
                    }
                });
            });

            socket.on('logout_prod', function (data) {
                console.log("Received logout request");

                var i = 0;
                while (i < clients.length && clients[i].username != data.username) {
                    i++;
                }
                if (i < clients.length) {
                    //remove client from array of clients
                    clients.splice(i, 1);
                }

                models.Producer.update({username: data.username}, {$set: {isLogged: false}}, function (err, User) {
                    if (err) {
                        console.error(err);
                    } else {
                        console.log("Producer Successfully Logged Out");
                    }
                });
            });


        });
    }
};

//expose the object to be imported
exports.default = SocketController;