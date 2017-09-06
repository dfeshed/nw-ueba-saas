import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('progress-bar', 'Integration | Component | progress bar', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

test('it renders and supports a percent attribute', function(assert) {

  this.set('myProgress', null);
  this.render(hbs`{{progress-bar percent=myProgress}}`);
  assert.equal(this.$('.rsa-progress-bar').length, 1, 'Expected root DOM element.');

  const $fill = this.$('.js-progress-bar__fill');
  assert.equal($fill.css('flex-basis'), '0%', 'Expected default display to be 0%.');

  this.set('myProgress', 12.8);
  assert.equal($fill.css('flex-basis'), '13%', 'Expected numeric values to be displayed as rounded integers.');

  this.set('myProgress', 'a');
  assert.equal($fill.css('flex-basis'), '0%', 'Expected non-numeric values to display as 0%.');

  this.set('myProgress', 120);
  assert.equal($fill.css('flex-basis'), '100%', 'Expected numbers > 100 to display as 100%.');

  this.set('myProgress', -10);
  assert.equal($fill.css('flex-basis'), '0%', 'Expected numbers < 0 to display as 0%.');
});
