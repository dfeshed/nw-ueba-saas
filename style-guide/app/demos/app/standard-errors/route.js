import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Standard Errors',
      'subtitle': 'Currently only enabled for Investigate Events errors',
      'description': 'The standard-errors service is a wrapper that allows for consistent display of both common and uncommon errors. This service is backed by the error-codes util that provides consistent parsing of errorCodes.',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/services/standard-errors.js'
    };
  }

});
