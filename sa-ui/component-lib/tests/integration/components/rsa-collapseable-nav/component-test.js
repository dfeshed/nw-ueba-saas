import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import EmberObject from '@ember/object';

const contentSections = [
  EmberObject.create({
    name: 'files',
    label: 'Files'
  }),
  EmberObject.create({
    name: 'hosts',
    label: 'Hosts'
  }),
  EmberObject.create({
    name: 'packets',
    label: 'Packets'
  })
];

module('Integration | Component | rsa-collapseable-nav', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders without sections', async function(assert) {
    await render(hbs `{{rsa-collapseable-nav}}`);

    const nav = find('.rsa-collapseable-nav.nav-expanded');
    const forExpanded = findAll('.for-expanded');
    const expandedTabs = findAll('.for-expanded .rsa-nav-tab');
    const trigger = findAll('.for-expanded .intersection-trigger');
    const forCollapsed = findAll('.for-collapsed');
    const collapsedTabs = findAll('.for-collapsed .rsa-nav-tab');

    assert.equal(trigger.length, 1);
    assert.equal(forCollapsed.length, 1);
    assert.equal(forExpanded.length, 1);
    assert.equal(collapsedTabs.length, 0);
    assert.equal(expandedTabs.length, 0);
    assert.ok(nav);
  });

  test('it renders with sections', async function(assert) {
    this.set('contentSections', contentSections);
    this.set('activeTab', null);

    await render(hbs `{{rsa-collapseable-nav activeTab=activeTab contentSections=contentSections}}`);

    const nav = find('.rsa-collapseable-nav.nav-expanded');
    const forExpanded = findAll('.for-expanded');
    const expandedTabs = findAll('.for-expanded .rsa-nav-tab');
    const trigger = findAll('.for-expanded .intersection-trigger');
    const forCollapsed = findAll('.for-collapsed');
    const collapsedTabs = findAll('.for-collapsed .rsa-nav-tab');

    assert.equal(trigger.length, 1, 'trigger.length');
    assert.equal(forCollapsed.length, 1, 'forCollapsed.length');
    assert.equal(forExpanded.length, 1, 'forExpanded.length');
    assert.equal(collapsedTabs.length, 0, 'collapsedTabs.length');
    assert.equal(expandedTabs.length, 3, expandedTabs.length);
    assert.ok(nav.classList.contains('rsa-collapseable-nav'), 'rsa-collapseable-nav');
    assert.ok(nav.classList.contains('nav-expanded'), 'nav-expanded');
    assert.ok(nav, 'nav');
  });

  test('it renders and updates activeTab', async function(assert) {
    this.set('contentSections', contentSections);
    this.set('activeTab', null);

    await render(hbs `{{rsa-collapseable-nav activeTab=activeTab contentSections=contentSections}}`);

    const nav = find('.rsa-collapseable-nav.nav-expanded');
    const forExpanded = findAll('.for-expanded');
    let expandedTabs = findAll('.for-expanded .rsa-nav-tab');
    const trigger = findAll('.for-expanded .intersection-trigger');
    const forCollapsed = findAll('.for-collapsed');
    let collapsedTabs = findAll('.for-collapsed .rsa-nav-tab');

    assert.equal(trigger.length, 1, 'trigger.length');
    assert.equal(forCollapsed.length, 1, 'forCollapsed.length');
    assert.equal(forExpanded.length, 1, 'forExpanded.length');
    assert.equal(collapsedTabs.length, 0, 'collapsedTabs.length');
    assert.equal(expandedTabs.length, 3, expandedTabs.length);
    assert.ok(nav.classList.contains('rsa-collapseable-nav'), 'rsa-collapseable-nav');
    assert.ok(nav.classList.contains('nav-expanded'), 'nav-expanded');
    assert.ok(nav, 'nav');

    this.set('activeTab', 'hosts');

    collapsedTabs = findAll('.for-collapsed .rsa-nav-tab');
    let activeCollapsed = findAll('.for-collapsed .rsa-nav-tab.is-active');
    expandedTabs = findAll('.for-expanded .rsa-nav-tab');
    let activeExpanded = findAll('.for-expanded .rsa-nav-tab.is-active');

    assert.equal(activeCollapsed.length, 1, 'activeCollapsed.length 1');
    assert.equal(activeExpanded.length, 1, 'activeExpanded.length 1');
    assert.equal(collapsedTabs.length, 1, 'collapsedTabs.length 1');
    assert.equal(expandedTabs.length, 3, 'expandedTabs.length 3');
    assert.equal(activeCollapsed[0].textContent.trim(), 'Hosts', 'collapsedTabs.textContent Hosts');
    assert.equal(activeExpanded[0].textContent.trim(), 'Hosts', 'expandedTabs.textContent Hosts');

    this.set('activeTab', 'files');

    collapsedTabs = findAll('.for-collapsed .rsa-nav-tab');
    activeCollapsed = findAll('.for-collapsed .rsa-nav-tab.is-active');
    expandedTabs = findAll('.for-expanded .rsa-nav-tab');
    activeExpanded = findAll('.for-expanded .rsa-nav-tab.is-active');

    assert.equal(activeCollapsed.length, 1, 'activeCollapsed.length 1');
    assert.equal(activeExpanded.length, 1, 'activeExpanded.length 1');
    assert.equal(collapsedTabs.length, 1, 'collapsedTabs.length 1');
    assert.equal(expandedTabs.length, 3, 'expandedTabs.length 3');
    assert.equal(activeCollapsed[0].textContent.trim(), 'Files', 'collapsedTabs.textContent Hosts');
    assert.equal(activeExpanded[0].textContent.trim(), 'Files', 'expandedTabs.textContent Hosts');
  });

  test('it calls onTabClick when tab clicked', async function(assert) {
    assert.expect(1);
    this.set('contentSections', contentSections);
    this.set('activeTab', 'hosts');
    this.set('onTabClick', () => {
      assert.ok(true);
    });

    await render(hbs `{{rsa-collapseable-nav onTabClick=onTabClick activeTab=activeTab contentSections=contentSections}}`);
    find('.for-expanded .rsa-nav-tab').click();
  });

  test('it collapses when its width changes', async function(assert) {
    assert.expect(3);
    this.set('contentSections', contentSections);
    this.set('activeTab', 'hosts');

    await render(hbs `{{rsa-collapseable-nav onTabClick=onTabClick activeTab=activeTab contentSections=contentSections}}`);
    const nav = find('.rsa-collapseable-nav');

    assert.ok(nav.className.includes('nav-expanded'));
    nav.parentElement.style.width = '1px';

    // wait for current frame batch to finish
    // also wait for next frame batch, that intersection observer will trigger
    await new Promise((resolve) => requestAnimationFrame(() => requestAnimationFrame(resolve)));

    assert.ok(nav.className.includes('nav-collapsed'));
    nav.parentElement.style.width = '1000px';

    // wait for current frame batch to finish
    // also wait for next frame batch, that intersection observer will trigger
    await new Promise((resolve) => requestAnimationFrame(() => requestAnimationFrame(resolve)));
    assert.ok(nav.className.includes('nav-expanded'));
  });

});
