/**
 * Created by André on 12/04/2017.
 */

var controllersISapp = angular.module('controllersISapp', []); // module to control the register sign in and listing app


//controller for the register view part of the controllerschatapp modules part of the root module rit2app
controllersISapp.controller('registerCtrl', function ($scope, $http, $window, $location,$encryp) { //receives the scope of this controller and http service for calls

    $scope.register = function () {

        if ($scope.role == "producer") {
            $scope.producer = $scope.form;
            if ($scope.producer.name != undefined && $scope.producer.rpassword != undefined && $scope.producer.password != undefined && $scope.producer.username != undefined && $scope.producer.email != undefined) {

                if ($scope.producer.password == $scope.producer.rpassword) {
                    var date = new Date();
                    $scope.producer.name = $encryp.pass($scope.producer.name);
                    $scope.producer.password = $encryp.pass($scope.producer.password);
                    $scope.producer.username = $encryp.pass($scope.producer.username);
                    $scope.producer.email  = $encryp.pass($scope.producer.email);

                    $scope.producer.date_registered = date;

                    $http
                        .post('/api/producer/register', $scope.producer)
                        .success(function (data, status, headers, config) {
                            $location.path("/signin");
                        });
                }
                else {
                    $scope.isError = true;  // show error in page
                    $scope.error = 'Error: Passwords doesnt match';
                }
            }
            else {
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Some of the fields are empty';
            }
        }

        if ($scope.role == "user") {
            $scope.user = $scope.form;


            if ($scope.user.name != undefined && $scope.user.rpassword != undefined && $scope.user.password != undefined && $scope.user.username != undefined && $scope.user.email != undefined) {

                if ($scope.user.password == $scope.user.rpassword) {
                    var date = new Date();
                    $scope.user.name = $encryp.pass($scope.user.name);
                    $scope.user.password = $encryp.pass($scope.user.password);
                    $scope.user.username = $encryp.pass($scope.user.username);
                    $scope.user.email = $encryp.pass($scope.user.email);

                    $scope.user.date_registered = date;

                    $http
                        .post('/api/user/register', $scope.user)
                        .success(function (data, status, headers, config) {
                            $location.path("/signin");
                        });
                }
                else {
                    $scope.isError = true;  // show error in page
                    $scope.error = 'Error: Passwords doesnt match';
                }
            }
            else {
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Some of the fields are empty';
            }
        }
    }
});
controllersISapp.controller('signinCtrl', function ($scope, $http, $window, $location, $socket,$encryp) {


    $scope.go_f = function () {
        $location.path("/register");
    }
    $scope.login_face = function () {

        FB.login(function (response) {

            if (response.authResponse) {
                $scope.token = response.authResponse.accessToken;
                //console.log('Welcome!  Fetching your information.... ');
                FB.api('/me', 'get', {fields: 'id,name,email,last_name'}, function (response) {
                    //console.log('Good to see you, ' + response.name + '.');
                    $window.sessionStorage.facename = response.name;
                    $window.sessionStorage.facepassword = response.id;
                    $window.sessionStorage.faceusername = response.last_name;
                    $window.sessionStorage.faceemail = response.email;
                    $window.sessionStorage.token = $scope.token;

                    var date = new Date();

                    $window.sessionStorage.facedate_registered = date;

                    $http
                        .post('/api/user/fbRegLog', {

                            name: $encryp.pass($window.sessionStorage.facename),
                            password: $encryp.pass(window.sessionStorage.facepassword),
                            email: $encryp.pass($window.sessionStorage.faceemail),
                            date_registered: $window.sessionStorage.facedate_registered,
                            username: $encryp.pass($window.sessionStorage.faceusername)
                        })
                        .success(function (data, status, headers, config) {
                            $location.path("/user");

                        })

                })
            } else {
                console.log('User cancelled login or did not fully authorize.');
            }

        });
    }


    $scope.login = function () {

        if ($scope.role == "Producer") {
            $scope.producer = $scope.form;
            $scope.producer.username=$encryp.pass($scope.producer.username);
            $scope.producer.password=$encryp.pass($scope.producer.password);

            $http
                .post('/api/producer/login', $scope.producer)
                .success(function (data, status, headers, config) {
                    $window.sessionStorage.username_producer = $scope.producer.username;
                    $window.sessionStorage.token = data.token;
                    $location.path("/producer");

                })
                .error(function (data, status, headers, config) { //if HTTP response was not OK;
                    $scope.isError = true;  // show error in page
                    $scope.error = 'Error: Wrong user or password';
                });
        }

        if ($scope.role == "User") {
            $scope.user = $scope.form;
            $scope.user.username=$encryp.pass($scope.user.username);
            $scope.user.password=$encryp.pass($scope.user.password);
            $http
                .post('/api/user/login', $scope.user)
                .success(function (data, status, headers, config) {
                    //$window.sessionStorage.username=data.username;
                    $window.sessionStorage.username_user = $scope.user.username;
                    // $window.sessionStorage.token = data.token;
                    $window.sessionStorage.islogged = true;

                    $location.path("/user");
                })
                .error(function (data, status, headers, config) { //if HTTP response was not OK;
                    $scope.isError = true;  // show error in page
                    $scope.error = 'Error: Wrong user or password';
                });
        }
    }
});

controllersISapp.controller('userCtrl', function ($scope, $http, $window, $location, $socket) {
    $socket.connect(); // connect to the Server WebSocket

    if ($window.sessionStorage.faceusername != null) {
        $window.sessionStorage.username_user = $window.sessionStorage.faceusername;
        $window.sessionStorage.facename = null;
        $window.sessionStorage.facepassword = null;
        $window.sessionStorage.faceemail = null;
        $window.sessionStorage.facedate_registered = null;
        $window.sessionStorage.faceusername = null;
    }

    $scope.auxiliar_reg_device = false;
    $scope.auxiliar_stop_device = false;
    $scope.auxiliar_unregister_device = false;
    $scope.table_history = false;
    $scope.auxiliar_start_device = false;
    $scope.auxiliar_remove_device = false;
    $scope.list = [];
    $scope.username = {user: $window.sessionStorage.username_user};


    $socket.emit('newUser:username', { //send new user event via websocket to server containing username
        username: $window.sessionStorage.username_user
    });

    $socket.on('list:producers', function (data) { // event received when a user logs in the server
        $scope.list_device_models = [];
        $scope.list_device_types = [];
        $scope.list_names = [];

        for (var i = 0; i < data.Producers.length; i++) {

            for (var j = 0; j < data.Producers[i].device_models.length; j++) {
                $scope.list_device_models.push(data.Producers[i].device_models[j]);
            }

            for (var z = 0; z < data.Producers[i].device_types.length; z++) {
                $scope.list_device_types.push(data.Producers[i].device_types[z]);
            }

            $scope.list_names.push(data.Producers[i].name);
        }
    });

    $socket.on('list:userDevices', function (data) { // event received when a user logs in the server
        $scope.list.push(data.devices);
    });


    $scope.auxiliar_reg = function () {
        $scope.auxiliar_reg_device = true;
        $scope.auxiliar_stop_device = false;
        $scope.auxiliar_unregister_device = false;
        $scope.auxiliar_start_device = false;
        $scope.auxiliar_remove_device = false;
        $scope.table_history = false;
        $scope.table_device_history = false;

    }

    $scope.auxiliar_stop = function () {
        $scope.auxiliar_reg_device = false;
        $scope.auxiliar_stop_device = true;
        $scope.auxiliar_unregister_device = false;
        $scope.auxiliar_start_device = false;
        $scope.auxiliar_remove_device = false;
        $scope.table_history = false;
        $scope.table_device_history = false;
    }
    $scope.auxiliar_unregister = function () {
        $scope.auxiliar_reg_device = false;
        $scope.auxiliar_stop_device = false;
        $scope.auxiliar_unregister_device = true;
        $scope.auxiliar_start_device = false;
        $scope.auxiliar_remove_device = false;
        $scope.table_history = false;
        $scope.table_device_history = false;

    }

    $scope.auxiliar_start = function () {
        $scope.auxiliar_reg_device = false;
        $scope.auxiliar_stop_device = false;
        $scope.auxiliar_unregister_device = false;
        $scope.auxiliar_start_device = true;
        $scope.auxiliar_remove_device = false;
        $scope.table_history = false;
        $scope.table_device_history = false;

    }

    $scope.auxiliar_remove = function () {
        $scope.auxiliar_reg_device = false;
        $scope.auxiliar_stop_device = false;
        $scope.auxiliar_unregister_device = false;
        $scope.auxiliar_start_device = false;
        $scope.auxiliar_remove_device = true;
        $scope.table_history = false;
        $scope.table_device_history = false;


    }

    $scope.logout = function () {
        $window.sessionStorage.islogged = false;
        $socket.disconnect();
        $location.path("/signin");
    }

    $scope.register_device = function () {
        $scope.auxiliar_reg_device = false;
        $scope.regist_dev = $scope.form1;

        $scope.list[0].push($scope.regist_dev.name);
        $scope.regist_dev.username = $window.sessionStorage.username_user;

        $http.post('http://localhost:8182/api/device/add', {
            name: $scope.regist_dev.name,
            current_state: "on"
        })
            .success(function (data, status, headers, config) {
                $http.post('/api/device/registerDevice', $scope.regist_dev)
                    .success(function (data, status, headers, config) {
                    });
            });

        $scope.form1 = null;
    }

    $scope.stop_device = function () {

        $scope.auxiliar_stop_device = false;
        $scope.stop_dev = $scope.form2;
        $scope.stop_dev.username = $window.sessionStorage.username_user;

        $http
            .post('http://localhost:8182/api/device/state', {
                name: $scope.regist_dev.name,
                state: "off"
            })
            .success(function (data, status, headers, config) {
                $http
                    .post('/api/device/stopMonitoringDevice', $scope.stop_dev)
                    .success(function (data, status, headers, config) {
                    });
            });

        $scope.form2 = null;
    }

    $scope.unregister_device = function () {
        $scope.auxiliar_unregister_device = false;
        $scope.unreg_dev = $scope.form3;
        $scope.unreg_dev.username = $window.sessionStorage.username_user;

        $http
            .post('http://localhost:8182/api/device/state', {
                name: $scope.regist_dev.name,
                state: "unmonitored"
            })
            .success(function (data, status, headers, config) {
                $http
                    .post('/api/device/unRegisterDevice', $scope.unreg_dev)
                    .success(function (data, status, headers, config) {
                    })
            });

        $scope.form3 = null;
    }

    $scope.start_device = function () {
        $scope.auxiliar_start_device = false;
        $scope.start_dev = $scope.form4;
        $scope.start_dev.username = $window.sessionStorage.username_user;

        $http
            .post('http://localhost:8182/api/device/state', {
                name: $scope.regist_dev.name,
                state: "on"
            })
            .success(function (data, status, headers, config) {
                $http
                    .post('/api/device/reMonitorDevice', $scope.start_dev)
                    .success(function (data, status, headers, config) {
                    })
            });

        $scope.form4 = null;
    }

    $scope.remove_device = function () {
        $scope.auxiliar_remove_device = false;
        $scope.remove_dev = $scope.form5;
        $scope.remove_dev.username = $window.sessionStorage.username_user;

        $http.post('http://localhost:8182/api/device/remove', {
            name: $scope.remove_dev.name
        })
            .success(function (data, status, headers, config) {
                $http
                    .post('/api/device/removeDevice', $scope.remove_dev)
                    .success(function (data, status, headers, config) {
                    })
            });

        $scope.form5 = null;
    }


    $scope.user_history = function () {
        $scope.user_h = {username: $scope.username.user};
        $scope.auxiliar_reg_device = false;
        $scope.auxiliar_stop_device = false;
        $scope.auxiliar_unregister_device = false;
        $scope.auxiliar_start_device = false;
        $scope.auxiliar_remove_device = false;
        $scope.table_history = false;
        $scope.table_device_history = false;
        $http
            .post('/api/user/history', $scope.user_h)
            .success(function (data, status, headers, config) {
                $scope.history = data;
                $scope.table_history = true;
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });
    }

    $scope.user_device_history = function () {
        $scope.user_dev_history = {username: $scope.username.user};
        $scope.auxiliar_reg_device = false;
        $scope.auxiliar_stop_device = false;
        $scope.auxiliar_unregister_device = false;
        $scope.auxiliar_start_device = false;
        $scope.auxiliar_remove_device = false;
        $scope.table_history = false;
        $scope.table_device_history = false;

        $http
            .post('/api/user/devices', $scope.user_dev_history)
            .success(function (data, status, headers, config) {
                $scope.history_devices = data;
                $scope.table_device_history = true;
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

    }
});

controllersISapp.controller('producerCtrl', function ($scope, $http, $window, $location, $socket) {
    $socket.connect(); // connect to the Server WebSocket
    $scope.auxiliar_add1 = false;
    $scope.auxiliar_add2 = false;
    $scope.auxiliar_remove1 = false;
    $scope.auxiliar_remove2 = false;


    $scope.username = {username: $window.sessionStorage.username_producer};

    $socket.emit('newProducer:username', { //send new user event via websocket to server containing username
        username: $window.sessionStorage.username_producer
    });

    $scope.auxiliar_fadd1 = function () {
        $scope.auxiliar_add1 = true;
        $scope.auxiliar_add2 = false;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_remove2 = false;
        $scope.auxiliar_dev_type_hist = false;
        $scope.auxiliar_dev_hist = false;
        $scope.auxiliar_user_hist = false;
    }
    $scope.auxiliar_fremove1 = function () {
        $scope.auxiliar_add1 = false;
        $scope.auxiliar_add2 = false;
        $scope.auxiliar_remove1 = true;
        $scope.auxiliar_remove2 = false;
        $scope.auxiliar_dev_type_hist = false;
        $scope.auxiliar_dev_hist = false;
        $scope.auxiliar_user_hist = false;

    }
    $scope.auxiliar_fadd2 = function () {
        $scope.auxiliar_add1 = false;
        $scope.auxiliar_add2 = true;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_remove2 = false;
        $scope.auxiliar_dev_type_hist = false;
        $scope.auxiliar_dev_hist = false;
        $scope.auxiliar_user_hist = false;
    }
    $scope.auxiliar_fremove2 = function () {
        $scope.auxiliar_add1 = false;
        $scope.auxiliar_add2 = false;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_remove2 = true;
        $scope.auxiliar_dev_type_hist = false;
        $scope.auxiliar_dev_hist = false;
        $scope.auxiliar_user_hist = false;
    }

    $scope.auxiliar_fdev_type_hist = function () {
        $scope.auxiliar_add1 = false;
        $scope.auxiliar_add2 = false;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_remove2 = false;
        $scope.auxiliar_dev_type_hist = true;
        $scope.auxiliar_dev_hist = false;
        $scope.auxiliar_user_hist = false;
        $scope.table_device = false;
        $scope.table_user = false;
        $scope.table_device_type = false;

    }

    $scope.auxiliar_fdev_hist = function () {
        $scope.auxiliar_add1 = false;
        $scope.auxiliar_add2 = false;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_remove2 = false;
        $scope.auxiliar_dev_type_hist = false;
        $scope.auxiliar_dev_hist = true;
        $scope.auxiliar_user_hist = false;
        $scope.table_device = false;
        $scope.table_user = false;
        $scope.table_device_type = false;

    }
    $scope.auxiliar_fuser_hist = function () {
        $scope.auxiliar_add1 = false;
        $scope.auxiliar_add2 = false;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_remove2 = false;
        $scope.auxiliar_remove1 = false;
        $scope.auxiliar_dev_type_hist = false;
        $scope.auxiliar_dev_hist = false;
        $scope.auxiliar_user_hist = true;
        $scope.table_device = false;
        $scope.table_user = false;
        $scope.table_device_type = false;
    }

    $scope.logout_producer = function () {
        $scope.auxiliar_reg = false;
        $window.sessionStorage.islogged = false;
        $socket.disconnect();
        $location.path("/signin");
    }

    $scope.add_model = function () {
        $scope.auxiliar_add1 = false;
        $scope.addm = $scope.form;
        $scope.addm.username = $window.sessionStorage.username_producer;
        $http
            .post('/api/producer/productmodel/add', $scope.addm)
            .success(function (data, status, headers, config) {
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form = null;
    }

    $scope.remove_model = function () {
        $scope.auxiliar_remove1 = false;
        $scope.removem = $scope.form;
        $scope.removem.username = $window.sessionStorage.username_producer;
        $http
            .post('/api/producer/productmodel/remove', $scope.removem)
            .success(function (data, status, headers, config) {
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form = null;
    }

    $scope.add_type = function () {
        $scope.auxiliar_add2 = false;
        $scope.addt = $scope.form;
        $scope.addt.username = $window.sessionStorage.username_producer;

        $http
            .post('/api/producer/devicetype/add', $scope.addt)
            .success(function (data, status, headers, config) {
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form = null;
    }

    $scope.remove_type = function () {
        $scope.auxiliar_remove2 = false;
        $scope.removet = $scope.form;
        $scope.removet.username = $window.sessionStorage.username_producer;

        $http
            .post('/api/producer/devicetype/remove', $scope.removet)
            .success(function (data, status, headers, config) {
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form = null;
    }

    $scope.checks_history = function () {
        $scope.auxiliar_dev_hist = false;
        $scope.table_user = false;
        $scope.table_device_type = false;
        $http
            .post('/api/producer/givenDeviceHistory', $scope.form2)
            .success(function (data, status, headers, config) {
                $scope.historyReadings = data;
                $scope.table_device = true;
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form2 = null;
    }

    $scope.devicet_history = function () {
        $scope.auxiliar_dev_type_hist = false;
        $scope.table_device = false;
        $scope.table_user = false;

        $http
            .post('/api/producer/deviceTypeHistory', $scope.form1)
            .success(function (data, status, headers, config) {
                $scope.historyt = data;
                $scope.table_device_type = true;
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form1 = null;
    }

    $scope.user_history = function () {
        $scope.auxiliar_user_hist = false;
        $scope.table_device_type = false;
        $scope.table_device = false;

        $http
            .post('/api/producer/userHistory', $scope.form3)
            .success(function (data, status, headers, config) {

                $scope.history = data;
                $scope.table_user = true;
            })
            .error(function (data, status, headers, config) { //if HTTP response was not OK;
                $scope.isError = true;  // show error in page
                $scope.error = 'Error: Wrong user or password';
            });

        $scope.form3 = null;
    }
});
