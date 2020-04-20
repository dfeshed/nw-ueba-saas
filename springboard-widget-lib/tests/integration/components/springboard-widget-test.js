import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  widget: '.springboard-widget',
  header: '.springboard-widget__header',
  content: '.springboard-widget__content'
};

module('Integration | Component | widget', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the widget component', async function(assert) {
    await render(hbs`<SpringboardWidget />`);

    assert.dom(SELECTORS.widget).exists('Widget is rendered');

  });

  test('it renders the header and content section if specified', async function(assert) {
    await render(hbs`
      <SpringboardWidget as |widget|>
         <widget.header/>
      </SpringboardWidget>
    `);

    assert.dom(SELECTORS.header).exists('Widget header is rendered');
    assert.dom(SELECTORS.content).doesNotExist();
    this.set('widget', {
      content: [
        {
          type: 'table'
        }
      ]
    });
    await render(hbs`
      <SpringboardWidget @widget={{this.widget}} as |widget|>
        <widget.content/>
        <widget.content/>
      </SpringboardWidget>
    `);

    assert.dom(SELECTORS.header).doesNotExist();
    assert.dom(SELECTORS.content).exists({ count: 2 });
  });
});
