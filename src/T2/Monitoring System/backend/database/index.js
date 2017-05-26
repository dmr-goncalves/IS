var populate = require('./populate.js');
var connected = false;

console.log('[Database API] Ready.');

populate.models.mongoose.connect('mongodb://localhost/monitoringSystem');

populate.models.mongoose.connection.on('error', function() {
  console.log('[Database API] Database is offline. Please turn MongoDB on and try again.');
  console.log('[Monitoring API] Process will now terminate..');
  process.exit(0);
});

populate.models.mongoose.connection.on('disconnected', function() {
  if(connected) {
    console.log('[Database API] Connection to the database was lost.');
    console.log('[Monitoring API] Process will now terminate..');
    process.exit(0);
  }
});

populate.models.mongoose.connection.on('connected', function() {
  connected = true;
  populate.states();
  populate.system_errors();
  console.log('[Database API] Connected to the database.');
});

module.exports.models = populate.models;
