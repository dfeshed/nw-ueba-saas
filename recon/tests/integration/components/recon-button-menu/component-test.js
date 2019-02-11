import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, triggerKeyEvent } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import $ from 'jquery';

const ARROW_UP_KEY = 38;
const ARROW_DOWN_KEY = 40;

const options = [{ key: 'PCAP', value: 'downloadPCAP' },
  { key: 'PAYLOAD', value: 'downloadPayload' },
  { key: 'PAYLOAD1', value: 'downloadPayload1' },
  { key: 'PAYLOAD2', value: 'downloadPayload2' } ];

const menuOffsetsStyle = (el) => {
  if (el) {
    const elRect = el.getBoundingClientRect();
    return `top: ${elRect.height - 1}px`.htmlSafe();
  } else {
    return null;
  }
};

module('Integration | Component | recon button menu', function(hooks) {
  setupRenderingTest(hooks);

  test('onkeyup arrow up/down will traverse through options correctly', async function(assert) {

    this.set('items', options);
    this.set('isExpanded', true);
    this.set('offsetsStyle', menuOffsetsStyle(this.get('element')));

    await render(hbs `
      {{#recon-button-menu isExpanded=isExpanded style=offsetsStyle currentIndex=-1 items=items menuStyle='export-packet-menu' tabindex=0}}
        {{#each items as |option|}}
          <li tabindex='-1'>
            <a>{{option.value}}</a>
          </li>
        {{/each}}
      {{/recon-button-menu}}`);

    const buttonMenuSelector = '.recon-button-menu';

    await triggerKeyEvent(buttonMenuSelector, 'keyup', ARROW_UP_KEY);
    assert.ok($('.recon-button-menu li:nth-child(4)').is(':focus'), 'Last option focussed');

    await triggerKeyEvent(buttonMenuSelector, 'keyup', ARROW_DOWN_KEY);
    assert.notOk($('.recon-button-menu li:nth-child(4)').is(':focus'), 'Last option not focussed anymore');
    assert.ok($('.recon-button-menu li:nth-child(1)').is(':focus'), 'First option focussed');

    await triggerKeyEvent(buttonMenuSelector, 'keyup', ARROW_DOWN_KEY);
    assert.ok($('.recon-button-menu li:nth-child(2)').is(':focus'), 'Second option focussed');

    await triggerKeyEvent(buttonMenuSelector, 'keyup', ARROW_UP_KEY);
    assert.ok($('.recon-button-menu li:nth-child(1)').is(':focus'), 'First option focussed');

  });

});