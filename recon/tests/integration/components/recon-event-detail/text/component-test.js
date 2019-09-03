import wait from 'ember-test-helpers/wait';
import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import VisualActions from 'recon/actions/visual-creators';
import DataHelper from '../../../../helpers/data-helper';

const _first200 = (str) => str.trim().replace(/\s/g, '').substring(0, 200);

moduleForComponent('recon-event-detail/text-content', 'Integration | Component | recon event detail text', {
  integration: true,
  beforeEach() {
    this.registry.injection('component:recon-event-detail/text-content', 'i18n', 'service:i18n');
    this.registry.injection('component:recon-event-detail/single-text', 'i18n', 'service:i18n');
    this.inject.service('redux');
    initialize(this);
  }
});

test('text view renders encoded text', function(assert) {
  new DataHelper(this.get('redux')).populateTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = document.querySelector('.recon-event-detail-text').textContent;
    assert.ok(_first200(str) === 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comresponseHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,' || _first200(str) === 'requestrequestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comresponseHTTP/1.1200OKCache-control:no-store,no-cache,must-reva');
  });
});

test('text view renders decoded text', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .populateTexts(true);
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = document.querySelector('.recon-event-detail-text').textContent;
    assert.ok(_first200(str) === 'requestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1Host:www.saavn.comConnection:keep-aliveAccept:*/*X-Requested-With:XMLHttpRequestUser-Age' || _first200(str) === 'requestrequestGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1Host:www.saavn.comConnection:keep-aliveAccept:*/*X-Requested-With:XMLHttpRequestU');
  });
});

test('text view renders log text', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData({ meta: [['medium', 32]] })
    .setViewToText()
    .populateTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = document.querySelector('.recon-event-detail-text').textContent;
    assert.ok(_first200(str) === 'RawLogGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comRawLogHTTP/1.1200OKCache-control:no-store,no-cache,must-revalidate,pri' || _first200(str) === 'RawLogRawLogGET/stats.php?ev=site:player:music_quality:128kbps&songid=EsAKpbWJ&_t=1485792552819&ct=1982326421HTTP/1.1$Host:www.saavn.comRawLogHTTP/1.1200OKCache-control:no-store,no-cache,must-revalida');
  });
});

test('text view renders raw endpoint text', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData({ meta: [['medium', 3], ['nwe.callback_id', 23], ['category', 'File'], ['filename', 'cmd.exe'], ['directory', 'C:\\Users\\']] })
    .setViewToText()
    .populateTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  assert.equal(document.querySelectorAll('.recon-event-detail-endpoint').length, 1, 'Endpoint detail is rendered');
  assert.equal(document.querySelector('.endpoint-detail-header').textContent.trim(), 'File');
});

test('renders spinner when data present but in the process of being rendered', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .populateTexts(false, false);
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const loader = document.querySelectorAll('.recon-loader').length;
    assert.equal(loader, 1);
  });
});

test('renders error when no data present and is network event', function(assert) {
  new DataHelper(this.get('redux'))
    .initializeData({ meta: [['service', 80]] })
    .setViewToText()
    .noTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = document.querySelector('.rsa-panel-message').textContent.trim().replace(/\s/g, '');
    assert.equal(str, 'Notextdatawasgeneratedduringcontentreconstruction.Thiscouldmeanthattheeventdatawascorruptorinvalid.TryenablingtheDisplayCompressedPayloadsbuttonorchecktheotherreconstructionviews.');
  });
});

test('renders error when no data present and is log event', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .noTexts();
  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    const str = document.querySelector('.rsa-panel-message').textContent.trim().replace(/\s/g, '');
    assert.equal(str, 'Notextdatawasgeneratedduringcontentreconstruction.Thiscouldmeanthattheeventdatawascorruptorinvalid.Checktheotherreconstructionviews.');
  });
});

test('renders nothing when data present, but hidden by request/response', function(assert) {
  new DataHelper(this.get('redux'))
    .setViewToText()
    .populateTexts();
  this.get('redux').dispatch(VisualActions.toggleRequestData());
  this.get('redux').dispatch(VisualActions.toggleResponseData());

  this.render(hbs`{{recon-event-detail/text-content}}`);
  return wait().then(() => {
    // remove the pager so its text doesn't confuse test
    const pager = document.querySelector('.recon-pager');
    pager.parentNode.removeChild(pager);

    const str = document.querySelector('.recon-event-detail-text').textContent;
    assert.equal(_first200(str), '');
  });
});