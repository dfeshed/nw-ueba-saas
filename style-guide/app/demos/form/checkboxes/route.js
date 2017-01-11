import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Form Checkbox',
      'subtitle': 'Basic form checkbox.',
      'description': 'rsa-form-checkbox-2 will replace rsa-form-checkbox in the near future. rsa-form-checkbox-2 is simply an extention of the native EmberJS checkbox input helper with some states specific to RSA. Refer to the input helper docs for additional information: http://emberjs.com/api/classes/Ember.Templates.helpers.html#method_input.',
      'testFilter': 'rsa-form-checkbox-2',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-form-checkbox-2.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-checkbox-2.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-form-checkbox-2.hbs'
    };
  }

});
