import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import VisualActions from 'recon/actions/visual-creators';
import { render, find, findAll } from '@ember/test-helpers';
import DataHelper from '../../../../helpers/data-helper';

const _first200 = (str) => str.trim().replace(/\s/g, '').substring(0, 200);

module('Integration | Component | recon event detail text', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.redux = this.owner.lookup('service:redux');
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('text view renders encoded text', async function(assert) {
    new DataHelper(this.get('redux')).populateTexts();
    await render(hbs`{{recon-event-detail/text-content}}`);
    const str = find('.recon-event-detail-text').textContent;
    assert.ok(_first200(str) === 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comresponseHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,' || _first200(str) === 'requestrequestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comresponseHTTP/1.1200OKCache-control:no-store,no-cache,must-reva');
  });

  test('text view renders decoded text', async function(assert) {
    new DataHelper(this.get('redux')).setViewToText().populateTexts(true);
    await render(hbs`{{recon-event-detail/text-content}}`);
    const str = find('.recon-event-detail-text').textContent;
    assert.ok(_first200(str) === 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1Host:www.saavn.comConnection:keep-aliveAccept:*/*X-Requested-With:XMLHttpRequestUser-Age' || _first200(str) === 'responserequestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1Host:www.saavn.comConnection:keep-aliveAccept:*/*X-Requested-With:XMLHttpRequest');
  });

  test('text view renders log text', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ meta: [['medium', 32]] })
      .setViewToText()
      .populateTexts();
    await render(hbs`{{recon-event-detail/text-content}}`);
    const str = find('.recon-event-detail-text').textContent;
    assert.ok(_first200(str) === 'RawLogGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comRawLogHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,pri' || _first200(str) === 'RawLogRawLogGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comRawLogHTTP/1.1200OKCache-control:no-store,no-cache,must-revalida');
  });

  test('text view renders raw endpoint text', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ meta: [['medium', 3], ['nwe.callback_id', 23], ['category', 'File'], ['filename', 'cmd.exe'], ['directory', 'C:\\Users\\']] })
      .setViewToText()
      .populateTexts();
    await render(hbs`{{recon-event-detail/text-content}}`);
    assert.equal(findAll('.recon-event-detail-endpoint').length, 1, 'Endpoint detail is rendered');
    assert.equal(find('.endpoint-detail-header').textContent.trim(), 'File');
  });

  test('renders spinner when data present but in the process of being rendered', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .populateTexts(false, false);
    await render(hbs`{{recon-event-detail/text-content}}`);
    const loader = findAll('.recon-loader').length;
    assert.equal(loader, 1);
  });

  test('renders error when no data present and is network event', async function(assert) {
    new DataHelper(this.get('redux'))
      .initializeData({ meta: [['service', 80]] })
      .setViewToText()
      .noTexts();
    await render(hbs`{{recon-event-detail/text-content}}`);
    const str = find('.rsa-panel-message').textContent.trim().replace(/\s/g, '');
    assert.equal(str, 'Notextdatawasgeneratedduringcontentreconstruction.Thiscouldmeanthattheeventdatawascorruptorinvalid.TryenablingtheDisplayCompressedPayloadsbuttonorchecktheotherreconstructionviews.');
  });

  test('renders error when no data present and is log event', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .noTexts();
    await render(hbs`{{recon-event-detail/text-content}}`);
    const str = find('.rsa-panel-message').textContent.trim().replace(/\s/g, '');
    assert.equal(str, 'Notextdatawasgeneratedduringcontentreconstruction.Thiscouldmeanthattheeventdatawascorruptorinvalid.Checktheotherreconstructionviews.');
  });

  test('renders a message when data present, but hidden by request/response', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .populateTexts();
    this.get('redux').dispatch(VisualActions.toggleRequestData());
    this.get('redux').dispatch(VisualActions.toggleResponseData());

    await render(hbs`{{recon-event-detail/text-content}}`);
    // remove the pager so its text doesn't confuse test
    const pager = find('.recon-pager');
    pager.parentNode.removeChild(pager);

    const translation = this.owner.lookup('service:i18n');

    const noContentString = find('.rsa-panel-message').textContent.trim();
    assert.equal(noContentString, translation.t('recon.textView.contentHiddenMessage').trim(), 'Did not find no content message panel');
  });

  test('renders a message when data present, but hidden by request', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .populateTextRequest();
    this.get('redux').dispatch(VisualActions.toggleRequestData());

    await render(hbs`{{recon-event-detail/text-content}}`);
    // remove the pager so its text doesn't confuse test
    const pager = find('.recon-pager');
    pager.parentNode.removeChild(pager);

    const translation = this.owner.lookup('service:i18n');

    const noContentString = find('.rsa-panel-message').textContent.trim();
    assert.equal(noContentString, translation.t('recon.textView.contentHiddenMessage').trim(), 'Did not find no content message panel');
  });

  test('renders a message when data present, but hidden by response', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .populateTextResponse();
    this.get('redux').dispatch(VisualActions.toggleResponseData());

    await render(hbs`{{recon-event-detail/text-content}}`);
    // remove the pager so its text doesn't confuse test
    const pager = find('.recon-pager');
    pager.parentNode.removeChild(pager);

    const translation = this.owner.lookup('service:i18n');

    const noContentString = find('.rsa-panel-message').textContent.trim();
    assert.equal(noContentString, translation.t('recon.textView.contentHiddenMessage').trim(), 'Did not find no content message panel');
  });

  test('will not render noContent message if request is hidden but response has content', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .populateTexts();
    this.get('redux').dispatch(VisualActions.toggleRequestData());

    await render(hbs`{{recon-event-detail/text-content}}`);
    // remove the pager so its text doesn't confuse test
    const pager = find('.recon-pager');
    pager.parentNode.removeChild(pager);

    assert.notOk(find('.rsa-panel-message'), 'Should not find no content message panel');
  });

  test('will not render noContent message if response is hidden but request has content', async function(assert) {
    new DataHelper(this.get('redux'))
      .setViewToText()
      .populateTexts();
    this.get('redux').dispatch(VisualActions.toggleResponseData());

    await render(hbs`{{recon-event-detail/text-content}}`);
    // remove the pager so its text doesn't confuse test
    const pager = find('.recon-pager');
    pager.parentNode.removeChild(pager);

    assert.notOk(find('.rsa-panel-message'), 'Should not find no content message panel');
  });
});