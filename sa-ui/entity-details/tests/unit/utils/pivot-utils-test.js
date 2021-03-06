import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { navigateToInvestigate } from 'entity-details/utils/pivot-utils';
import { waitUntil } from '@ember/test-helpers';
import sinon from 'sinon';
import moment from 'moment';
import { windowProxy } from 'component-lib/utils/window-proxy';

let openStub = null;
let currentUrl = null;
let newTab = false;
module('Unit | Utils | pivot-utils', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    openStub = sinon.stub(windowProxy, 'openInNewTab').callsFake((urlPassed) => {
      currentUrl = urlPassed;
      newTab = true;
    });
  });
  hooks.afterEach(function() {
    openStub.restore();
    currentUrl = null;
    newTab = false;
  });

  test('it should use host link instead of adding any filter', (assert) => {
    const item = { machine_name_link: 'https://localhost:4200/investigate/hosts', field: 'TestUser' };
    const column = { linkField: 'machine_name_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf('investigate/hosts'));
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4663','4660','4670','5145'") === -1);
      assert.ok(newTab);
    });
  });

  test('it should use process link for process_name_link', (assert) => {
    const item = { process_name_link: 'https://localhost:4200/process-analysis', field: 'TestUser' };
    const column = { linkField: 'process_name_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf('process-analysis'));
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4663','4660','4670','5145'") === -1);
      assert.ok(newTab);
    });
  });

  test('it should use process link for dst_process_link', (assert) => {
    const item = { dst_process_link: 'https://localhost:4200/process-analysis', field: 'TestUser' };
    const column = { linkField: 'dst_process_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf('process-analysis'));
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4663','4660','4670','5145'") === -1);
      assert.ok(newTab);
    });
  });

  test('it should use process link for process_name_link', (assert) => {
    const item = { src_process_link: 'https://localhost:4200/process-analysis', field: 'TestUser' };
    const column = { linkField: 'src_process_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf('process-analysis'));
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4663','4660','4670','5145'") === -1);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with schema filter', (assert) => {
    const item = { linkField: 'https://localhost:4200/investigate/events', field: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4663','4660','4670','5145'") > 0);
      // Assert entity value filter along with schema filter
      assert.ok(decodeURIComponent(currentUrl).indexOf("(username='Name1'||user.dst='Name1'||user.src='Name1')") > 0);
      // Assert Broker id should be passed one
      assert.ok(decodeURIComponent(currentUrl).indexOf('sid=brokerId') > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with active_directory schema filter', (assert) => {
    const item = { linkField: 'https://localhost:4200/investigate/events', field: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'active_directory', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4741','4742','4733','4734','4740','4794','5376','5377','5136'") > 0);
      // Assert entity value filter along with schema filter
      assert.ok(decodeURIComponent(currentUrl).indexOf("(username='Name1'||user.dst='Name1'||user.src='Name1')") > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with authentication schema filter', (assert) => {
    const item = { linkField: 'https://localhost:4200/investigate/events', field: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'authentication', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4624','4625','4769','4648'") > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with process schema filter', (assert) => {
    const item = { linkField: 'https://localhost:4200/investigate/events', field: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'process', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("'Process Event' AND device.type='nwendpoint'") > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with registry schema filter', (assert) => {
    const item = { linkField: 'https://localhost:4200/investigate/events', field: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: null };
    navigateToInvestigate('User', 'Name1', 'registry', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("'Registry Event' AND device.type='nwendpoint'") > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with file schema filter', (assert) => {
    const item = { linkField: 'https://localhost:4200/investigate/events', filename: '/sys/log/4/File.accdb' };
    const column = { linkField: 'user_link', field: 'filename', additionalFilter: '(obj.name = ${objValue} || filename = ${objValue})' };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, 'brokerId');
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("(obj.name = '%2Fsys%2Flog%2F4%2FFile.accdb' || filename = '%2Fsys%2Flog%2F4%2FFile.accdb')") > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot given link with schema filter if broker id is not passed and addition filter', (assert) => {
    const item = { user_link: 'https://localhost:4200/investigation/007f93ca-bf34-4aeb-805a-d039934842ae/events/somedata', username: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: '(obj.name = ${objValue})' };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, null);
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf("'4663','4660','4670','5145'") > 0);
      // Assert Broker id should be link one
      assert.ok(decodeURIComponent(currentUrl).indexOf('sid=007f93ca-bf34-4aeb-805a-d039934842ae') > 0);
      // Assert additional filter along with schema filter
      assert.ok(decodeURIComponent(currentUrl).indexOf('&& (obj.name = \'TestUser\')') > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot investigate event with given layout', (assert) => {
    const item = { user_link: 'https://localhost:4200/investigation/007f93ca-bf34-4aeb-805a-d039934842ae/events/somedata', username: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: '(obj.name = ${objValue})' };
    navigateToInvestigate('User', 'Name1', 'file', 1562192044531, item, column, null);
    return waitUntil(() => currentUrl !== null).then(() => {
      assert.ok(decodeURIComponent(currentUrl).indexOf('mps=default&rs=max') > 0);
      assert.ok(newTab);
    });
  });

  test('it should be able to pivot investigate event with a "starting one hour before" window and event filter applied', (assert) => {
    const item = { user_link: 'https://localhost:4200/investigation/007f93ca-bf34-4aeb-805a-d039934842ae/events/somedata', username: 'TestUser' };
    const column = { linkField: 'user_link', field: 'username', additionalFilter: '(obj.name = ${objValue})' };
    const eventTime = 1562192044;
    navigateToInvestigate('User', 'Name1', 'file', eventTime, item, column, null);
    return waitUntil(() => currentUrl !== null).then(() => {
      const decodedComponent = decodeURIComponent(currentUrl);
      assert.ok(decodedComponent.includes('&st=1562191980&et='));
      const [ , eventTimeRangeStart, eventTimeRangeEnd] = decodedComponent.match(/\(event\.time=(\d+?)-(\d+?)\)/i);
      const startMoment = moment.unix(parseInt(eventTimeRangeStart, 10));
      const endMoment = moment.unix(parseInt(eventTimeRangeEnd, 10));
      assert.ok(startMoment.isBefore(moment.unix(eventTime)));
      assert.ok(endMoment.isAfter(moment.unix(eventTime)));
      assert.equal(endMoment.diff(startMoment), 60000); // timeWindow is 1 minute
      assert.ok(decodedComponent.includes('(event.time='));
      assert.ok(newTab);
    });
  });
});
