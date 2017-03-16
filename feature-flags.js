module.exports = {
  'show-respond-route': true,
  'show-investigate-route': true,

  // some features that have been completed are turned off for 11.0
  // and will not be included until 11.1.
  // default to this true for devs, but let jenkins turn off via
  // command-line
  '11.1-enabled': function(environment, flagValue) {

    // if environment is test, then we want 11.1 on
    // as we do not want to have to deal with changing
    // tests for short period
    if (environment === 'test') {
      return true;
    }

    // If no flag value defined, go with leaving it
    // enabled, this mostly handles devs, consider this
    // the "default"
    if (flagValue === undefined) {
      return true;
    }

    // otherwise return the flagValue
    return flagValue;
  }
};