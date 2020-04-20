import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Properties Panel',
      'subtitle': 'A reusable component for displaying supplemental property data.',
      'description': `
        This pattern is more style and organization than functionality.
        Instead, this component leverages several other components such as
        rsa-collapseable-nav, rsa-collapseable-actions, rsa-content-accordion,
        rsa-editable-text, and rsa-content-definition. Within this component
        are two distinct sections, the header and the content. The developer
        has full control over what is included in these sections but there are
        some basic guidelines which are included below.

        <br><br>

        The Header: The header typically contains a title(h1), a sometimes
        editable summary(h2), icons for controlling the panel itself(.control-icons),
        potentially key value pairs(rsa-content-definition), potentially
        navigation(rsa-collapseable-nav), and potentially actions for the
        content below(rsa-collapseable-actions).

        <br><br>

        The Content: The content area is scrollable and allows any content to
        be injected; In the case of rendering grouped key/value pairs, it is
        standard practice to use rsa-content-accordion and vertically
        arranged rsa-content-definition components.
      `,
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-properties-panel/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_properties-panel.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-properties-panel/template.hbs'
    };
  }

});
