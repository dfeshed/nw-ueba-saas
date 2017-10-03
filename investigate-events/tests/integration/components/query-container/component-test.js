import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('query-container', 'Integration | Component | query container', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).initializeData();
  this.render(hbs`{{query-container}}`);
  assert.equal(this.$().text().trim(), '');
});
