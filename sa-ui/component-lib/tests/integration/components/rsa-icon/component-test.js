import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-icon', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs `{{rsa-icon name='account-circle-1'}}`);
    const iconCount = findAll('.rsa-icon.rsa-icon-account-circle-1').length;
    assert.equal(iconCount, 1);
  });

  test('it includes the proper classes when isLarge is true', async function(assert) {
    await render(hbs `{{rsa-icon size='large' name='account-circle-1'}}`);
    const iconCount = findAll('.rsa-icon.is-large').length;
    assert.equal(iconCount, 1);
  });

  test('it includes the proper classes when isLarger is true', async function(assert) {
    await render(hbs `{{rsa-icon size='larger' name='account-circle-1'}}`);
    const iconCount = findAll('.rsa-icon.is-larger').length;
    assert.equal(iconCount, 1);
  });

  test('it includes the proper classes when isLargest is true', async function(assert) {
    await render(hbs `{{rsa-icon size='largest' name='account-circle-1'}}`);
    const iconCount = findAll('.rsa-icon.is-largest').length;
    assert.equal(iconCount, 1);
  });

  test('it includes the proper title', async function(assert) {
    await render(hbs `{{rsa-icon size='largest' name='account-circle-1' title='Foo'}}`);
    const title = find('.rsa-icon').getAttribute('title');
    assert.equal(title, 'Foo');
  });

  test('Help icon is accessible via tab key when displayOnTab is true', async function(assert) {
    await render(hbs `{{rsa-icon name='help-circle'}}`);
    const helpIcon = find('.rsa-icon.rsa-icon-help-circle').getAttribute('tabindex');
    assert.equal(helpIcon, -1, 'Tabindex is -1 as default');
    await render(hbs `{{rsa-icon displayOnTab=true name='help-circle'}}`);
    const helpIconwithTab = find('.rsa-icon.rsa-icon-help-circle').getAttribute('tabindex');
    assert.equal(helpIconwithTab, 0, 'Display on true tabindex is set as 0');
  });
});
