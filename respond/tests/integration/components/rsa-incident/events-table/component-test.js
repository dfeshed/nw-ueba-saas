import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../../helpers/data-helper';

moduleForComponent('rsa-incident-events-table', 'Integration | Component | Incident Events Table', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.inject.service('redux');
    // TODO figure out what to specifically inject into, rather than all components
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentStoryline();
  this.render(hbs`{{rsa-incident/events-table}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-data-table');
    assert.equal($el.length, 1, 'Expected to find data table root element in DOM.');

    const $rows = $el.find('.rsa-data-table-body-row');
    assert.ok($rows.length, 'Expected to find at least one data table body row element in DOM.');

    const $cells = $rows.first().find('.rsa-incident-events-table-row__body');
    assert.ok($cells.length, 'Expected to find at least one data table row body element in DOM.');
  });
});