import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Collapseable Navigation',
      'subtitle': 'A collection of tabs that collapses into a single tab when there is insufficient room to display them all.',
      'description': `
        This component should be used whenever horizontal nav is called for.
        Using this component ensures additional options can be added in the
        future without requiring design effort, and minimalizing developer cost.

        <br/>
        <br/>

        This component uses the IntersectionObserver API to detect when the
        rightside edge of the rsa-collapseable-nav component intersects with
        the .intersection-trigger element. The .intersection-trigger element
        is positioned inline with the other tabs, so the list is ensured to
        collapse before obscuring tab options.

        <br/>
        <br/>

        Two components are included: rsa-collapseable-nav and
        rsa-collapseable-nav/collapsedList. The
        rsa-collapseable-nav/collapsedList is supplementary and only exists
        to facilitate testing of collapsed actions due to the reliance on
        rsa-tethered-panel. The rsa-tethered-panel renders outside the
        testable scope which makes testing panel content essentially impossible.
      `,
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-collapseable-nav.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/nav/_collapseable-nav.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-collapseable-nav.hbs'
    };
  }

});
