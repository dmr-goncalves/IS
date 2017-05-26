var app = module.exports = require('express')();
var models = require('../database/models.js');

var _secret = require('../index.js');

var jwt = require('jsonwebtoken');

console.log('[Producer API] Ready.');

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
function url_base64_decode(str) {
    var output = str.replace('-', '+').replace('_', '/');
    switch (output.length % 4) {
        case 0:
            break;
        case 2:
            output += '==';
            break;
        case 3:
            output += '=';
            break;
        default:
            throw 'Illegal base64url string!';
    }
    return window.atob(output);
}
/* Registers a new producer */
app.post('/api/producer/register', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var producer = new models.Producer();

    producer.username = req.body.username.toString();
    producer.name = req.body.name.toString();
    producer.email = req.body.email.toString();
    producer.password = req.body.password.toString();
    producer.date_registered = req.body.date_registered;
    producer.isLogged = false;

    models.Producer.findOne({username: producer.username}, function (err, producers) {
        if (producers == null) { //We can save it because it's not registered yet
            producer.save(function (err) {
                if (err) {
                    console.error("Error registering: ", err);
                    res.status(401).send("Error registering");
                } else {
                    console.log("Producer Registered");
                    res.status(200).send("Producer Registered");
                }
            });
        } else {
            //We already have an user with this username, register another
            console.log("Cannot register because we already have one producer registered with that username");
            res.status(403).send("Producer Already Exists");
        }
    });
});

/* Registers a new producer from the app */
app.post('/api/mobile/producer/register', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var producer = new models.Producer();

        producer.username = req.body.username;
        producer.name = req.body.name;
        producer.email = req.body.email;
        producer.password = req.body.password;
        producer.date_registered = req.body.date_registered;
        producer.isLogged = false;


        models.Producer.findOne({username: producer.username}, function (err, producers) {
            if (producers == null) { //We can save it because it's not registered yet
                producer.save(function (err) {
                    if (err) {
                        console.error("Error registering: ", err);
                        res.status(401).send("Error Registering");
                    } else {
                        console.log("Producer Registered Successfully");
                        res.status(200).send("Producer Registered Successfully");
                    }
                });
            } else {
                //We already have an user with this username, register another
                console.log("Cannot register because we already have one producer registered with that username");
                res.status(403).send("Producer Already Exists");
            }
        });
    }
});

/* Login a producer */
app.post('/api/producer/login', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var producer = new models.Producer();

    producer.username = req.body.username.toString();
    producer.password = req.body.password.toString();

    models.Producer.findOne({$and: [{username: producer.username}, {password: producer.password}]}, function (err, producerL) { //Check if the producer exists
        if (producerL == null) {
            console.log("Producer to login not found or password is wrong");
            res.status(404).send("Producer Not Found Or Wrong Password");
        } else {
            if (producerL.isLogged) { //Check if the producer is already logged in
                console.log("Producer is already logged in");
                res.status(401).send("Producer Was Already Logged In");
            } else {
                models.Producer.update({username: producer.username}, {$set: {isLogged: true}}, function (err, producerF) { //Update producer because he's successfully logged in
                    if (producerF != null) {
                        models.Producer.findOne({username: req.body.username}, function (err2, producerF2) { //Fetch producer's latest info
                            if (producerF2 != null) {
                                console.log("Producer was successfully logged in.");
                                var token = jwt.sign(producerF2, _secret._secret, {expiresIn: 3600 * 5});
                                res.json({token: token});
                            }
                        });
                    }
                });
            }
        }
    });
});

/* Login a producer from the app */
app.post('/api/mobile/producer/login', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var producer = new models.Producer();

        producer.username = req.body.username;
        producer.password = req.body.password;

        models.Producer.findOne({$and: [{username: producer.username}, {password: producer.password}]}, function (err, producerL) {  //Check if producer exists
            if (producerL == null) {
                console.log("Producer to login not found or password is wrong");
                res.status(404).send("Producer Not Found Or Wrong Password");
            } else {
                if (producerL.isLogged) { //Check if he's already logged in
                    console.log("Producer is already logged in");
                    res.status(401).send("Producer Was Already Logged In");
                } else {
                    models.Producer.update({username: producer.username}, {$set: {isLogged: true}}, function (err, producerF) { //Update producer's info
                        if (producerF != null) {
                            models.Producer.findOne({username: req.body.username}, function (err2, producerF2) { //Fetch producer's latest info
                                if (producerF2 != null) {
                                    console.log("Producer was successfully logged in. ");
                                    res.json(producerF2);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
});

/* Logout a producer */
app.post('/api/producer/logout', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");
    models.Producer.findOne({username: req.body.username}, function (err, producer) { //Check if producer exists
        if (producer != null) {
            if (producer.isLogged) { //Check if producer is logged in
                models.Producer.update({username: req.body.username}, {$set: {isLogged: false}}, function (err, producer2) { //Update his info
                    if (producer2 != null) {
                        console.log("Producer Successfully Logged Out");
                        res.status(200).send("Producer Successfully Logged Out");
                    }
                });
            }
            else {
                console.log("Producer was not logged in. ");
                res.status(401).send("Producer Was Not Logged In");
            }
        }
    });
});

/* Logout a producer from the app */
app.post('/api/mobile/producer/logout', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");
        models.Producer.findOne({username: req.body.username}, function (err, producer) { //Check if producer exists
            if (producer != null) {
                if (producer.isLogged) { //Check if producer is logged in
                    models.Producer.update({username: req.body.username}, {$set: {isLogged: false}}, function (err, producer2) { //Fetch producer's latest info
                        if (producer2 != null) {
                            console.log("Producer Successfully Logged Off");
                            res.status(200).send("Producer Successfully Logged Off");
                        }
                    });
                }
                else {
                    console.log("Producer was not logged in. ");
                    res.status(401).send("Producer Was Not Logged In");
                }
            }
        });
    }
});

/* Adds a new product model */
app.post('/api/producer/productmodel/add', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var productModel = new models.ProductModel();

    productModel.name = req.body.name;
    productModel.id_product_models = req.body.id_product_models;

    models.ProductModel.findOne(
        {
            $and: [{
                name: req.body.name
            },
                {id_product_models: req.body.id_product_models}]
        }, function (err, productModelFound) {
            if (productModelFound == null) { //We can save it because it's not registered yet
                productModel.save(function (err) {
                    if (err) {
                        console.error("Error while saving product model", err);
                        res.status(401).send("Error");
                    } else {//Now we update producer's device models
                        models.Producer.findOne({username: req.body.username}, function (err, producer) {
                            if (producer != null) {
                                producer.device_models.push(productModel.name); //Add device model to list
                                models.Producer.update({username: req.body.username}, {$set: {device_models: producer.device_models}}, function (err) {
                                    if (!err) {
                                        console.log("Product Model Registered");
                                        res.status(200).send("ProductModelRegistered");
                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                //We already have a product model with this id, register another
                res.status(402).send("Product Model Already Exists");
                console.log("Cannot register because we already have one product model registered with that id");
            }
        });
});

/* Adds a new product model from the app */
app.post('/api/mobile/producer/productmodel/add', function (req, res) {
    if (req.body.platform) {
        res.header("Access-Control-Allow-Origin", "*");

        var productModel = new models.ProductModel();

        productModel.name = req.body.name;
        productModel.id_product_models = req.body.id_product_models;

        models.ProductModel.findOne(
            {
                $and: [{name: req.body.name},
                    {id_product_models: req.body.id_product_models}]
            }, function (err, productModelFound) {
                if (productModelFound == null) { //We can save it because it's not registered yet
                    productModel.save(function (err) {
                        if (!err) { //Now we update producer's device models
                            models.Producer.findOne({username: req.body.username}, function (err, producer) {
                                if (producer != null) {
                                    producer.device_models.push(productModel.name); //Add device model to list
                                    models.Producer.update({username: req.body.username}, {$set: {device_models: producer.device_models}}, function (err, producerU) {
                                        if (producerU != null) {
                                            console.log("Product Model Registered");
                                            models.Producer.findOne({username: req.body.username}, function (err, prod) {
                                                if (prod != null) {
                                                    res.json(prod);
                                                } else {
                                                    res.status(401).send("Producer Not Found");
                                                }
                                            });
                                        } else {
                                            res.status(401).send("Failed To Register Product Model")
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {//We already have a product model with this id, register another
                    res.status(402).send("Product Model Already Exists");
                    console.log("Cannot register because we already have one product model registered with that id");
                }
            });
    }
});

/* Removes a product model */
app.post('/api/producer/productmodel/remove', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    models.ProductModel.findOne({
        $and: [{
            name: req.body.name
        },
            {id_product_models: req.body.id_product_models}]
    }, function (err, productModel) {
        if (productModel == null) {
            //We can't remove it because it doesn't exist
            console.log("Cannot remove because ProductModel doesn't exist");
            res.status(401).send("Product Model Not Found");
        } else {
            productModel.remove(function (err) {
                if (err) {
                    console.error("Error while removing product model: ", err);
                    res.status(402).send("Error while removing product model:");
                } else {
                    models.Producer.findOne({username: req.body.username}, function (err, producer) { //Fetch producer's info
                        if (producer != null) {
                            var index = producer.device_models.indexOf(req.body.name);
                            producer.device_models.splice(index, 1);
                            models.Producer.update({username: req.body.username}, {$set: {device_models: producer.device_models}}, function (err) { //Update his info
                                console.log("ProductModel Removed");
                                res.status(200).send("Product Model Removed");
                            });
                        }
                    });
                }
            });
        }
    });
});

/* Removes a product model from the app */
app.post('/api/mobile/producer/productmodel/remove', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        models.ProductModel.findOne(
            {
                $and: [{
                    name: req.body.name
                },
                    {id_product_models: req.body.id_product_models}]
            }, function (err, productModel) {
                if (productModel == null) {
                    //We can't remove it because it doesn't exist
                    console.log("Cannot remove because ProductModel doesn't exist");
                    res.status(401).send("Product Model Not Found");
                } else {
                    productModel.remove(function (err) {
                        if (!err) {
                            models.Producer.findOne({username: req.body.username}, function (err, producer) { //Fetch producer's info
                                if (producer != null) {
                                    var index = producer.device_models.indexOf(req.body.name);
                                    producer.device_models.splice(index, 1); //Remove product model from producer's list
                                    models.Producer.update({username: req.body.username}, {$set: {device_models: producer.device_models}}, function (err, producerU) { //Update his info
                                        if (producerU != null) {
                                            console.log("ProductModel Removed");
                                            models.Producer.findOne({username: req.body.username}, function (err, prod) { //Fetch latest info to send to mobile
                                                if (prod != null) {
                                                    res.json(prod);
                                                } else {
                                                    res.status(401).send("Producer Not Found");
                                                }
                                            });
                                        } else {
                                            res.status(401).send("Failed To Remove Product Model");
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
    }
});

/* Adds a new device type */
app.post('/api/producer/devicetype/add', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var deviceType = new models.DeviceType();

    deviceType.name = req.body.name;
    deviceType.id_device_type = req.body.id_device_type;

    models.DeviceType.findOne({
        $and: [{
            name: deviceType.name
        },
            {id_device_type: deviceType.id_device_type}]
    }, function (err, DeviceTypes) { //Check if device type exist
        if (DeviceTypes == null) {
            //We can save it because it's not registered yet
            deviceType.save(function (err) {
                if (err) {
                    console.error("Error saving device type: ", err);
                    res.status(401).send("Error");
                } else {
                    deviceType.save(function (err) {
                        if (err) {
                            console.error("Error while saving device type", err);
                            res.status(401).send("Error");
                        } else {//Now we update producer's device models
                            models.Producer.findOne({username: req.body.username}, function (err, producer) { //Get producer object
                                if (producer != null) {
                                    producer.device_types.push(deviceType.name); //Update producer's device type list
                                    models.Producer.update({username: req.body.username}, {$set: {device_types: producer.device_types}}, function (err) { //Update db
                                        if (!err) {
                                            console.log("Device Type Registered");
                                            res.status(200).send("Device Type Registered");
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        } else {
            //We already have a device type with this id, register another
            res.status(402).send("Device Type Already Exists");
            console.log("Cannot register because we already have one device type registered with that id");
        }
    });
});

/* Adds a new device type from the app */
app.post('/api/mobile/producer/devicetype/add', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var deviceType = new models.DeviceType();

        deviceType.name = req.body.name;
        deviceType.id_device_type = req.body.id_device_type;

        models.DeviceType.findOne({
            $and: [{
                name: deviceType.name
            },
                {id_device_type: deviceType.id_device_type}]
        }, function (err, DeviceTypes) { //Check if device type exists
            if (DeviceTypes == null) {
                //We can save it because it's not registered yet
                deviceType.save(function (err) {
                    if (err) {
                        console.error("Error saving device type: ", err);
                        res.status(401).send("Error");
                    } else {
                        deviceType.save(function (err) {
                            if (err) {
                                console.error("Error while saving device type", err);
                                res.status(401).send("Error");
                            } else {//Now we update producer's device models
                                models.Producer.findOne({username: req.body.username}, function (err, producer) { //Get producer object
                                    if (producer != null) {
                                        producer.device_types.push(deviceType.name); //Add device type to the list
                                        models.Producer.update({username: req.body.username}, {$set: {device_types: producer.device_types}}, function (err, producerU) { //Update producer's info
                                            if (producerU != null) {
                                                console.log("Device Type Registered");
                                                models.Producer.findOne({username: req.body.username}, function (err, prod) { //Fetch latest info to send to the mobile
                                                    if (prod != null) {
                                                        res.json(prod);
                                                    } else {
                                                        res.status(401).send("Producer Not Found");
                                                    }
                                                });
                                            } else {
                                                res.status(401).send("Failed To Register Device Type")
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            } else {//We already have a device type with this id, register another
                res.status(402).send("Device Type Already Exists");
                console.log("Cannot register because we already have one device type registered with that id");
            }
        });
    }
});

/* Removes a device type */
app.post('/api/producer/devicetype/remove', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    models.DeviceType.findOne({
        $and: [
            {
                name: req.body.name
            },
            {id_device_type: req.body.id_device_type}]
    }, function (err, deviceTypes) { //Check if device type exists
        if (deviceTypes == null) {
            //We can't remove it because it doesn't exist
            console.log("Cannot remove because it doesn't exist");
            res.status(404).send("Device Type Not Found");
        } else {
            deviceTypes.remove(function (err) {
                if (err) {
                    console.error("Error while removing device type: ", err);
                    res.status(401).send("Error while removing device type");
                } else {
                    models.Producer.findOne({username: req.body.username}, function (err, producer) { //Get producer object
                        if (producer != null) {
                            var index = producer.device_types.indexOf(req.body.name);
                            producer.device_types.splice(index, 1); //Remove product model from the list
                            models.Producer.update({username: req.body.username}, {$set: {device_types: producer.device_types}}, function (err) { //Update his info
                                console.log("Device Type Removed");
                                res.status(200).send("Device Type Removed");
                            });
                        }
                    });
                }
            });
        }
    });
});

/* Removes a device type from the app */
app.post('/api/mobile/producer/devicetype/remove', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        models.DeviceType.findOne({
            $and: [{
                name: req.body.name
            },
                {id_device_type: req.body.id_device_type}]
        }, function (err, deviceTypes) { //Check if device type exists
            if (deviceTypes == null) {
                //We can't remove it because it doesn't exist
                console.log("Cannot remove because it doesn't exist");
                res.status(404).send("Device Type Not Found");
            } else {
                deviceTypes.remove(function (err) {
                    if (err) {
                        console.error("Error while removing device type: ", err);
                        res.status(401).send("Error While Removing Device Type");
                    } else {
                        models.Producer.findOne({username: req.body.username}, function (err, producer) { //Get producer object
                            if (producer != null) {
                                var index = producer.device_types.indexOf(req.body.name);
                                producer.device_types.splice(index, 1); //Remove device type from the list
                                models.Producer.update({username: req.body.username}, {$set: {device_types: producer.device_types}}, function (err, producerU) { //Update producer's info
                                        if (producerU != null) {
                                            console.log("Device Type Removed");
                                            models.Producer.findOne({username: req.body.username}, function (err, prod) { // Fetch latest info to send to the mobile
                                                if (prod != null) {
                                                    res.json(prod);
                                                } else {
                                                    res.status(401).send("Producer Not Found");
                                                }
                                            });
                                        }
                                        else {
                                            res.status(401).send("Failed To Remove Device Type");
                                        }
                                    }
                                );
                            }
                        });
                    }
                });
            }
        });
    }
});

/* Checks history of a given device */
app.post('/api/producer/givenDeviceHistory', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var historyReadings = [];

    models.Device.findOne({name: req.body.name}, function (err, dev) { //Get all history
        if (dev != null) {
            historyReadings = dev.readings;
            res.send(historyReadings); //Send the object with the final history
        }
    });
});

/* Checks history of a given device from the app */
app.post('/api/mobile/producer/givenDeviceHistory', function (req, res) {
    if (req.body.platform != undefined) {
        res.header("Access-Control-Allow-Origin", "*");

        var historyReadings = [];

        models.Device.findOne({name: req.body.name}, function (err, dev) { //Get all history
            if (dev != null) {
                historyReadings = dev.readings;
                res.send(historyReadings); //Send the object with the final history
            }
        });
    }
});

/* Checks history of a given device type history */
app.post('/api/producer/deviceTypeHistory', function (req, res) {
    res.header("Access-Control-Allow-Origin", "*");

    var readings = [];
    var historyName = [];

    models.Device.find({device_type: req.body.type}, function (err, dev) { //Get all history
        if (dev != null) {
            for (var i = 0; i < dev.length; i++) {
                if (dev[i].readings != null) {

                    for (var j = 0; j < dev[i].readings.length; j++) {
                        historyName.push(dev[i].name);
                        readings.push(dev[i].readings[j]);
                    }
                }
            }

            var history = {
                historyName: historyName,
                readings: readings
            };

            res.send(history); //Send the object with the final history
        }
    });
});

/* Checks history of a given device type history from the app */
app.post('/api/mobile/producer/devicetype/history', function (req, res) {
    if (req.body.platform != undefined) {

        var readings = [];

        models.Device.find({device_type: req.body.type}, function (err, dev) { //Get all history
            if (dev != null) {
                for (var i = 0; i < dev.length; i++) {
                    if (dev[i].readings != null) {

                        for (var j = 0; j < dev[i].readings.length; j++) {
                            readings.push(dev[i].name + " -> " + dev[i].readings[j]);
                        }
                    }
                }

                res.send(readings); //Send the object with the final history
            }
        });
    }
});

/* Check history of a given user */
app.post('/api/producer/userHistory', function (req, res) {
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

/* Check history of a given user from the app*/
app.post('/api/mobile/producer/userHistory', function (req, res) {
    if (req.body.platform != null) {
        res.header("Access-Control-Allow-Origin", "*");

        var history = [];

        models.UserHistory.find({username: req.body.username}, function (err, user) { //Get all history
            if (user != null) {
                for (var i = 0; i < user.length; i++) {
                    history.push("[" + user[i].date + "] -> " + user[i].description); //Build an array with all information
                }
                res.send(history); //Send the final array
            }
        });
    }
});

/* Consult user info */
app.post('/api/mobile/producer/producerInfo', function (req, res) {
    if (req.body.platform != undefined) {
        models.Producer.findOne({username: req.body.username}, function (err, producer) { //Find producer
            if (producer != null) {
                res.json(producer); //Send producer info
            }
        });
    }
});