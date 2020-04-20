import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Editable Text',
      'subtitle': 'A toggleable text display that allows editing.',
      'description': `
        This component should be used whenever a single piece of text requires
        editability outside of a form. This component can be rendered within
        any block level element, but inline rendering is not yet supported.
      `,
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-editable-text/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_editable-text.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-editable-text/template.hbs'
    };
  }

});
