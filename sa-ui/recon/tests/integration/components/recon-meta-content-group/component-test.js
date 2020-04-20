import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

const metaItems = [[ 'size', 62750 ],
  [ 'payload', 56460 ],
  [ 'eth.src', '70:56:81:9A:94:DD' ],
  [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
];

const metaFormatMap = [{ 'size': 62750 },
  { 'payload': 56460 },
  { 'eth.src': '70:56:81:9A:94:DD' },
  { 'eth.dst': '10:0D:7F:75:C4:C8' }
];

module('Integration | Component | recon-meta-content-group', function(hooks) {
  setupRenderingTest(hooks);

  test('meta items are grouped by alphabets for groupType alphabet', async function(assert) {
    this.set('metaItems', metaItems);
    this.set('metaFormatMap', metaFormatMap);
    this.set('groupType', 'alphabet');
    await render(hbs `{{recon-meta-content-group metaItems=metaItems metaFormatMap=metaFormatMap groupType=groupType}}`);

    assert.equal(findAll('.recon-meta-content-group .meta-content-section').length, 3, '3 sections should be displayed');
    assert.equal(find('.recon-meta-content-group .meta-content-section:nth-of-type(1) .section-name').textContent, 'E', 'first section name');

    assert.equal(findAll('.recon-meta-content-group .meta-content-section:nth-of-type(1) .recon-meta-content-item').length, 2, '2 meta items are there under first section');

    assert.equal(find('.recon-meta-content-group .meta-content-section:nth-of-type(1) .recon-meta-content-item .meta-name').textContent.trim(), 'eth.dst', 'meta name of first meta item');
    assert.equal(find('.recon-meta-content-group .meta-content-section:nth-of-type(1) .recon-meta-content-item .meta-value').textContent.trim(), '10:0D:7F:75:C4:C8', 'meta value of first meta item');
  });

  test('meta items are grouped by parsing order or default for groupType none', async function(assert) {
    this.set('metaItems', metaItems);
    this.set('metaFormatMap', metaFormatMap);
    this.set('groupType', 'none');
    await render(hbs `{{recon-meta-content-group metaItems=metaItems metaFormatMap=metaFormatMap groupType=groupType}}`);

    assert.equal(findAll('.recon-meta-content-group .meta-content-section').length, 0, 'meta items are not grouped into sections');
    assert.equal(findAll('.recon-meta-content-group .recon-meta-content-item').length, 4, '4 meta items are displayed');
  });
});
