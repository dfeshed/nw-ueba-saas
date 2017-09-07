import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('/rsa-content-datetime', 'Integration | Component | rsa-content-datetime', {
  integration: true
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

test('it includes the proper classes', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('timestamp', 1464108661196);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=timestamp}}`);
  const contentCount = this.$().find('.rsa-content-datetime').length;
  assert.equal(contentCount, 1);
});

test('it renders the date and time', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('timestamp', 1464108661196);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}`);
  const content = this.$().find('.rsa-content-datetime').text().trim();
  assert.equal(content, '05/24/2016 12:51pm');
});

test('it renders the time only', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('timestamp', 1464108661196);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone displayDate=false}}`);
  const content = this.$().find('.rsa-content-datetime').text().trim();
  assert.equal(content, '12:51pm');
});

test('it renders the date only', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('timestamp', 1464108661196);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone displayTime=false}}`);
  const content = this.$().find('.rsa-content-datetime').text().trim();
  assert.equal(content, '05/24/2016');
});

test('it as time ago', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('timestamp', 1464108661196);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=timestamp i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone asTimeAgo=true}}`);
  const content = this.$().find('.rsa-content-datetime').text().trim();
  assert.ok(content.indexOf('ago') > -1);
});
