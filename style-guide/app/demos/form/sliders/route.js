import Ember from 'ember';

const {
  Route,
  Logger
} = Ember;

export default Route.extend({
  actions: {
    onSliderChange(value) {
      Logger.debug('Slider is moved to ', value);
    }
  },

  model() {
    return {
      'start': [ 25,75 ],
      'range': {
        'min': [ 0 ],
        'max': [ 100 ]
      },
      'steps': 1,
      'disabled': true,
      'connect': true,
      'title': 'Form Slider',
      'subtitle': 'Basic form slider.',
      'description': 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling.',
      'testFilter': 'rsa-form-slider',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-form-slider.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/form/_form-slider.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-form-slider.hbs'
    };
  }
});