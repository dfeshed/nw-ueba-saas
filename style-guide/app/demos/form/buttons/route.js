import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Form Button',
      'subtitle': 'Basic widget for initiating actions.',
      'description': 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling. Click actions can be assigned to buttons by passing an action helper to defaultAction, but remember, actions override default broswer behavior. It\'s a good idea to avoid assigning a defaultAction to buttons with type="submit".',
      'testFilter': 'rsa-form-button',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-form-button.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_form-button.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-form-button.hbs'
    };
  }

});
