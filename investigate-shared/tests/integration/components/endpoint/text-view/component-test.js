import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | endpoint/text-view', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('text-view component renders', async function(assert) {
    const fileData = {
      hash: 'a873a7d3b90c6f2d156e5026b72a5652d4893081cd188300141a95dc38cba56b',
      fileName: 'gatherNetworkInfo.vbs',
      encodedData: [
        'RGltIEZTTywgc2hlbGwsIHhzbFByb2Nlc3Nvcg0',
        'AGltIEZTTywgc2hlbGwsIHhzbFByb2Nlc3Nvcg0',
        'BGltIEZTTywgc2hlbGwsIHhzbFByb2Nlc3Nvcg0'
      ]
    };
    this.set('fileData', fileData);

    await render(hbs`{{endpoint/text-view fileData=fileData}}`);

    assert.equal(findAll('textarea.text-view').length, 1, 'text view component has rendered.');
  });

  test('Text-view component renders base64 data set in unicode', async function(assert) {
    const fileData = {
      hash: 'a873a7d3b90c6f2d156e5026b72a5652d4893081cd188300141a95dc38cba56b',
      fileName: 'gatherNetworkInfo.vbs',
      encodedData: [
        'ZGFua29nYWk=',
        '5bCP6aO85by+',
        '4pyTIMOgIGxhIG1vZGUK'
      ]
    };
    this.set('fileData', fileData);

    await render(hbs`{{endpoint/text-view fileData=fileData}}`);

    assert.equal(find('textarea.text-view').textContent.trim(), 'dankogai小飼弾✓ à la mode', 'Array of base64 strings converted into unicode');
  });

});
