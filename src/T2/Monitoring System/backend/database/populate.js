var models = require('./models.js');
var debug = false;

module.exports = {
  /* Populates devices' states */
  states : function() {
    models.State.find(function(err, states) {
      if(states.length != 3) {
        if(states.length > 0) models.State.remove();
        new models.State({
          id_state : 'on',
          name : 'On'
        }).save();
        new models.State({
          id_state : 'off',
          name : 'Off'
        }).save();
        new models.State({
          id_state : 'unmonitored',
          name : 'Unmonitored'
        }).save();
        if(debug) console.log('[Debug API] States created successfully.');
      }
      else if(debug) console.log('[Debug API] States already exist.');
    });
  },
  /* Populates system's errors */
  system_errors : function() {
    models.SystemError.find(function(err, system_errors) {
      if(system_errors.length != 4) {
        if(system_errors.length > 0) models.SystemError.remove();
        new models.SystemError({
          id_system_error : 'no_error',
          name : 'No Error'
        }).save();
        new models.SystemError({
          id_system_error : 'no_database_connection',
          name : 'No Database Connection'
        }).save();
        new models.SystemError({
          id_system_error : 'no_data_extraction',
          name : 'No Data Extraction'
        }).save();
        new models.SystemError({
          id_system_error : 'unrecognized',
          name : 'Unrecognized'
        }).save();
        if(debug) console.log('[Debug API] System Errors created successfully.');
      }
      else if(debug) console.log('[Debug API] System Errors already exist.');
    });
  }
}

module.exports.models = models;
