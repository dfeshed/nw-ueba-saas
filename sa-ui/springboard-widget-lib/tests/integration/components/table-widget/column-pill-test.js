import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

module('Integration | Component | table-widget/pill', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders pill based on score', async function(assert) {

    this.set('score', 100);
    await render(hbs`<TableWidget::ColumnPill @score={{this.score}}/>`);

    assert.dom('.is-danger').exists({ count: 1 });

    this.set('score', 0);
    assert.dom('.is-low').exists({ count: 1 });

    this.set('score', 60);
    assert.dom('.is-medium').exists({ count: 1 });

    this.set('score', 92);
    assert.dom('.is-high').exists({ count: 1 });

  });
});
