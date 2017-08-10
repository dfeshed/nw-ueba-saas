import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { INITIALIZE_CONTEXT_PANEL } from 'context/actions/types';

moduleForComponent('context-panel/header', 'Integration | Component | context panel/header', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  this.set('i18n', { t() {
    return 'Return Value';
  }
  });

  this.get('redux').dispatch({
    type: INITIALIZE_CONTEXT_PANEL,
    payload: { lookupKey: '1.1.1.1', meta: 'IP' }
  });

  this.render(hbs`{{context-panel/header i18n=i18n}}`);
  assert.ok(this.$('.rsa-icon-help-circle-lined').length === 1, 'Need to display help icons.');
  assert.ok(this.$('.rsa-icon-close-filled').length === 1, 'Need to display close icons.');
  assert.ok(this.$('.rsa-context-panel__header').text().indexOf('1.1.1.1') > 0, 'Need to display only Meta key.');
});