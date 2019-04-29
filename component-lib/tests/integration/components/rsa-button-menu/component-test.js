import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

const options = [{ key: 'OPTION1', value: 'showOption1' },
  { key: 'OPTION2', value: 'showOption2' },
  { key: 'OPTION3', value: 'showOption3' } ];

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return `top: ${elRect.height - 1}px`.htmlSafe();
  } else {
    return null;
  }
};

module('Integration | Component | rsa-button-menu', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {

    this.set('items', options);
    this.set('isExpanded', true);
    this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));

    await render(hbs `
      {{#rsa-button-menu isExpanded=isExpanded style=offsetsStyle items=items}}
        {{#each items as |option|}}
          <li>
            <a>{{option.value}}</a>
          </li>
        {{/each}}
      {{/rsa-button-menu}}`);

    assert.equal(findAll('.rsa-button-menu li').length, 3, '3 drop down options are visible');
  });

});
