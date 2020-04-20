import Route from '@ember/routing/route';

export default Route.extend({
  model() {
    return {
      title: 'Switches',
      subtitle: 'Basic switches for manipulating values.',
      description: 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling.',
      jsRepo: 'https://github.com/knownasilya/ember-toggle',
      styleRepo: 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-switches.scss'
    };
  }
});
