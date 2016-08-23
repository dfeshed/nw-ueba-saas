import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-chart', 'Integration | Component | rsa-chart', {
  integration: true
});

test('it renders', function(assert) {
  this.render(hbs `{{rsa-chart}}`);
  assert.equal(this.$('.rsa-chart').length, 1, 'Testing to see if the .rsa-chart class exists');
  assert.equal(this.$('.rsa-chart-background').length, 1, 'Testing to see if the .rsa-chart-background class exists');
});

test('The Chart component is properly sized when supplied with a size attribute', function(assert) {
  this.render(hbs `{{rsa-chart width=800}}`);
  assert.equal(this.$('.rsa-chart svg').width(), 800, 'Width should be 800');

  this.render(hbs `{{rsa-chart height=100}}`);
  assert.equal(this.$('.rsa-chart svg').height(), 100, 'Height should be 100');
});

test('The Chart component is properly sized when supplied with a margin attribute', function(assert) {
  this.set('margin', { top: 0, bottom: 0, left: 0, right: 0 });
  this.render(hbs `{{rsa-chart margin=margin}}`);
  const $el = this.$('.rsa-chart svg');
  const width = $el.width();
  const height = $el.height();
  assert.equal(this.$('.rsa-chart-background').attr('width'), width, 'Width should be same as component width');
  assert.equal(this.$('.rsa-chart-background').attr('height'), height, 'Height should be same as component height');
});
