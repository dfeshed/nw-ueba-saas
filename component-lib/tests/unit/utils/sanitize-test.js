import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { sanitizeHtml } from 'component-lib/utils/sanitize';

module('Unit | Utils | sanitizeHtml', function(hooks) {
  setupTest(hooks);

  test('allows img tag with src, height, width, name, alt, border, hspace, vspace and align attributes', async function(assert) {
    assert.expect(1);
    const html = '<img src="#" height="1" width="1" name="x" alt="x" border="1" hspace="1" onerror=alert(1) vspace="1" align="center" />';
    const expected = '<img src="#" height="1" width="1" name="x" alt="x" border="1" hspace="1" vspace="1" align="center" />';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows id, class, style and title attributes globally', async function(assert) {
    assert.expect(1);
    const html = '<p style="color:red;" id="p" class="x"><img id="foo" class="small" title="xss" onerror=alert(1) /></p>';
    const expected = '<p style="color:red;" id="p" class="x"><img id="foo" class="small" title="xss" /></p>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows p tag with align attribute', async function(assert) {
    assert.expect(1);
    const html = '<p align="top" onerror=alert(1)>x</p>';
    const expected = '<p align="top">x</p>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows label tag with for attribute', async function(assert) {
    assert.expect(1);
    const html = '<label class="h" for="input123" data-id="random">w</label>';
    const expected = '<label class="h" for="input123">w</label>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows font tag with color, face and size attributes', async function(assert) {
    assert.expect(1);
    const html = '<font face="verdana" color="green" size="20">text</font>';
    const expected = '<font face="verdana" color="green" size="20">text</font>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows anchor tag with name, nohref, and href attributes', async function(assert) {
    assert.expect(1);
    const html = '<a href="#" nohref="#" name="x" onfocus=alert(1) onblur=alert(2) onclick=alert(3) onmousedown=alert(4) onmouseup=alert(5)>link</a>';
    const expected = '<a href="#" nohref="#" name="x">link</a>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows table tag with height, width, border, cellpadding, cellspacing, bgcolor, background, align and noresize attributes', async function(assert) {
    assert.expect(1);
    const html = '<table height="1" width="1" border="1" cellpadding="1" cellspacing="1" bgcolor="red" background="none" align="center" noresize="1"></table>';
    const expected = '<table height="1" width="1" border="1" cellpadding="1" cellspacing="1" bgcolor="red" background="none" align="center" noresize="1"></table>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows both td and th tags with background, char, charoff, colspan, rowspan, align and valign attributes', async function(assert) {
    assert.expect(1);
    const html = '<table><tr><th background="1" char="1" charoff="1" colspan="1" rowspan="1" align="center" valign="top">x</th></tr><tr><td background="2" char="1" charoff="1" colspan="1" rowspan="1" align="center" valign="top"></td></tr></table>';
    const expected = '<table><tr><th background="1" char="1" charoff="1" colspan="1" rowspan="1" align="center" valign="top">x</th></tr><tr><td background="2" char="1" charoff="1" colspan="1" rowspan="1" align="center" valign="top"></td></tr></table>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows both td and th tags with bgcolor, abbr, axis, headers, scope, nowrap, height and width attributes', async function(assert) {
    assert.expect(1);
    const html = '<table><tr><th bgcolor="red" abbr="1" axis="1" headers="1" scope="1" nowrap="1" height="1" width="1">x</th></tr><tr><td bgcolor="red" abbr="1" axis="1" headers="1" scope="1" nowrap="1" height="1" width="1"></td></tr></table>';
    const expected = '<table><tr><th bgcolor="red" abbr="1" axis="1" headers="1" scope="1" nowrap="1" height="1" width="1">x</th></tr><tr><td bgcolor="red" abbr="1" axis="1" headers="1" scope="1" nowrap="1" height="1" width="1"></td></tr></table>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows tr tag with char, height, width, charoff, valign, align and background attributes', async function(assert) {
    assert.expect(1);
    const html = '<table><tr char="1" height="1" width="1" charoff="1" valign="1" background="1"><th>x</th></tr><tr><td></td></tr></table>';
    const expected = '<table><tr char="1" height="1" width="1" charoff="1" valign="1" background="1"><th>x</th></tr><tr><td></td></tr></table>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows col and colgroup tags with span and width attributes', async function(assert) {
    assert.expect(1);
    const html = '<table><colgroup span="1" width="2"><col span="1" width="1"></col><col span="1" width="1"></col></colgroup><tr><th>x</th><th>x</th></tr><tr><td>y</td><td>y</td></tr></table>';
    const expected = '<table><colgroup span="1" width="2"><col span="1" width="1"></col><col span="1" width="1"></col></colgroup><tr><th>x</th><th>x</th></tr><tr><td>y</td><td>y</td></tr></table>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows thead, tbody, tfoot, colgroup and col tags with align, charoff, valign and char attributes', async function(assert) {
    assert.expect(1);
    const html = '<table><thead align="top" charoff="1" valign="1" char="1"><tr><th></th></tr></thead><tbody align="top" charoff="1" valign="1" char="1"><tr><td></td></tr></tbody><tfoot align="top" charoff="1" valign="1" char="1"><tr><td></td></tr></tfoot><colgroup align="top" charoff="1" valign="1" char="1"><col align="top" charoff="1" valign="1" char="1"></col></colgroup></table>';
    const expected = '<table><thead align="top" charoff="1" valign="1" char="1"><tr><th></th></tr></thead><tbody align="top" charoff="1" valign="1" char="1"><tr><td></td></tr></tbody><tfoot align="top" charoff="1" valign="1" char="1"><tr><td></td></tr></tfoot><colgroup align="top" charoff="1" valign="1" char="1"><col align="top" charoff="1" valign="1" char="1"></col></colgroup></table>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows anchor, label, noscript, h1, h2, h3, h4, h5, h6 tags', async function(assert) {
    assert.expect(1);
    const html = '<a>x</a><label>y</label><noscript>z</noscript><h1>1</h1><h2>2</h2><h3>3</h3><h4>4</h4><h5>5</h5><h6>6</h6>';
    const expected = '<a>x</a><label>y</label><noscript>z</noscript><h1>1</h1><h2>2</h2><h3>3</h3><h4>4</h4><h5>5</h5><h6>6</h6>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows p, i, b, u, strong, em, small, big, pre and code tags', async function(assert) {
    assert.expect(1);
    const html = '<p></p><i></i><b></b><u></u><strong></strong><em></em><small></small><big></big><pre></pre><code></code>';
    const expected = '<p></p><i></i><b></b><u></u><strong></strong><em></em><small></small><big></big><pre></pre><code></code>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows cite, samp, sub, sup, strike, center and blockquote tags', async function(assert) {
    assert.expect(1);
    const html = '<cite></cite><samp></samp><sub></sub><sup></sup><strike></strike><center></center><blockquote></blockquote>';
    const expected = '<cite></cite><samp></samp><sub></sub><sup></sup><strike></strike><center></center><blockquote></blockquote>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows hr, br, col, font, map, span, div and img tags', async function(assert) {
    assert.expect(1);
    const html = '<hr /><br /><col></col><font></font><map></map><span></span><div></div><img />';
    const expected = '<hr /><br /><col></col><font></font><map></map><span></span><div></div><img />';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows ul, ol, li, dd, dt, dl, tbody, thead and tfoot tags', async function(assert) {
    assert.expect(1);
    const html = '<ul><li></li></ul><ol><li></li></ol><dl><dt></dt><dd></dd></dl><tbody></tbody><thead></thead><tfoot></tfoot>';
    const expected = '<ul><li></li></ul><ol><li></li></ol><dl><dt></dt><dd></dd></dl><tbody></tbody><thead></thead><tfoot></tfoot>';
    assert.equal(sanitizeHtml(html), expected);
  });

  test('allows table, td, th, tr, colgroup, fieldset and legend tags', async function(assert) {
    assert.expect(1);
    const html = '<table><tr><th></th></tr><tr><td></td></tr></table><colgroup><col></col></colgroup><fieldset></fieldset><legend></legend>';
    const expected = '<table><tr><th></th></tr><tr><td></td></tr></table><colgroup><col></col></colgroup><fieldset></fieldset><legend></legend>';
    assert.equal(sanitizeHtml(html), expected);
  });
});
