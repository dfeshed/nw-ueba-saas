import { skip } from 'qunit';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-badge-score', 'Integration | Component | rsa-content-badge-score', {
  integration: true
});

test('it includes the proper classes', function(assert) {
  this.render(hbs `{{rsa-content-badge-score}}`);
  const badgeCount = this.$('.rsa-content-badge-score').length;
  assert.equal(badgeCount, 1);
});

test('it sets the label', function(assert) {
  this.render(hbs `{{rsa-content-badge-score label='Foo'}}`);
  const label = this.$('.label').text();
  assert.notEqual(label.indexOf('Foo'), -1);
});

test('it sets the score', function(assert) {
  this.render(hbs `{{rsa-content-badge-score score='9.9'}}`);
  const label = this.$('.score').text();
  assert.notEqual(label.indexOf('9.9'), -1);
});

test('it includes the proper classes when there is no label', function(assert) {
  this.render(hbs `{{rsa-content-badge-score icon='account-circle-1'}}`);
  const badge = this.$('.rsa-content-badge-score');
  assert.ok(badge.hasClass('hide-label'));
});

test('it includes the proper classes when isDanger is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-score isDanger=true}}`);
  const badge = this.$('.rsa-content-badge-score');
  assert.ok(badge.hasClass('is-danger'));
});

test('it includes the proper classes when isInline is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-score isInline=true style='medium' score=50}}`);
  const badge = this.$('.rsa-content-badge-score');
  assert.ok(badge.hasClass('is-inline'));

  const progressBar = this.$('.progress-bar');
  assert.equal(progressBar.length, 1, 'Progress bar is render');
});

test('it renders the progress bar with the correct length based on the score when isInline is true', function(assert) {
  this.render(hbs `{{rsa-content-badge-score isInline=true style='medium' score=50}}`);
  const badge = this.$('.rsa-content-badge-score');
  assert.ok(badge.hasClass('progress-bar-length-50'), 'Progress bar has the expected class name with the expected length');
  assert.equal(this.$('.progress-bar').css('flex-grow'), '0.5', 'Progress bar has 50% length');
});

skip('it renders the progress bar with the correct color based on the style', function(assert) {
  this.render(hbs `{{rsa-content-badge-score isInline=true style='medium' score=50}}`);
  const badge = this.$('.rsa-content-badge-score');
  assert.ok(badge.hasClass('progress-bar-length-50'), 'Progress bar has the expected class name with the expected length');

  const progressBar = this.$('.progress-bar');
  assert.equal(progressBar.css('background-image'), 'linear-gradient(90deg, rgba(255, 255, 255, 0), rgb(255, 160, 0))', 'Progress bar has medium color');
});

test('it includes the proper classes in smaller mode', function(assert) {
  this.render(hbs `{{rsa-content-badge-score  size='smaller' score=50}}`);
  assert.ok(this.$('svg').length <= 0, "Smaller mode sholdn't render svg");
  assert.ok(this.$('circle').length <= 0, "Smaller mode sholdn't render circle");
});
