import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { linux, windows } from '../../../state/overview.hostdetails';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';

let setState;

moduleForComponent('host-detail/overview/summary', 'Integration | Component | endpoint host overview/summary', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    setState = (machine) => {
      const { overview } = machine;
      const state = Immutable.from({ endpoint: { overview } });
      applyPatch(state);
      this.inject.service('redux');
    };
  },

  afterEach() {
    revertPatch();
  }
});

test('Number of accordions rendered based on OS selected, Linux : 0', function(assert) {
  setState(linux);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const numberOfnumberAccordions = this.$('.rsa-content-accordion').length;
  assert.equal(numberOfnumberAccordions, 0, 'Number of accordions rendered when the selected OS is Linux');
});

test('Accordion titles displayed for Windows OS', function(assert) {
  setState(windows);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const accordionTitles = this.$('.rsa-content-accordion h3');
  assert.equal(accordionTitles[0].innerText.trim(), 'SECURITY CONFIGURATION', 'Third accordion is for the list Security configuration');
});