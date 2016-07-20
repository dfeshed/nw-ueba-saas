import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Application Content',
      'subtitle': 'Application wrapper for use with rsa-application-header and rsa-application-footer',
      'description': 'The Application Content is a single use component that generally lives in the application template of the project. Because of this, it\'s unlikely you will need to work with it directly. However, in the case that you do, an example can be found below. Because this component is absolutely positioned within the document, we do not demo the component with the code example. However you can observe it\'s behavior in the Style Guide itself.',
      'testFilter': 'rsa-application-content',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-application-content.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_application-content.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-application-content.hbs'
    };
  }

});
