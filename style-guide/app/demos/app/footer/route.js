import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Application Footer',
      'subtitle': 'Full width branded footer.',
      'description': 'The Application Footer is a single use component that generally lives in the application template of the project. Because of this, it\'s unlikely you will need to work with it directly. However, in the case that you do, examples can be found below. Because this component is absolutely positioned within the document, we do not demo the component with the code examples. However you can observe it\'s behavior in the Style Guide itself.',
      'testFilter': 'rsa-application-footer',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-application-footer.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_application-footer.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-application-footer.hbs'
    };
  }

});
