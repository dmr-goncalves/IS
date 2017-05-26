var app = module.exports = require('express')();
var models = require('../database/models.js');
var _secret = require('../index.js');

var jwt = require('jsonwebtoken');

console.log('[User API] Ready.');

function getDate() {
    var dateAux = new Date();
    var dd = dateAux.getDate();
    var mm = dateAux.getMonth() + 1; //January is 0!

    var yyyy = dateAux.getFullYear();

    var hour = dateAux.getHours();

    var min = dateAux.getMinutes();

    var sec = dateAux.getSeconds();

    var day = dateAux.getDay();

    switch (day) {
        case 0:
            day = "Sun"
            break;
        case 1:
            day = "Mon";
            break;
        case 2:
            day = "Tue";
            break;
        case 3:
            day = "Wed";
            break;
        case 4:
            day = "Thu";
            break;
        case 5:
            day = "Fri";
            break;
        case 6:
            day = "Sat";
            break;
    }
    if (dd < 10) {
        dd = '0' + dd;
    }
    if (mm < 10) {
        mm = '0' + mm;
    }
    return day + " " + dd + '/' + mm + '/' + yyyy + " " + hour + "h" + min + "m" + sec + "s";
}

function enc(s) {
    var enc = "";
    var str = "";
    // make sure that input is string
    str = s.toString();
    for (var i = 0; i < s.length; i++) {
        // create block
        var a = s.charCodeAt(i);
        // bitwise XOR
        var b = a ^ 123;
        enc = enc + String.fromCharCode(b);
    }
    return enc;
}

function saveUserHistory(username, description) {

    var userH = new models.UserHistory();
    userH.username = username;
    userH.date = getDate();
    userH.description = description;

    userH.save(function (err) {
        if (err) {
            console.log("Error saving history")
        } else {
            console.log("User History saved successfully");
        }
    });
}

/* Registers a new user */
app.post('/api/user/register', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var user = new models.User();
    user.username = req.body.username.toString();
    user.name = req.body.name.toString();
    user.email = req.body.email.toString();
    user.password = req.body.password.toString();
    user.date_registered = req.body.date_registered;
    user.isLogged = false;

    models.User.findOne({username: user.username}, function (err, users) {
        if (users == null) { //We can save it because it's not registered yet
            user.save(function (err) {
                if (err) {
                    console.error("Error Registering: ", err);
                    res.status(401).send("Error Registering");
                } else {
                    console.log("User Registered");
                    saveUserHistory(user.username, "Registered in the system");
                    res.status(200).send("User Registered Successfully");
                }
            });
        } else {//We already have an user with this username, register another
            console.log("Cannot register because we already have one user registered with that username");
            res.status(402).send("User Already Exists");
        }
    });
});

/* Registers a new user from the app*/
app.post('/api/mobile/user/register', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var user = new models.User();

        user.username = req.body.username;
        user.name = req.body.name;
        user.email = req.body.email;
        user.password = req.body.password;
        user.date_registered = req.body.date_registered;
        user.isLogged = false;

        models.User.findOne({username: user.username}, function (err, users) {
            if (users == null) { //We can save it because it's not registered yet
                user.save(function (err) {
                    if (err) {
                        console.error("Error Registering: ", err);
                        res.status(401).send("Error Registering");
                    } else {
                        console.log("User Registered");
                        saveUserHistory(user.username, "Registered in the system from the app");
                        res.status(200).send("User Registered Successfully");
                    }
                });
            } else {//We already have an user with this username, register another
                console.log("Cannot register because we already have one user registered with that username");
                res.status(402).send("User Already Exists");
            }
        });
    }
});

/* Login user */
app.post('/api/user/login', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var user = new models.User();

    user.username = req.body.username.toString();
    user.password = req.body.password.toString();

    models.User.findOne({$and: [{username: user.username}, {password: user.password}]}, function (err, userL) {
        if (userL == null) {
            console.log("User to login not found or password is wrong");
            res.status(404).send('Wrong User or Password');
        } else {
            if (userL.isLogged) {
                console.log("User is already logged in");
                res.status(400).send("User Was Already Logged In");
            } else {
                models.User.update({username: user.username}, {$set: {isLogged: true}}, function (err) {
                    if (!err) {
                        models.User.findOne({username: user.username}, function (err, userF2) { //We fetch the updated user info
                            if (userF2 != null) {
                                console.log("User was successfully logged in. ");
                                saveUserHistory(user.username, "Logged in to the system");
                                var token = jwt.sign(userF2, _secret._secret, {expiresIn: 3600 * 5});
                                res.json({token: token});
                            }
                        });
                    }
                });
            }
        }
    });
});

/* Login user from the app*/
app.post('/api/mobile/user/login', function (req, res) {
    if (req.body.platform != undefined) {

        res.header("Access-Control-Allow-Origin", "*");
        var user = new models.User();

        user.username = req.body.username;
        user.password = req.body.password;

        models.User.findOne({$and: [{username: user.username}, {password: user.password}]}, function (err, userL) {
            if (userL == null) {
                console.log("User to login not found or password is wrong");
                res.status(404).send('Wrong User or Password');
            } else {
                if (userL.isLogged) {
                    console.log("User is already logged in");
                    res.status(400).send("User Was Already Logged In");
                } else {
                    models.User.update({username: user.username}, {$set: {isLogged: true}}, function (err) {
                        if (!err) {
                            models.User.findOne({username: user.username}, function (err, userF2) { //We fetch the updated user info
                                if (userF2 != null) {
                                    console.log("User was successfully logged in");
                                    saveUserHistory(user.username, "Logged in to the system from the app");
                                    res.json(userF2);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
});

/* Logoff user */
app.post('/api/user/logout', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    models.User.findOne({username: req.body.username}, function (err, user) {
        if (user != null) {
            if (user.isLogged) {
                models.User.update({username: req.body.username}, {$set: {isLogged: false}}, function (err) {
                    if (!err) {
                        console.log("User logout successfully");
                        saveUserHistory(req.body.username, "Logged off from the system");
                        res.status(200).send("User Was Successfully Logged Off");
                    }
                });
            } else {
                console.log("User was not logged in. ");
                res.status(401).send("User Was Not Logged In");
            }
        }
    });
});

/* Logoff user from the app*/
app.post('/api/mobile/user/logout', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        models.User.findOne({username: req.body.username}, function (err, user) {
            if (user != null) {
                if (user.isLogged) {
                    models.User.update({username: req.body.username}, {$set: {isLogged: false}}, function (err) {
                        if (!err) {
                            console.log("User logout successfully");
                            saveUserHistory(req.body.username, "Logged off from the system from the app");
                            res.status(200).send("User Was Successfully Logged Off");
                        }
                    });
                } else {
                    console.log("User was not logged in. ");
                    res.status(401).send("User Was Not Logged In");
                }
            }
        });
    }
});

/* Consults user's devices history */
app.post('/api/user/devices', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    models.User.findOne({username: req.body.username}, function (err, user) {
        if (user != null) {
            var historyName = [];
            var historyReadings = [];

            for (var i = 0; i < user.devices.length; i++) {
                models.Device.findOne({name: user.devices[i]}, function (err, device) {
                    if (device != null) {
                        for (var z = 0; z < device.readings.length; z++) {
                            historyName.push(device.name);
                            historyReadings.push(device.readings[z]);
                        }
                    }
                });
            }

            var history = { //Build an object with the arrays
                historyName: historyName,
                historyReadings: historyReadings
            };

            saveUserHistory(req.body.username, "Consulted Devices History");

            setTimeout(function () {
                res.send(history); //Send the object with the final history
            }, 50);

        }
    });
});

/* Consults user's devices history from the app */
app.post('/api/mobile/user/devices', function (req, res) {
    if (req.body.undefined != undefined) {
        models.User.findOne({username: req.body.username}, function (err, user) {
            if (user != null) {
                var history = [];

                for (var i = 0; i < user.devices.length; i++) {
                    models.Device.findOne({name: user.devices[i]}, function (err, device) {
                        if (device != null) {
                            for (var z = 0; z < device.readings.length; z++) {
                                history.push(device.name + " - >" + device.readings[z]);
                            }
                        }
                    });
                }

                saveUserHistory(req.body.username, "Consulted Devices History");

                setTimeout(function () {
                    res.send(history); //Send the object with the final history
                }, 50);

            }
        });
    }
});

/* Consults user's history */
app.post('/api/user/history', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var historyDate = [];
    var historyDescription = [];

    models.UserHistory.find({username: req.body.username}, function (err, user) { //Get all history
        if (user != null) {
            for (var i = 0; i < user.length; i++) { //Build two array with the data
                historyDate.push(user[i].date);
                historyDescription.push(user[i].description);
            }

            var history = { //Build an object with the arrays
                historyDate: historyDate,
                historyDescription: historyDescription
            };

            res.send(history); //Send the object with the final history
        }
    });
});

/* Consults user's history from the app */
app.post('/api/mobile/user/history', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");
        var history = [];

        models.UserHistory.find({username: req.body.username}, function (err, user) { //Get all history
            if (user != null) {
                for (var i = 0; i < user.length; i++) {
                    history.push("[" + user[i].date + "] -> " + user[i].description); //Build an array with all info
                }
                res.send(history);// Send the final array
            }
        });
    }
});

/* Consult user info */
app.post('/api/mobile/user/userInfo', function (req, res) {
    if (req.body.platform != undefined) {
        models.User.findOne({username: req.body.username}, function (err, user) { //Get user info
            if (user != null) {
                res.json(user); //Send user data
            }
        });
    }
});

/* Get Facebook Register/Login */
app.post('/api/user/fbRegLog', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    models.User.findOne({username: req.body.username}, function (err, user) {
        if (user == null) { //We register the user
            var user = new models.User();
            user.username = req.body.username.toString();
            user.name = req.body.name.toString();
            user.email = req.body.email.toString();
            user.password = req.body.password.toString();
            user.date_registered = req.body.date_registered;
            user.isLogged = false;

            user.save(function (err) {
                if (err) {
                    console.error("Error Registering: ", err);
                    res.status(401).send("Error Registering");
                } else {
                    console.log("User Registered");
                    saveUserHistory(user.username, "Registered in the system");
                    models.User.findOne({$and: [{username: user.username}, {password: user.password}]}, function (err, userL) {
                        if (userL == null) {
                            console.log("User to login not found or password is wrong");
                            res.status(404).send('Wrong User or Password');
                        } else {
                            if (user.isLogged) {
                                console.log("User is already logged in");
                                res.status(400).send("User Was Already Logged In");
                            } else {
                                console.log("User was successfully logged in. ");
                                saveUserHistory(user.username, "Logged in to the system");
                                res.status(200).send("User was successfully logged in.");
                            }
                        }
                    });
                }
            });
        } else { //We'll see if he's already logged in
            models.User.findOne({$and: [{username: user.username}, {password: user.password}]}, function (err, userL) {
                if (userL == null) {
                    console.log("User to login not found or password is wrong");
                    res.status(404).send('Wrong User or Password');
                } else {
                    if (user.isLogged) {
                        console.log("User is already logged in");
                        res.status(400).send("User Was Already Logged In");
                    } else {
                        console.log("User was successfully logged in.");
                        saveUserHistory(user.username, "Logged in to the system");
                        res.status(200).send("User was successfully logged in.");
                    }
                }
            });
        }
    });

});