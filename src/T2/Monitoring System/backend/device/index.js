var app = module.exports = require('express')();
var models = require('../database/models.js');
require('./manager.js');

console.log('[Device API] Ready.');

function saveDeviceUsageHistory(state, description, date, device_name) {

    var usage = new models.Usage();

    usage.state = state;
    usage.date = date;
    usage.description = description;
    usage.device_name = device_name;

    usage.save(function (err) {
        if (err) {
            console.log("Error saving history")
        } else {
            console.log("Device Usage History saved successfully");
        }
    });
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

/* Registers a new device */
app.post('/api/device/registerDevice', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var dateAux = getDate();

    var device = new models.Device();
    device.name = req.body.name;
    device.device_type = req.body.device_type;
    device.model = req.body.model;
    device.date_registered = req.body.date_registered;
    device.producer = req.body.producer;
    device.current_state = "On";
    device.user = req.body.username;

    device.usage_history.push("(" + dateAux + ") -> Registered in the system");

    models.User.findOne({username: req.body.username}, function (err, user) {
        if (user == null) {
            console.log("User not found");
            res.status(404).send("User not found");
        } else {
            if (user.devices.indexOf(device.name) > -1) { //Check if the user as a device like that already
                console.log("User already have that device registered");
                res.status(401).send("User already have that device registered");
            } else {
                models.DeviceType.findOne({name: device.device_type}, function (err, devices) { //Check if there's that device type registered in the platform
                    if (devices == null) {//If not send error message
                        console.log("Device Type not found");
                        res.status(404).send("Device Type not found");
                    } else {
                        models.ProductModel.findOne({name: device.model}, function (err, model) { //Check if there's that device model registered in the platform
                            if (model == null) {
                                console.log("Product Model not found");
                                res.status(404).send("Product Model not found");
                            } else {
                                models.State.findOne({name: "On"}, function (err, state) { //Check if there's there's the desired state registered in the platform
                                    if (state != null) {
                                        device.save();
                                        saveDeviceUsageHistory(device.current_state, "Registered successfully", dateAux, req.body.name);
                                        user.devices.push(device.name);
                                        models.User.update({username: req.body.username}, {$set: {devices: user.devices}}, function (err) {
                                            if (!err) {
                                                console.log("Device Registered Successfully");
                                                saveUserHistory(req.body.username, "Device " + device.name + " Registered successfully");
                                                res.status(200).send("Device Registered Successfully");
                                            }
                                        });
                                    } else {
                                        console.log("State not found");
                                        res.status(404).send("State not found");
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    });
});

/* Registers a new device from the app */
app.post('/api/mobile/device/registerDevice', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var dateAux = getDate();

        var device = new models.Device();
        device.name = req.body.name;
        device.device_type = req.body.device_type;
        device.model = req.body.model;
        device.date_registered = req.body.date_registered;
        device.producer = req.body.producer;
        device.current_state = "On";
        device.user = req.body.username;

        device.usage_history.push("(" + dateAux + ") -> Registered in the system from the app");

        models.User.findOne({username: req.body.username}, function (err, user) {
            if (user == null) {
                console.log("User not found");
                res.status(404).send("User not found");
            } else {
                if (user.devices.indexOf(device.name) > -1) { ////Check if user has that device registered
                    console.log("User already have that device registered");
                    res.status(401).send("User already have that device registered");
                } else {
                    models.DeviceType.findOne({name: device.device_type}, function (err, devices) { //Check if there's that device type registered in the platform
                        if (devices == null) {//If not send error message
                            console.log("Device Type not found");
                            res.status(404).send("Device Type not found");
                        } else {
                            models.ProductModel.findOne({name: device.model}, function (err, model) { //Check if there's that device model registered in the platform
                                if (model == null) {
                                    console.log("Product Model not found");
                                    res.status(404).send("Product Model not found");
                                } else {
                                    models.State.findOne({name: "On"}, function (err, state) { //Check if there's the desired state registered in the platform
                                        if (state != null) {
                                            device.save();
                                            saveDeviceUsageHistory(device.current_state, "Registered successfully from the app", dateAux, req.body.name);
                                            user.devices.push(device.name);
                                            models.User.update({username: req.body.username}, {$set: {devices: user.devices}}, function (err, User) {
                                                if (User != null) {
                                                    console.log("Device Registered Successfully");
                                                    saveUserHistory(req.body.username, "Device " + device.name + " Registered successfully from the app");
                                                    models.User.findOne({username: req.body.username}, function (err, userF) { //Fetch the latest user info
                                                        if (userF != null) {
                                                            res.json(userF);
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            console.log("State not found");
                                            res.status(404).send("State not found");
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }
});

/* Stop a device from being monitored */
app.post('/api/device/stopMonitoringDevice', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var Device = new models.Device();
    var dateAux = getDate();

    models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device is registered in the platform
        if (deviceF != null) {
            if (deviceF.current_state == "On") {
                models.User.findOne({username: req.body.username}, function (err, user) {
                    if (user == null) {
                        console.log("User not found");
                        res.status(404).send("User not found");
                    } else {
                        models.State.findOne({name: "Unmonitored"}, function (err, state) { //Check is the desired state is registered in the platform
                            if (state != null) {
                                if (user.devices.indexOf(req.body.name) > -1) { //Check if user has that device registered
                                    deviceF.usage_history.push("(" + dateAux + ") -> Stopped monitoring");
                                    models.Device.update({name: req.body.name}, {
                                        $set: {
                                            current_state: "Unmonitored",
                                            usage_history: deviceF.usage_history
                                        }
                                    }, function (err) {
                                        if (err) {
                                            res.status(401).send("Error");
                                        } else {
                                            console.log("Device Updated");
                                            saveDeviceUsageHistory("Unmonitored", "Stopped Monitoring", dateAux, req.body.name);
                                            saveUserHistory(req.body.username, "Stopped monitoring device ( " + req.body.name + " )");
                                            res.status(200).send("Device Updated")
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            } else {
                console.log("Device wasn't on");
                res.status(401).send("Device wasn't on");
            }
        } else {
            console.log("Device not found");
            res.status(404).send("Device Not Found");
        }
    });
});

/* Stop a device from being monitored from the app*/
app.post('/api/mobile/device/stopMonitoringDevice', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var dateAux = getDate();

        models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device is registered in the platform
            if (deviceF != null) {
                if (deviceF.current_state == "On") {
                    models.User.findOne({username: req.body.username}, function (err, user) {
                        if (user == null) {
                            console.log("User not found");
                            res.status(404).send("User Not Found");
                        } else {
                            models.State.findOne({name: "Unmonitored"}, function (err, state) { //CHeck if the desired state exists
                                if (state != null) {
                                    if (user.devices.indexOf(req.body.name) > -1) { //Check if user ha the device registered
                                        deviceF.usage_history.push("(" + dateAux + ") -> Stopped monitoring from the app");
                                        models.Device.update({name: req.body.name}, {
                                            $set: {
                                                current_state: "Unmonitored",
                                                usage_history: deviceF.usage_history
                                            }
                                        }, function (err) {
                                            if (err) {
                                                res.status(401).send("Error");
                                            } else {
                                                console.log("Device Updated");
                                                saveDeviceUsageHistory("Unmonitored", "Stopped Monitoring from the app", dateAux, req.body.name);
                                                saveUserHistory(req.body.username, "Stopped monitoring device ( " + req.body.name + " ) from the app");
                                                models.User.findOne({username: req.body.username}, function (err, userF) { //Fetch user's last info
                                                    if (userF != null) {
                                                        res.json(userF);
                                                    }
                                                });

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                } else {
                    console.log("Device wasn't on");
                    res.status(401).send("Device wasn't on");
                }
            } else {
                console.log("Device not found");
                res.status(404).send("Device Not Found");
            }
        });
    }
});

/* Start monitoring a device again */
app.post('/api/device/reMonitorDevice', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var dateAux = getDate();

    models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device exists in the platform
        if (deviceF != null) {
            if (deviceF.current_state == "Unmonitored" || deviceF.current_state == "Off") {
                models.User.findOne({username: req.body.username}, function (err, user) {
                    if (user == null) {
                        console.log("User not found");
                        res.status(404).send("User not found");
                    } else {
                        models.State.findOne({name: "On"}, function (err, state) { //Check if the desired state exists in the platform
                            if (state != null) {
                                if (user.devices.indexOf(req.body.name) > -1) { //Check if user has the device
                                    deviceF.usage_history.push("(" + dateAux + ") -> Started monitoring");
                                    models.Device.update({name: req.body.name}, {
                                        $set: {
                                            current_state: "On",
                                            usage_history: deviceF.usage_history
                                        }
                                    }, function (err, device) {
                                        if (err) {
                                            res.status(401).send("Error");
                                        } else {
                                            console.log("Device Updated");
                                            saveDeviceUsageHistory("On", "Started Monitoring", dateAux, req.body.name);
                                            saveUserHistory(req.body.username, "Started monitoring device ( " + req.body.name + " ) again");
                                            res.status(200).send("Started Monitoring device successfully");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
            else {
                console.log("Device Was On or Off but not Unmonitored");
                res.status(401).send("Device Was On or Off but not Unmonitored");
            }
        } else {
            console.log("Device not found");
            res.status(404).send("Device Not Found");
        }
    });
});

/* Start monitoring a device again from the app */
app.post('/api/mobile/device/reMonitorDevice', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var dateAux = getDate();

    models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device exists in the platform
        if (deviceF != null) {
            if (deviceF.current_state == "Unmonitored" || deviceF.current_state == "Off") { //Check if it's unmonitored
                models.User.findOne({username: req.body.username}, function (err, user) {
                    if (user == null) {
                        console.log("User not found");
                        res.status(404).send("User Not Found");
                    } else {
                        models.State.findOne({name: "On"}, function (err, state) { //Check if the state exists
                            if (state != null) {
                                if (user.devices.indexOf(req.body.name) > -1) { //Check if user has the device registered
                                    deviceF.usage_history.push("(" + dateAux + ") -> Started monitoring from the app");
                                    models.Device.update({name: req.body.name}, {
                                        $set: {
                                            current_state: "On",
                                            usage_history: deviceF.usage_history
                                        }
                                    }, function (err, device) {
                                        if (device == null) {
                                            res.status(401).send("Error");
                                        } else {
                                            console.log("Device Updated");
                                            saveDeviceUsageHistory("On", "Started Monitoring from the app", dateAux, req.body.name);
                                            saveUserHistory(req.body.username, "Started monitoring device ( " + req.body.name + " ) again, from the app");
                                            models.User.findOne({username: req.body.username}, function (err, userF) { //Fetch user's last info
                                                if (userF != null) {
                                                    res.json(userF);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            } else {
                console.log("Device Was On or Off but not Unmonitored");
                res.status(401).send("Device Was On or Off but not Unmonitored");
            }
        } else {
            console.log("Device not found");
            res.status(404).send("Device Not Found");
        }
    });
});

/* UnRegisters a device */
app.post('/api/device/unRegisterDevice', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var dateAux = getDate();

    models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device exists in the platform
        if (deviceF != null) {
            if (deviceF.current_state != "Off") {
                models.User.findOne({username: req.body.username}, function (err, user) {
                    if (user == null) {
                        console.log("User not found");
                        res.status(404).send("User not found");
                    } else {
                        models.State.findOne({name: "Off"}, function (err, state) { //Check if the desired state exists in the platform
                            if (state != null) {
                                if (user.devices.indexOf(req.body.name) > -1) { //Check if user has the device
                                    deviceF.usage_history.push("(" + dateAux + ") -> Unregistered");
                                    models.Device.update({name: req.body.name}, {
                                        $set: {
                                            current_state: "Off",
                                            usage_history: deviceF.usage_history
                                        }
                                    }, function (err, device) {
                                        if (err) {
                                            res.status(401).send("Error");
                                        } else {
                                            console.log("Device Unregistered Successfully");
                                            saveDeviceUsageHistory("Off", "Unregistered", dateAux, req.body.name);
                                            saveUserHistory(req.body.username, "Unregistered device ( " + req.body.name + " )");
                                            res.status(200).send("Unregistered successfully");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
            else {
                console.log("Device Was Already Off");
                res.status(401).send("Device Was Already Off");
            }
        } else {
            console.log("Device not found");
            res.status(404).send("Device Not Found");
        }
    });
});

/* UnRegisters a device from the app*/
app.post('/api/mobile/device/unRegisterDevice', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var dateAux = getDate();

        models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device exists in the platform
            if (deviceF != null) {
                if (deviceF.current_state != "Off") {
                    models.User.findOne({username: req.body.username}, function (err, user) {
                        if (user == null) {
                            console.log("User not found");
                            res.status(404).send("User Not Found");
                        } else {
                            models.State.findOne({name: "Off"}, function (err, state) { //Check if the desired state exists in the platform
                                if (state != null) {
                                    if (user.devices.indexOf(req.body.name) > -1) { //Check if user has the device
                                        deviceF.usage_history.push("(" + dateAux + ") -> Unregistered from the app");
                                        models.Device.update({name: req.body.name}, {
                                            $set: {
                                                current_state: "Off",
                                                usage_history: deviceF.usage_history
                                            }
                                        }, function (err, device) {
                                            if (err) {
                                                res.status(401).send("Error");
                                            } else {
                                                console.log("Device Unregistered Successfully from the app");
                                                saveDeviceUsageHistory("Off", "Unregistered from the app", dateAux, req.body.name);
                                                saveUserHistory(req.body.username, "Unregistered device ( " + req.body.name + " ) from the app");
                                                models.User.findOne({username: user.username}, function (err, userU) {
                                                    if (userU != null) {
                                                        res.json(userU);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
                else {
                    console.log("Device Was Already Off");
                    res.status(401).send("Device Was Already Off");
                }
            } else {
                console.log("Device Not Found");
                res.status(404).send("Device Not Found");
            }
        });
    }
});

/* Remove a device */
app.post('/api/device/removeDevice', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var dateAux = getDate();

    models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device exists
        if (deviceF != null) {
            models.User.findOne({username: req.body.username}, function (err, userF) {
                if (userF != null) {
                    if (userF.devices.indexOf(req.body.name) != -1) { //Check if the user has that device
                        //If user has that device remove it
                        deviceF.remove();
                        userF.devices.splice(userF.devices.indexOf(req.body.name), 1); //Check if user has that device
                        models.User.update({username: req.body.username}, {$set: {devices: userF.devices}}, function (err, user) {
                            if (err) {
                                res.status(401).send("Error");
                            } else {
                                console.log("Device Removed Successfully");
                                saveUserHistory(req.body.username, "Removed device ( " + req.body.name + " )");
                                saveDeviceUsageHistory("Off", "Removed successfully", dateAux, req.body.name);
                                res.status(200).send("Device Removed Successfully");
                            }
                        });
                    } else {
                        console.log("User Doesn't Have That Device");
                        res.status(404).send("User Doesn't Have That Device");
                    }
                }
            });
        } else {
            console.log("Device Not Found");
            res.status(404).send("Device Not Found");
        }
    });
});

/* Remove a device from the app */
app.post('/api/mobile/device/removeDevice', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var dateAux = getDate();

        models.Device.findOne({name: req.body.name}, function (err, deviceF) { //Check if the device exists
            if (deviceF != null) {
                models.User.findOne({username: req.body.username}, function (err, userF) {
                    if (userF != null) {
                        if (userF.devices.indexOf(req.body.name) != -1) { //Check if the user has that device
                            //If user has that device remove it
                            deviceF.remove();
                            userF.devices.splice(userF.devices.indexOf(req.body.name), 1); //Check if user has that device
                            models.User.update({username: req.body.username}, {$set: {devices: userF.devices}}, function (err, user) {
                                if (err) {
                                    res.status(401).send("Error");
                                } else {
                                    console.log("Device Removed Successfully");
                                    saveUserHistory(req.body.username, "Removed device ( " + req.body.name + " )");
                                    saveDeviceUsageHistory("Off", "Removed successfully", dateAux, req.body.name);
                                    models.User.findOne({username: req.body.username}, function (err, userF) {
                                        if (userF != null) {
                                            res.json(userF);
                                        }
                                    });
                                }
                            });
                        } else {
                            console.log("User Doesn't Have That Device");
                            res.status(404).send("User Doesn't Have That Device");
                        }
                    }
                });
            } else {
                console.log("Device Not Found");
                res.status(404).send("Device Not Found");
            }
        });
    }
});

/* Consult device info */
app.post('/api/mobile/device/deviceInfo', function (req, res) {
    if (req.body.platform != undefined) {
        models.User.findOne({username: req.body.username}, function (err, user) { //Find user
            if (user != null) {
                if (user.devices.indexOf(req.body.name) > -1) { //Check if he has that device
                    models.Device.findOne({name: req.body.name}, function (err, device) { //Check if the device exists
                        if (device != null) {
                            saveUserHistory(req.body.username, "Checked device ( " + req.body.name + " ) info from the app");
                            res.json(device); //Send device info
                        } else {
                            res.status(401).send("Device Not Found");
                        }
                    });
                } else {
                    res.status(401).send("User Does Not Have That Device");
                }
            } else {
                res.status(401).send("User Not Found");
            }
        });
    }
});