import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  header: '.springboard-widget__header',
  title: '.springboard-widget__header-title',
  gearIcon: '.springboard-widget__header-gear',
  rightArrow: '.springboard-widget__header-arrow'
};

module('Integration | Component | SpringboardWidget/header', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the header component', async function(assert) {

    await render(hbs`<SpringboardWidget::Header />`);

    assert.dom(SELECTORS.header).exists('Header is rendered');

  });

  test('it renders the title of the widget', async function(assert) {
    this.set('title', 'Top Risky Hosts');
    await render(hbs`<SpringboardWidget::Header @title={{this.title}}/>`);
    assert.dom(SELECTORS.title).exists('Has Title');
    assert.dom(SELECTORS.title).containsText('Top Risky Hosts');
  });

  test('it should display gear icon and right arrow icon', async function(assert) {
    await render(hbs`<SpringboardWidget::Header @title={{this.title}}/>`);
    assert.dom(SELECTORS.gearIcon).exists('Has Gear Icon');
    assert.dom(SELECTORS.rightArrow).exists('Has Right Arrow Icon');
  });

  test('it should call external function on click', async function(assert) {
    assert.expect(2);
    this.set('navigateTo', () => {
      assert.ok(true);
    });
    this.set('onEditWidget', () => {
      assert.ok(true);
    });
    await render(hbs`<SpringboardWidget::Header @editWidget={{this.onEditWidget}} @navigateTo={{this.navigateTo}} @title={{this.title}}/>`);
    await click('.springboard-widget__header-arrow');
    await click('.springboard-widget__header-gear');
  });


});
