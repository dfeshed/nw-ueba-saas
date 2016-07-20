import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Form Checkbox',
      'subtitle': 'Basic form checkbox.',
      'description': 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling.',
      'testFilter': 'rsa-form-checkbox',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-form-checkbox.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_form-checkbox.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-form-checkbox.hbs'
    };
  }

});
