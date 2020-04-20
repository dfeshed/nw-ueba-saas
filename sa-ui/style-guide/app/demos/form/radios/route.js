import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Form Radio',
      'subtitle': 'Basic form radio button.',
      'description': 'The ember radio button component is used in addition with RSA specific styles. For additional info on ember-radio-button, a link to the github docs can be found in the jsRepo link below.',
      'jsRepo': 'https://github.com/yapplabs/ember-radio-button',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-radio.scss'
    };
  }

});
