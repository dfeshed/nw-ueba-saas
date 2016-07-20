import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Form Input',
      'subtitle': 'Single line free form text input field.',
      'description': 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling.',
      'testFilter': 'rsa-form-input',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-form-input.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_form-input.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-form-input.hbs'
    };
  }

});
