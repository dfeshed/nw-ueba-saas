import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | investigate-header', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const tabsWithoutUsers = ['Navigate', 'Events', 'Event Analysis', 'Hosts', 'Files', 'Malware Analysis'];
  const tabsWithUsers = ['Navigate', 'Events', 'Event Analysis', 'Hosts', 'Files', 'Users', 'Malware Analysis'];

  test('The header shows all of the tabs except Users', async function(assert) {
    assert.expect(6);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('authorities', []);
    await render(hbs`{{#investigate-header}}{{/investigate-header}}`);
    findAll('.rsa-nav-tab').forEach((tab, index) => {
      assert.equal(tab.textContent.trim(), tabsWithoutUsers[index]);
    });
  });

  test('The header shows all of the tabs including Users when user has the UEBA_Analysts role', async function(assert) {
    assert.expect(7);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('authorities', ['UEBA_Analysts']);
    await render(hbs`{{#investigate-header}}{{/investigate-header}}`);
    findAll('.rsa-nav-tab').forEach((tab, index) => {
      assert.equal(tab.textContent.trim(), tabsWithUsers[index]);
    });
  });

  test('Clicking on Events Analysis would route to the default url', async function(assert) {
    assert.expect(1);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('authorities', []);
    await render(hbs`{{#investigate-header}}{{/investigate-header}}`);

    const link = find('.rsa-nav-tab.is-active a');
    assert.equal(link.pathname, '/investigate/events', 'Events Analysis tab should always route to the default url');
  });
});