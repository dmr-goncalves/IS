var express = require('express');
var app = express();
var path = require('path');
var http = require('http');
var socketio = require('socket.io');
var socketioJwt = require('socketio-jwt'); //For token based authentication in the web server
var SocketController = require('./socket.js'); //Controller that deals with the web socket event
var https = require('https');
var fs = require('fs');

var db = require('./database/index.js');
var user = require('./user/index.js');
var device = require('./device/index.js');
var producer = require('./producer/index.js');

var options = {
    key: fs.readFileSync('localhostt.key'),
    cert: fs.readFileSync('localhostt.cert'),
}

var _secret = "FCT is the best college";

app.use(require('body-parser').json());

/* CORS handling. */
app.use(require('body-parser').urlencoded({
    extended: true
}));

app.set('port', process.env.PORT || 3000); // you can change the port to another value here
app.set('views', path.join(__dirname, '..', 'frontend/views')); // sets up the path for the Jade Templates
app.set('view engine', 'jade'); //setup template engine - we're using jade
app.use(express.static(path.join(__dirname, '..', 'frontend/public')));// setting up the public dir

app.get('/', function (req, res) {
    var templateData = {							//data to set html header (angular module) and page title that is used also for menu
        angularApp: "ISapp",
        pageTitle: "IS"
    }
    res.render('index', templateData);
});

app.use(user);
app.use(device);
app.use(producer);

var server = http.createServer(app);
var io = socketio.listen(server); //WebSocket server object associated with the same port as HTTP
server.listen(app.get('port'), function () {
    console.log('Express server listening at %s on port %s', server.address().address, app.get('port'));
});

/*app.listen(3000, function () {
 console.log('[Monitoring API] Ready.');
 });*/


var ssl = https.createServer(options, app);

ssl.listen(443, function () {
    console.log('Express "secure" server listening on port ' + 443);
});

var ios = socketio.listen(ssl); //WebSocket server Object associated with the same port as HTTP
SocketController.default.StartSocket(ios, socketioJwt, _secret); // call StartSocket function in socketController

SocketController.default.StartSocket(io, socketioJwt, _secret); //Call StartSocket function in SocketController

module.exports._secret = _secret;