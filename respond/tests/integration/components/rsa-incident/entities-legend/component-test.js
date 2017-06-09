import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('rsa-incident-entities-legend', 'Integration | Component | Incident Entities Legend', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const data = [
  { key: 'ip', value: 2 },
  { key: 'host', value: 3 },
  { key: 'domain', value: 4 }
];

const selection = { type: '', ids: [] };

test('it renders counts and sometimes renders selection info when appropriate', function(assert) {
  this.setProperties({
    data,
    selection
  });
  this.render(hbs`{{rsa-incident/entities-legend data=data selection=selection}}`);

  return wait()
    .then(() => {
      const $el = this.$('.rsa-incident-entities-legend');
      assert.equal($el.length, 1, 'Expected to find root element in DOM.');

      const $counts = $el.find('.datum');
      assert.ok($counts.length, 'Expected to find at least one count element in DOM.');

      const $noSelection = $el.find('.selection');
      assert.notOk($noSelection.length, 'Expected not to find any selection DOM when no selection has been applied.');

      this.set('selection', { type: 'indicator', ids: ['indicatorId1'] });
      return wait();
    })
    .then(() => {
      const $selection = this.$('.rsa-incident-entities-legend .selection');
      assert.ok($selection.length, 'Expected to find selection DOM when an indicator has been selected.');

      this.set('selection', { type: 'event', ids: ['eventId1'] });
      return wait();
    })
    .then(() => {
      const $selection2 = this.$('.rsa-incident-entities-legend .selection');
      assert.ok($selection2.length, 'Expected to find selection DOM when an event has been selected.');

      this.set('selection', { type: 'node', ids: ['nodeId1'] });
      return wait();
    })
    .then(() => {
      const $selection3 = this.$('.rsa-incident-entities-legend .selection');
      assert.notOk($selection3.length, 'Expected to not find selection DOM when a node has been selected.');
    });
});