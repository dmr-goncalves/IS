/**
 * Created by André on 12/04/2017.
 */

var ISapp = angular.module('ISapp', ['ngRoute','controllersISapp']);


ISapp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
        when('/register', {
            templateUrl: 'partials/register.html', controller: 'registerCtrl'  //register view controller either here or in the template itself
        }).
        when('/signin', {
            templateUrl: 'partials/signin1.html', controller:'signinCtrl'  //signin view controller either here or in the template itself
        }).
        when('/user', {
            templateUrl: 'partials/user.html', controller:'userCtrl'  //online users view controller either here or in the template itself
        }).
        when('/producer', {
            templateUrl: 'partials/producer.html', controller:'producerCtrl'  //online users view controller either here or in the template itself
        }).
        // If invalid route, just redirect to the main signin view
        otherwise({ redirectTo: '/signin' });
    }]);


/*
 * Service to provide an authentication interceptor that will be used in all calls to the $http service to fill the authentication
 * header with the token
 */
ISapp.factory('authInterceptor', function ($rootScope, $q, $window) {
    return {
        request: function (config) {
            config.headers = config.headers || {};
            if ($window.sessionStorage.token) {
                config.headers.Authorization = 'Bearer ' + $window.sessionStorage.token; //sets the http Authorization with the signed token
            }
            return config;
        },
        responseError: function (rejection) {
            return $q.reject(rejection);
        }
    };
});

//Adds the interceptor service to the $httpProvider service
ISapp.config(function ($httpProvider) {
    $httpProvider.interceptors.push('authInterceptor');
});

// encryptation /decryptation
ISapp.factory('$encryp', function () {

    return {
        pass: function(s){
                return s.toString('base64');
            }
    };
});


ISapp.factory('$socket', function ($rootScope, $window) {
    var socket;
    return {
        connect: function(){
            socket = io.connect('', {'force new connection': true ,
                query: 'token=' + $window.sessionStorage.token
            });
            console.log('socket connected');
        },
        disconnect: function(){
            console.log('Socket Disconnecting');

            socket.disconnect();
        },
        on: function (eventName, callback) {
            socket.on(eventName, function () { // the arguments in the callback function() stored in args contain the sent data
                var args = arguments;
                console.log("received event " + eventName + "with arguments " + args);
                $rootScope.$apply(function () { // this is to apply the data to the scope, how this is done depends on the how the function is writen in the actual call in the controller
                    callback.apply(socket, args);
                });
            });
        },
        emit: function (eventName, data, callback) {

            socket.emit(eventName, data, function () {
                var args = arguments;
                console.log("sent event " + eventName + "with arguments " + args);
                $rootScope.$apply(function () {
                    if (callback) {
                        callback.apply(socket, args);
                    }
                });
            })
        }
    };
});


/// facebook inicialization
window.fbAsyncInit = function() {
    FB.init({
        appId      : '1010301732379716',
        xfbml      : true,
        version    : 'v2.6'
    });
};
(function(d, s, id){
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {return;}
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));


