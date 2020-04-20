import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  content: '.springboard-widget__content'
};

module('Integration | Component | SpringboardWidget/content', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders content section', async function(assert) {
    await render(hbs`<SpringboardWidget::Content />`);

    assert.dom(SELECTORS.content).exists();

  });
});
