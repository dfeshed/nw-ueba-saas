import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-alerts-table/alert-item', 'Integration | Component | rsa alerts table alert item', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const event = {
  id: 'event1'
};

const enrichment = {
  isEnrichment: true,
  key: 'foo',
  i18nKey: 'i18nFoo',
  value: 'bar',
  allEnrichments: {}
};

test('it renders an enrichment if given an object with isEnrichment, an event otherwise', function(assert) {
  this.set('item', enrichment);
  this.render(hbs`{{rsa-alerts-table/alert-item item=item index=0}}`);

  return wait()
    .then(() => {
      assert.equal(this.$('.rsa-alerts-table-alert-item').length, 1, 'Expected to find root DOM node.');
      assert.ok(this.$('.enrichment').length, 'Expected to find enrichment DOM');
      assert.notOk(this.$('.event').length, 'Expected to not find event DOM');

      // Swap out the data item for an event.
      this.set('item', event);
      return wait();
    })
    .then(() => {
      assert.notOk(this.$('.enrichment').length, 'Expected to not find enrichment DOM');
      assert.ok(this.$('.event').length, 'Expected to find event DOM');
    });
});
