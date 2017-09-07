import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('file-list', 'Integration | Component | file list', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('should initially load files', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{file-list}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-data-table');
    assert.equal($el.length, 1, 'Expected to find data table root element in DOM.');

    const $rows = $el.find('.rsa-data-table-body-row');
    assert.ok($rows.length, 'Expected to find at least one data table body row element in DOM.');

  });
});

test('should show loading indicator on sorting', function(assert) {
  new DataHelper(this.get('redux')).initializeData().setSortBy();
  this.render(hbs`{{file-list}}`);
  return wait().then(() => {
    assert.equal(this.$().find('.rsa-loader').length, 1);
  });
});

