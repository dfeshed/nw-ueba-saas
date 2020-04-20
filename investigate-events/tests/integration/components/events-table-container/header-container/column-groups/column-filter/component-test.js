import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, fillIn, find, render } from '@ember/test-helpers';

module('Integration | Component | Column Filtering', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('columnGroup item should render correctly', async function(assert) {
    assert.expect(1);
    this.set('filterText', '');
    this.set('updateText', (text) => {
      assert.equal(text, 'abc', 'filter text is passed to parent component');
    });
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-filter
        filterTextUpdated=updateText
        filterText=filterText
      }}
    `);

    await fillIn('input', 'abc');
  });

  test('columnGroup text should clear', async function(assert) {
    assert.expect(1);
    this.set('filterText', 'abc');
    this.set('updateText', (text) => {
      assert.equal(text, '', 'filter text is passed to parent component');
    });
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-filter
        filterTextUpdated=updateText
        filterText=filterText
      }}
    `);

    await click('button');
  });

  test('clear button should not appear if no text', async function(assert) {
    this.set('filterText', '');
    this.set('updateText', () => {});
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-filter
        filterTextUpdated=updateText
        filterText=filterText
      }}
    `);

    assert.notOk(find('button'), 'button should not be present');
  });

  test('clear button should appear if text', async function(assert) {
    this.set('filterText', 'abc');
    this.set('updateText', () => {});
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-filter
        filterTextUpdated=updateText
        filterText=filterText
      }}
    `);

    assert.ok(find('button'), 'button should be present');
  });

  test('text should be selected if property set', async function(assert) {
    this.set('filterText', 'abc');
    this.set('updateText', () => {});
    this.set('shouldSelectTextForRemoval', false);
    await render(hbs`
      {{events-table-container/header-container/column-groups/column-group-details/column-filter
        filterTextUpdated=updateText
        filterText=filterText
        shouldSelectTextForRemoval=shouldSelectTextForRemoval
      }}
    `);

    this.set('shouldSelectTextForRemoval', true);
    const input = find('input');
    const lengthOfSelection = input.selectionEnd - input.selectionStart;
    assert.ok(lengthOfSelection === 3, 'text in box is selected');
  });
});
