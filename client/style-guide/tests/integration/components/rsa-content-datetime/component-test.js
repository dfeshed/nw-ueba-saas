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
    key: 'MM/DD/YYYY'
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
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=1464108661196}}`);
  let contentCount = this.$().find('.rsa-content-datetime').length;
  assert.equal(contentCount, 1);
});

test('it renders the date and time', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=1464108661196 i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone}}`);
  let content = this.$().find('.rsa-content-datetime').text().trim();
  assert.equal(content, '05/24/2016 12:51pm');
});

test('it renders the time only', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=1464108661196 i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone displayDate=false}}`);
  let content = this.$().find('.rsa-content-datetime').text().trim();
  assert.equal(content, '12:51pm');
});

test('it renders the date only', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=1464108661196 i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone displayTime=false}}`);
  let content = this.$().find('.rsa-content-datetime').text().trim();
  assert.equal(content, '05/24/2016');
});

test('it as time ago', function(assert) {
  this.set('timeFormat', timeFormat);
  this.set('dateFormat', dateFormat);
  this.set('timezone', timezone);
  this.set('i18n', i18n);
  this.render(hbs `{{rsa-content-datetime timestamp=1464108661196 i18n=i18n timeFormat=timeFormat dateFormat=dateFormat timezone=timezone asTimeAgo=true}}`);
  let content = this.$().find('.rsa-content-datetime').text().trim();
  assert.ok(content.indexOf('ago') > -1);
});
