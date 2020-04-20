import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Form Checkbox',
      'subtitle': 'Basic form checkbox.',
      'description': 'rsa-form-checkbox will replace rsa-form-checkbox in the near future. rsa-form-checkbox is simply an extention of the native EmberJS checkbox input helper with some states specific to RSA. Refer to the input helper docs for additional information: http://emberjs.com/api/classes/Ember.Templates.helpers.html#method_input.',
      'testFilter': 'rsa-form-checkbox',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-form-checkbox.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-checkbox.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-form-checkbox.hbs'
    };
  }

});
