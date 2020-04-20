import Route from '@ember/routing/route';
import { debug } from '@ember/debug';

export default Route.extend({
  actions: {
    onSliderChange(value) {
      debug(`Slider is moved to ${value}`);
    }
  },

  model() {
    return {
      // defaults for 2 handle slider
      start: [ 25, 75 ],
      steps: 1,
      disabled: true,
      connect: true,
      // defaults for 1 handle slider
      singleStart: [ 25 ],
      singleSteps: 1,
      singleConnect: false,
      //
      title: 'Form Slider',
      subtitle: 'Basic form slider.',
      description: 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling.',
      testFilter: 'range-slider',
      jsRepo: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/range-slider/component.js',
      styleRepo: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-slider.scss'
    };
  }
});