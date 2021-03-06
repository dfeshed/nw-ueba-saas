import { find, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-content-badge-score', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{rsa-content-badge-score}}`);
    const badgeCount = findAll('.rsa-content-badge-score').length;
    assert.equal(badgeCount, 1);
  });

  test('it sets the label', async function(assert) {
    await render(hbs `{{rsa-content-badge-score label='Foo'}}`);
    const label = find('.label').textContent;
    assert.notEqual(label.indexOf('Foo'), -1);
  });

  test('it sets the score', async function(assert) {
    await render(hbs `{{rsa-content-badge-score score='9.9'}}`);
    const label = find('.score').textContent;
    assert.notEqual(label.indexOf('9.9'), -1);
  });

  test('it includes the proper classes when there is no label', async function(assert) {
    await render(hbs `{{rsa-content-badge-score icon='account-circle-1'}}`);
    const badge = find('.rsa-content-badge-score');
    assert.ok(badge.classList.contains('hide-label'));
  });

  test('it includes the proper classes when isDanger is true', async function(assert) {
    await render(hbs `{{rsa-content-badge-score style='danger'}}`);
    const badge = find('.rsa-content-badge-score');
    assert.ok(badge.classList.contains('is-danger'));
  });

  test('it includes the proper classes when isInline is true', async function(assert) {
    await render(hbs `{{rsa-content-badge-score isInline=true style='medium' score=50}}`);
    const badge = find('.rsa-content-badge-score');
    assert.ok(badge.classList.contains('is-inline'));

    const progressBar = findAll('.progress-bar');
    assert.equal(progressBar.length, 1, 'Progress bar is render');
  });

  test('it renders the progress bar with the correct length based on the score when isInline is true', async function(assert) {
    await render(hbs `{{rsa-content-badge-score isInline=true style='medium' score=50}}`);
    const badge = find('.rsa-content-badge-score');
    assert.ok(badge.classList.contains('progress-bar-length-50'), 'Progress bar has the expected class name with the expected length');
    assert.equal(window.getComputedStyle(find('.progress-bar')).getPropertyValue('flex-grow'), '0.5', 'Progress bar has 50% length');
  });

  test('it renders the progress bar with the correct class and style', async function(assert) {
    this.set('isInline', true);
    this.set('style', 'medium');
    this.set('score', 50);
    await render(hbs `{{rsa-content-badge-score isInline=isInline style=style score=score}}`);
    const badge = find('.rsa-content-badge-score');
    assert.ok(badge.classList.contains('progress-bar-length-50'), 'Progress bar has the expected class name with the expected length');
    assert.ok(badge.classList.contains('is-medium'), 'Progress bar has the expected style');
  });

  test('it includes the proper classes in smaller mode', async function(assert) {
    await render(hbs `{{rsa-content-badge-score  size='smaller' score=50}}`);
    assert.ok(findAll('svg').length <= 0, "Smaller mode sholdn't render svg");
    assert.ok(findAll('circle').length <= 0, "Smaller mode sholdn't render circle");
  });
});
