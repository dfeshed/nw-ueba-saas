module.exports = {
  // used to manage future features
  future(environment, flagValue) {
    // if environment is test, then we want 'future' on
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