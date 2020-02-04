import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  header: '.springboard-widget__header'
};

module('Integration | Component | SpringboardWidget/header', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the header component', async function(assert) {

    await render(hbs`<SpringboardWidget::Header />`);

    assert.dom(SELECTORS.header).exists('Header is rendered');

  });
});
