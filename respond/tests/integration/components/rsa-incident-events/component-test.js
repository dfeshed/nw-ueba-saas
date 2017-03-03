import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('rsa-incident-events', 'Integration | Component | Incident Events', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentStoryline();
  this.render(hbs`{{rsa-incident-events}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-data-table');
    assert.equal($el.length, 1, 'Expected to find data table root element in DOM.');

    const $rows = $el.find('.rsa-data-table-body-row');
    assert.ok($rows.length, 'Expected to find at least one data table body row element in DOM.');

    const $cells = $rows.first().find('.rsa-data-table-body-cell');
    assert.ok($cells.length, 'Expected to find at least one data table body cell element in DOM.');
  });
});