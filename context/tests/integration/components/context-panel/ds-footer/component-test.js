import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import dSDetails from 'context/config/lists';

moduleForComponent('context-panel/ds-footer', 'Integration | Component | context panel/ds footer', {
  integration: true
});

test('it renders', function(assert) {

  const contextData = {};

  this.set('contextData', contextData);
  this.set('dSDetails', dSDetails);
  this.render(hbs`{{context-panel/ds-footer contextData=contextData dSDetails=dSDetails length=5}}`);

  assert.equal(this.$().text().trim(), 'Total: 5');
});
