import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/criticality-score', 'Integration | Component | context panel/criticality score', {
  integration: true
});

test('Should show error incase of HIGH rating', function(assert) {

  const label = 'RishRating';
  const score = 'HIGH';
  this.set('label', label);
  this.set('score', score);
  this.render(hbs`{{context-panel/criticality-score label=label score=score}}`);
  assert.equal(this.$('.rsa-context-panel__grid__host-details__field-circle__value').text(), 'HIGH');
  assert.equal(this.$('.rsa-context-panel__grid__host-details__field-circle__value').css('color'), 'rgb(255, 8, 0)');
});

test('Should not show as red incase of NON HIGH rating', function(assert) {

  const label = 'RishRating';
  const score = 'MEDIUM';
  this.set('label', label);
  this.set('score', score);
  this.render(hbs`{{context-panel/criticality-score label=label score=score}}`);
  assert.equal(this.$('.rsa-context-panel__grid__host-details__field-circle__value').text(), 'MEDIUM');
  assert.equal(this.$('.rsa-context-panel__grid__host-details__field-circle__value').css('color'), 'rgb(255, 233, 0)');
});
