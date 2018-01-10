import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { linux, windows, mac } from '../../../state/overview.hostdetails';
import engineResolverFor from '../../../../../helpers/engine-resolver';
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

test('Number of accordions rendered based on OS selected, Windows : 3', function(assert) {
  setState(windows);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const numberOfnumberAccordions = this.$('.rsa-content-accordion').length;
  assert.equal(numberOfnumberAccordions, 3, 'Number of accordions rendered when the selected OS is Windows');
});

test('Number of accordions rendered based on OS selected, Linux : 2', function(assert) {
  setState(linux);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const numberOfnumberAccordions = this.$('.rsa-content-accordion').length;
  assert.equal(numberOfnumberAccordions, 2, 'Number of accordions rendered when the selected OS is Linux');
});

test('Number of accordions rendered based on OS selected, Mac : 2', function(assert) {
  setState(mac);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const numberOfnumberAccordions = this.$('.rsa-content-accordion').length;
  assert.equal(numberOfnumberAccordions, 2, 'Number of accordions rendered when the selected OS is Mac');
});

test('Accordion titles displayed for Windows OS', function(assert) {
  setState(windows);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const accordionTitles = this.$('.rsa-content-accordion h3');
  assert.equal(accordionTitles[0].innerText.trim(), 'IP ADDRESSES (4)', 'First accordion is for IP address and IP address count');
  assert.equal(accordionTitles[1].innerText.trim(), 'LOGGED-IN USERS (2)', 'Second accordion is for Logged-In users and Logged-In users count');
  assert.equal(accordionTitles[2].innerText.trim(), 'SECURITY CONFIGURATION', 'Third accordion is for the list Security configuration');
});

test('Accordion titles displayed for Linux OS', function(assert) {
  setState(linux);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const accordionTitles = this.$('.rsa-content-accordion h3');
  assert.equal(accordionTitles[0].innerText.trim(), 'IP ADDRESSES (1)', 'First accordion is for IP address and IP address count');
  assert.equal(accordionTitles[1].innerText.trim(), 'LOGGED-IN USERS (2)', 'Second accordion is for Logged-In users and Logged-In users count');
});

test('Accordion titles displayed for Mac OS', function(assert) {
  setState(mac);
  this.render(hbs`{{host-detail/overview/summary}}`);
  const accordionTitles = this.$('.rsa-content-accordion h3');
  assert.equal(accordionTitles[0].innerText.trim(), 'IP ADDRESSES (1)', 'First accordion is for IP address and IP address count');
  assert.equal(accordionTitles[1].innerText.trim(), 'LOGGED-IN USERS (2)', 'Second accordion is for Logged-In users and Logged-In users count');
});