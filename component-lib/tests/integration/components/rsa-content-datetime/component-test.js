import { module, test } from 'qunit';
import { find, findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | rsa-content-datetime', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const timeFormat = {
    selected: {
      format: 'hh:mma'
    }
  };

  const dateFormat = {
    selected: {
      format: 'MM/DD/YYYY'
    }
  };

  const timezone = {
    selected: 'America/New_York'
  };

  const i18n = {
    locale: 'en'
  };

  test('it includes the proper classes', async function(assert) {
    this.set('timeFormat', timeFormat);
    this.set('dateFormat', dateFormat);
    this.set('timezone', timezone);
    this.set('timestamp', 1464108661196);
    this.set('i18n', i18n);
    await render(hbs `{{rsa-content-datetime timestamp=timestamp}}`);
    const contentCount = findAll('.rsa-content-datetime').length;
    assert.equal(contentCount, 1);
  });

  test('it renders the date and time', async function(assert) {
    this.set('timeFormat', timeFormat);
    this.set('dateFormat', dateFormat);
    this.set('timezone', timezone);
    this.set('timestamp', 1464108661196);
    this.set('i18n', i18n);
    await render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}`);
    const content = find('.rsa-content-datetime').textContent.trim();
    const title = find('.rsa-content-datetime .datetime').getAttribute('title');
    assert.equal(content, '05/24/2016 12:51pm');
    assert.equal(title, '05/24/2016 12:51pm');
  });

  test('it renders the time only', async function(assert) {
    this.set('timeFormat', timeFormat);
    this.set('dateFormat', dateFormat);
    this.set('timezone', timezone);
    this.set('timestamp', 1464108661196);
    this.set('i18n', i18n);
    await render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone displayDate=false}}`);
    const content = find('.rsa-content-datetime').textContent.trim();
    assert.equal(content, '12:51pm');
  });

  test('it renders the date only', async function(assert) {
    this.set('timeFormat', timeFormat);
    this.set('dateFormat', dateFormat);
    this.set('timezone', timezone);
    this.set('timestamp', 1464108661196);
    this.set('i18n', i18n);
    await render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone displayTime=false}}`);
    const content = find('.rsa-content-datetime').textContent.trim();
    assert.equal(content, '05/24/2016');
  });

  test('it as time ago', async function(assert) {
    this.set('timeFormat', timeFormat);
    this.set('dateFormat', dateFormat);
    this.set('timezone', timezone);
    this.set('timestamp', 1464108661196);
    this.set('i18n', i18n);
    await render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone asTimeAgo=true}}`);
    const content = find('.rsa-content-datetime').textContent.trim();
    const title = find('.rsa-content-datetime .time-ago').getAttribute('title');
    assert.ok(content.indexOf('ago') > -1);
    assert.ok(title.indexOf('ago') > -1);
  });

  test('it renders the date and time including withTimeAgo', async function(assert) {
    this.set('timeFormat', timeFormat);
    this.set('dateFormat', dateFormat);
    this.set('timestamp', new Date().getTime() - new Date(60000));
    this.set('i18n', i18n);
    await render(hbs `{{rsa-content-datetime withTimeAgo=true timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}`);
    const content = find('.rsa-content-datetime').textContent.trim();
    const title = find('.rsa-content-datetime .time-ago').getAttribute('title');
    assert.ok(content.indexOf('ago') > -1);
    assert.ok(title.indexOf('ago') > -1);
  });

});
