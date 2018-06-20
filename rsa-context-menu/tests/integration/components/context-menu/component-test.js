import { module, test } from 'qunit';
import { contextualMenuJson } from './data';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { triggerEvent, find, findAll, settled, render } from '@ember/test-helpers';
import { patchFetch } from '../../../helpers/patch-fetch';
import { waitFor } from 'ember-wait-for-test-helper/wait-for';
import { Promise } from 'rsvp';

let fetchResolved = false;
const callback = () => {};
const wormhole = 'wormhole-context-menu';
const e = {
  clientX: 10,
  clientY: 10,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};

module('Integration | Component | Context Menu', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    fetchResolved = false;
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    document.removeEventListener('contextmenu', callback);
  });

  test('both actions and subactions render with correct label', async function(assert) {
    assert.expect(33);

    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            fetchResolved = true;
            return contextualMenuJson;
          }
        });
      });
    });

    this.set('contextSelection', {
      displayValue: '127.0.0.1',
      metaName: 'ip.src',
      metaValue: '127.0.0.1',
      moduleName: 'EventAnalysisPanel'
    });

    await render(hbs`{{#rsa-context-menu contextSelection=contextSelection}}{{context-menu}}{{/rsa-context-menu}}`);

    triggerEvent('.content-context-menu', 'contextmenu', e);

    await waitFor(() => fetchResolved === true);

    triggerEvent('.content-context-menu', 'contextmenu', e);

    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 7);
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(1) .context-menu__item__label`).textContent.trim(), 'External Lookup');
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(2) .context-menu__item__label`).textContent.trim(), 'Data Science');
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(3) .context-menu__item__label`).textContent.trim(), 'Copy');
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(4) .context-menu__item__label`).textContent.trim(), 'Live Lookup');
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(5) .context-menu__item__label`).textContent.trim(), 'Refocus');
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(6) .context-menu__item__label`).textContent.trim(), 'Refocus New Tab');
      assert.equal(find(`${selector} > .context-menu__item:nth-of-type(7) .context-menu__item__label`).textContent.trim(), 'Apply !EQUALS Drill');

      const parentSelector = `${selector} .context-menu__item--parent`;
      assert.equal(findAll(parentSelector).length, 4);

      const externalLookupSubItems = `${parentSelector}:nth-of-type(1) .context-menu--sub li`;
      assert.equal(findAll(externalLookupSubItems).length, 10);
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(1)`).textContent.trim(), 'Google Malware Diagnostic for IPs and Hostnames');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(2)`).textContent.trim(), 'SANS IP History');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(3)`).textContent.trim(), 'McAfee SiteAdvisor for Hostnames');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(4)`).textContent.trim(), 'Endpoint Thick Client Lookup');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(5)`).textContent.trim(), 'BFK Passive DNS Collection');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(6)`).textContent.trim(), 'CentralOps Whois for IPs and Hostnames');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(7)`).textContent.trim(), 'Malwaredomainlist.com Search');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(8)`).textContent.trim(), 'Robtex IP Search');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(9)`).textContent.trim(), 'ThreatExpert Search');
      assert.equal(find(`${parentSelector}:nth-of-type(1) .context-menu--sub li:nth-of-type(10)`).textContent.trim(), 'getIPVoidSearch');

      const dataScienceSubItems = `${parentSelector}:nth-of-type(2) .context-menu--sub li`;
      assert.equal(findAll(dataScienceSubItems).length, 3);
      assert.equal(find(`${parentSelector}:nth-of-type(2) .context-menu--sub li:nth-of-type(1)`).textContent.trim(), 'Suspicious Domain Report');
      assert.equal(find(`${parentSelector}:nth-of-type(2) .context-menu--sub li:nth-of-type(2)`).textContent.trim(), 'Suspicious DNS Activity Report');
      assert.equal(find(`${parentSelector}:nth-of-type(2) .context-menu--sub li:nth-of-type(3)`).textContent.trim(), 'Host Profile Report');

      const copySubItems = `${parentSelector}:nth-of-type(3) .context-menu--sub li`;
      assert.equal(findAll(copySubItems).length, 0);

      const liveLookupSubItems = `${parentSelector}:nth-of-type(4) .context-menu--sub li`;
      assert.equal(findAll(liveLookupSubItems).length, 0);

      const refocusSubItems = `${parentSelector}:nth-of-type(5) .context-menu--sub li`;
      assert.equal(findAll(refocusSubItems).length, 2);
      assert.equal(find(`${parentSelector}:nth-of-type(5) .context-menu--sub li:nth-of-type(1)`).textContent.trim(), 'Apply EQUALS');
      assert.equal(find(`${parentSelector}:nth-of-type(5) .context-menu--sub li:nth-of-type(2)`).textContent.trim(), 'Apply !EQUALS');

      const refocusNewTabSubItems = `${parentSelector}:nth-of-type(6) .context-menu--sub li`;
      assert.equal(findAll(refocusNewTabSubItems).length, 2);
      assert.equal(find(`${parentSelector}:nth-of-type(6) .context-menu--sub li:nth-of-type(1)`).textContent.trim(), 'Refocus EQUALS Drill in New Tab');
      assert.equal(find(`${parentSelector}:nth-of-type(6) .context-menu--sub li:nth-of-type(2)`).textContent.trim(), 'Refocus !EQUALS Drill in New Tab');

      const applyEqualsSubItems = `${parentSelector}:nth-of-type(7) .context-menu--sub li`;
      assert.equal(findAll(applyEqualsSubItems).length, 0);
    });
  });
});
