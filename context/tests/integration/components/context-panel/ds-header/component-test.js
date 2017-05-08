import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import dSDetails from 'context/config/machines';

moduleForComponent('context-panel/ds-header', 'Integration | Component | context panel/ds header', {
  integration: true
});

test('it renders', function(assert) {
  const contextData = {};


  this.set('contextData', contextData);
  this.set('dSDetails', dSDetails);
  this.render(hbs`{{context-panel/ds-header contextData=contextData dSDetails=dSDetails}}`);

  assert.equal(this.$().text().trim(), 'Endpoint');
});