import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Form Errors',
      'subtitle': 'How to display errors associated with an entire form opposed to a single field.',
      'description': 'Single field errors are handled by each form control, but errors that apply to an entire form can use the .form-error-message class. Form errors should always be placed immediately above the button controls.',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-errors.scss'
    };
  }

});
