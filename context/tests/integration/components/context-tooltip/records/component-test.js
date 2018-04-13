import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const model = { type: 'IP', id: '10.20.30.40' };

module('Integration | Component | context tooltip records', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.owner.inject('component', 'context', 'service:context');
    initialize(this.owner);
  });

  test('it renders', async function(assert) {
    assert.expect(4);

    this.set('model', model);
    await render(hbs`{{context-tooltip/records model=model}}`);

    assert.equal(this.$('.rsa-context-tooltip-records').length, 1);
    assert.ok(findAll('.rsa-context-tooltip-records__record').length, 'Expected to find one or more records in the DOM');
    assert.ok(find('.rsa-context-tooltip-records__record .value').textContent.trim(), 'Expected to find record value');
    assert.ok(find('.rsa-context-tooltip-records__record .text').textContent.trim(), 'Expected to find record name');
  });

  test('it only renders criticality and asset risk for IPs, HOSTs', async function(assert) {
    assert.expect(5);

    const criticality = 'Criticality';
    const assetRisk = 'Asset Risk';

    this.set('model', model);
    await render(hbs`{{context-tooltip/records model=model}}`);

    assert.ok(findAll('.rsa-context-tooltip-records__record .text')[5].textContent.indexOf(criticality), 'Expected to find Criticality Attribute for IP');
    assert.ok(findAll('.rsa-context-tooltip-records__record .text')[6].textContent.indexOf(assetRisk), 'Expected to find Asset Risk Attribute for IP');

    this.setProperties({
      model: { type: 'HOST', id: 'MACHINE1' }
    });

    assert.ok(findAll('.rsa-context-tooltip-records__record .text')[4].textContent.indexOf(criticality), 'Expected to find Criticality Attribute for HOST');
    assert.ok(findAll('.rsa-context-tooltip-records__record .text')[5].textContent.indexOf(assetRisk), 'Expected to find Asset Risk Attribute for HOST');

    this.setProperties({
      model: { type: 'USER', id: 'testuser' }
    });

    let archerAttributesFound = false;

    // archerAttributesFound flag will be true if Criticality or Asset Risk attribute is available in DOM
    findAll('.rsa-context-tooltip-records__record .text').forEach((element) => {
      if (element.innerHTML.trim() === criticality || element.innerHTML.trim() === assetRisk) {
        archerAttributesFound = true;
      }
    });

    assert.notOk(archerAttributesFound, 'Expected not to find Criticality & Asset Risk Attributes for USER');

  });
});
