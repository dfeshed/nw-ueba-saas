import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Collapseable Actions',
      'subtitle': 'A collection of actionable components that collapses as available space is reduced.',
      'description': `
        The rsa-collapseable-actions component groups controls into three
        sections arranged left to right: dropdowns, buttons, and toggles.
        In its fully expanded state, everything is aligned horizontally and
        labels and icons are both displayed. As the component is further
        collapsed, icons and labels will be removed from buttons, non-isPrimary
        buttons will nest under an additional More dropdown and toggles will
        wrap to a second line.

        <br><br>

        The removal of icons and labels for buttons can be controlled via the
        isPrimary property within the buttonList.
      `,
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-collapseable-actions/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_collapseable-actions.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-collapseable-actions/template.hbs'
    };
  }

});
