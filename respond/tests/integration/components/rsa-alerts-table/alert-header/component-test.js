import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import StoryPoint from 'respond/utils/storypoint/storypoint';

moduleForComponent('rsa-alerts-table/alert-header', 'Integration | Component | rsa alerts table alert header', {
  integration: true,
  resolver: engineResolverFor('respond')
});

const indicator = {
  id: 'id1',
  alert: {}
};

const events = [
  {
    id: 'event1'
  }
];

const enrichments = [
  {
    isEnrichment: true,
    key: 'foo',
    i18nKey: 'i18nFoo',
    value: 'bar',
    allEnrichments: {}
  }
];

const group = StoryPoint.create({
  indicator,
  events,
  enrichments,
  showEnrichmentsAsItems: false,
  isOpen: false
});

const index = 1;

test('it renders tabs, and highlights the appropriate tabs when they are clicked', function(assert) {
  this.setProperties({
    group,
    index
  });
  this.render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);

  return wait()
    .then(() => {
      assert.equal(this.$('.rsa-alerts-table-alert-header').length, 1, 'Expected to find root DOM node.');

      const $tabs = this.$('.tab');
      assert.equal($tabs.length, 2, 'Expected to find 2 tabs');
      assert.notOk($tabs.first().hasClass('active'), 'Expected both tabs to be closed initially');
      assert.notOk($tabs.last().hasClass('active'), 'Expected both tabs to be closed initially');

      // Click on the first tab; that should open it.
      $tabs.first().trigger('click');
      return wait();
    })
    .then(() => {
      const $tabs = this.$('.tab');
      assert.ok($tabs.first().hasClass('active'), 'Expected first tab to be open after being clicked');
      assert.notOk($tabs.last().hasClass('active'), 'Expected second tab to remain closed');

      // Click on the second tab; that should open it and close the 1st tab.
      $tabs.last().trigger('click');
      return wait();
    })
    .then(() => {
      const $tabs = this.$('.tab');
      assert.notOk($tabs.first().hasClass('active'), 'Expected first tab to be closed after 2nd tab is opened');
      assert.ok($tabs.last().hasClass('active'), 'Expected second tab to open after being clicked');

      // Click on the second tab again; that should close it.
      $tabs.last().trigger('click');
      return wait();
    })
    .then(() => {
      const $tabs = this.$('.tab');
      assert.notOk($tabs.first().hasClass('active'), 'Expected both tabs to be closed');
      assert.notOk($tabs.last().hasClass('active'), 'Expected both tabs to be closed');
    });
});

test('it omits the enrichments tab if there are no enrichments', function(assert) {
  group.set('enrichments', []);
  this.setProperties({
    group,
    index
  });
  this.render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);

  return wait()
    .then(() => {
      const $tabs = this.$('.tab');
      assert.equal($tabs.length, 1, 'Expected to find only 1 tab');
      assert.ok($tabs.hasClass('rsa-alerts-table-alert-header__events'), 'Expected to find the events tab');
      assert.notOk($tabs.hasClass('rsa-alerts-table-alert-header__enrichments'), 'Expected to not find the enrichments tab');
    });
});

test('it omits the events tab if there are no events', function(assert) {
  group.setProperties({
    events: null,
    enrichments: null
  });

  this.setProperties({
    group,
    index
  });
  this.render(hbs`{{rsa-alerts-table/alert-header group=group index=index}}`);

  return wait()
    .then(() => {
      const $tabs = this.$('.tab');
      assert.notOk($tabs.length, 'Expected to find zero tabs');
    });
});
