import { module, test } from 'qunit';
import Service from '@ember/service';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll } from '@ember/test-helpers';

const event = {
  id: '586ecf95ecd25950034e1312:0'
};

const enrichment = {
  isEnrichment: true,
  key: 'foo',
  i18nKey: 'i18nFoo',
  value: 'bar',
  allEnrichments: {}
};

module('Integration | Component | rsa alerts table alert item', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'incident'
    }));
  });

  test('it renders an enrichment if given an object with isEnrichment, an event otherwise', async function(assert) {
    assert.expect(7);

    this.set('item', enrichment);
    this.set('autoHighlightEntities', false);
    await render(hbs`{{rsa-alerts-table/alert-item item=item autoHighlightEntities=autoHighlightEntities index=0}}`);

    assert.equal(findAll('.rsa-alerts-table-alert-item').length, 1, 'Expected to find root DOM node.');
    assert.ok(findAll('.enrichment').length, 'Expected to find enrichment DOM');
    assert.notOk(findAll('.event').length, 'Expected to not find event DOM');
    assert.notOk(findAll('[test-id=respondReconLink]').length, 'Expected to not find event recon link');

    this.set('item', event);

    assert.notOk(findAll('.enrichment').length, 'Expected to not find enrichment DOM');
    assert.ok(findAll('.event').length, 'Expected to find event DOM');
    assert.ok(findAll('[test-id=respondReconLink]').length, 'Expected to find event recon link');
  });
});
