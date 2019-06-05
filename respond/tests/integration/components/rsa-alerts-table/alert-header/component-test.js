import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import StoryPoint from 'respond/utils/storypoint/storypoint';
import { findAll, render, click } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | rsa alerts table alert header', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  const indicator = {
    id: 'id1',
    alert: {}
  };
  const events = [{
    id: 'event1'
  }];
  const enrichments = [{
    isEnrichment: true,
    key: 'foo',
    i18nKey: 'i18nFoo',
    value: 'bar',
    allEnrichments: {}
  }];

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.set('indicator', indicator);
    this.set('events', events);
    this.set('index', 1);
    this.set('enrichments', enrichments);
  });

  test('it renders tabs, and highlights the appropriate tabs when they are clicked', async function(assert) {
    this.set('group', StoryPoint.create({
      indicator: this.get('indicator'),
      events: this.get('events'),
      enrichments: this.get('enrichments'),
      showEnrichmentsAsItems: false,
      isOpen: false
    }));
    await render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);
    assert.equal(findAll('.rsa-alerts-table-alert-header').length, 1, 'Expected to find root DOM node.');

    let tabs = findAll('.tab');
    assert.equal(tabs.length, 2, 'Expected to find 2 tabs');
    assert.notOk(tabs[0].classList.contains('active'), 'Expected both tabs to be closed initially');
    assert.notOk(tabs[1].classList.contains('active'), 'Expected both tabs to be closed initially');
    // Click on the first tab; that should open it.
    await click(tabs[0]);
    tabs = findAll('.tab');
    assert.ok(tabs[0].classList.contains('active'), 'Expected first tab to be open after being clicked');
    assert.notOk(tabs[1].classList.contains('active'), 'Expected second tab to remain closed');
    // Click on the second tab; that should open it and close the 1st tab.
    await click(tabs[1]);
    tabs = findAll('.tab');
    assert.notOk(tabs[0].classList.contains('active'), 'Expected first tab to be closed after 2nd tab is opened');
    assert.ok(tabs[1].classList.contains('active'), 'Expected second tab to open after being clicked');
    // Click on the second tab again; that should close it.
    await click(tabs[1]);
    tabs = findAll('.tab');
    assert.notOk(tabs[0].classList.contains('active'), 'Expected both tabs to be closed');
    assert.notOk(tabs[1].classList.contains('active'), 'Expected both tabs to be closed');
  });

  test('it omits the enrichments tab if there are no enrichments', async function(assert) {
    this.set('group', StoryPoint.create({
      indicator: this.get('indicator'),
      events: this.get('events'),
      enrichments: [],
      showEnrichmentsAsItems: false,
      isOpen: false
    }));

    await render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);
    const tabs = findAll('.tab');
    assert.equal(tabs.length, 1, 'Expected to find only 1 tab');
    assert.ok(tabs[0].classList.contains('rsa-alerts-table-alert-header__events'), 'Expected to find the events tab');
    assert.notOk(tabs[0].classList.contains('rsa-alerts-table-alert-header__enrichments'), 'Expected to not find the enrichments tab');
  });

  test('it omits the events tab if there are no events', async function(assert) {
    this.set('group', StoryPoint.create({
      indicator: this.get('indicator'),
      events: null,
      enrichments: null,
      showEnrichmentsAsItems: false,
      isOpen: false
    }));

    await render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);
    const tabs = findAll('.tab');
    assert.notOk(tabs.length, 'Expected to find zero tabs');
  });

  test('events tab will render ueba link component for alerts', async function(assert) {
    this.set('group', StoryPoint.create({
      indicator: this.get('indicator'),
      events: this.get('events'),
      enrichments: [],
      showEnrichmentsAsItems: false,
      isOpen: false
    }));

    await render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);
    assert.equal(findAll('[test-id=respondUebaLink]').length, 1);
  });
});
