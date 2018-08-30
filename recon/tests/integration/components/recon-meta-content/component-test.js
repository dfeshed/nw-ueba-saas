import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, triggerEvent } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;
module('Integration | Component | recon-meta-content', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => patchReducer(this, state);
    initialize(this.owner);
  });


  test('several meta items render correctly', async function(assert) {
    new ReduxDataHelper(setState).meta([
    [ 'size', 62750 ],
    [ 'payload', 56460 ],
    [ 'medium', 1 ],
    [ 'eth.src', '70:56:81:9A:94:DD' ],
    [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
    ]).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(findAll('.recon-meta-content-item').length, 5);
  });

  test('zero meta items render correctly', async function(assert) {
    new ReduxDataHelper(setState).meta([]).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(findAll('.recon-meta-content-item').length, 0);

  });

  test('show tooltip for endpoint event lengthy meta', async function(assert) {
    const endpointData = [{
      'charset': 'UTF-8',
      'contentDecoded': true,
      'firstPacketId': 1,
      'firstPacketTime': 1485792552870,
      'text': 'param.dst=test-value test-value test-value test-value test-value 0000000'
    }];

    new ReduxDataHelper(setState).meta([
    [ 'param.dst', 'test-value test-value' ],
    [ 'nwe.callback_id', 'foo' ]
    ])
    .endpointText(endpointData)
    .build();

    await render(hbs`{{recon-meta-content}}`);
    document.querySelector('.tooltip-text').setAttribute('style', 'width:100px');
    await triggerEvent('.tooltip-text', 'mouseover');
    assert.equal(findAll('.ember-tether').length, 1, 'Tool tip is rendered');
    assert.ok(find('.ember-tether .tool-tip-value').textContent.indexOf('test-value') > 0);
    assert.equal(find('.ember-tether .tool-tip-value .tool-tip-note').textContent.trim(), 'Note:Only the initial 255 characters of the value are indexed as part of this meta, and will be included in any indexed search.');
    await triggerEvent('.tooltip-text', 'mouseout');
    assert.equal(findAll('.ember-tether .tool-tip-value').length, 0, 'Tool tip is hidden');
  });
});
