import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-carousel', 'Integration | Component | rsa-carousel', {
  integration: true,
  mock: []
});

test('The carousel component properly renders with the correct number of expected elements.', function(assert) {
  this.render(hbs`{{rsa-carousel}}`);
  assert.equal(this.$('section.rsa-carousel').length, 1, 'Testing to see if the section.rsa-carousel element exists.');
  assert.equal(this.$('section.rsa-carousel__body').length, 1, 'Testing to see if the section.rsa-carousel__body element exists.');
  assert.equal(this.$('nav.rsa-carousel__arrow').length, 2, 'Testing to see if both nav.rsa-carousel__arrow elements exist.');
  assert.equal(this.$('nav.rsa-carousel__arrow .rsa-form-button span i.rsa-icon-arrow-left-12').length, 1, 'Testing to see if the left arrow icon button exists.');
  assert.equal(this.$('nav.rsa-carousel__arrow .rsa-form-button span i.rsa-icon-arrow-right-12').length, 1, 'Testing to see if the right arrow icon button exists.');
  assert.equal(this.$('section.rsa-carousel__viewport').length, 1, 'Testing to see if the section.rsa-carousel__viewport element exists.');
  assert.equal(this.$('footer.rsa-carousel__footer').length, 1, 'Testing to see if the footer.rsa-carousel__footer element exists.');
});

test('The number of content cards and navigational dots appropriately changes to match the parent container of the carousel.', function(assert) {
  this.set('mock', ['a','b','c','d','e','f','g','h','i','j']);
  this.render(hbs`
    <div class="constraintContainer" style="width:1440px">
      {{#rsa-carousel class="rsa-incident-carousel" items=mock as |incident|}}
        <div class="rsa-content-card" style="width:350px"></div>
      {{/rsa-carousel}}
    </div>
  `);
  assert.equal(this.$('.rsa-carousel__visible-items .rsa-content-card').length, 3, 'Given a container width of 1440px and a card width of 350px, I should be showing three cards.');
  assert.equal(this.$('.rsa-carousel__footer .dot.is-selected').length, 1, 'I should be showing one selected navigational dot.');
  assert.equal(this.$('.rsa-carousel__footer .dot').length, 4, 'Given a container width of 1440px and a card width of 350px, I should be showing four navigational dots.');

  this.set('mock', ['a','b','c','d','e','f','g','h','i','j']);
  this.render(hbs`
    <div class="constraintContainer" style="width:1000px">
      {{#rsa-carousel class="rsa-incident-carousel" items=mock as |incident|}}
        <div class="rsa-content-card" style="width:350px"></div>
      {{/rsa-carousel}}
    </div>
  `);
  assert.equal(this.$('.rsa-carousel__visible-items .rsa-content-card').length, 2, 'Given a container width of 1000px and a card width of 350px, I should be showing two cards.');
  assert.equal(this.$('.rsa-carousel__footer .dot.is-selected').length, 1, 'I should be showing one selected navigational dot.');
  assert.equal(this.$('.rsa-carousel__footer .dot').length, 5, 'Given a container width of 1000px and a card width of 350px, I should be showing five navigational dots.');
});

test('The navigational arrow buttons react properly to user clicks.', function(assert) {
  this.set('mock', ['a','b','c','d','e','f','g','h','i','j']);
  this.render(hbs`
    <div class="constraintContainer" style="width:1440px">
      {{#rsa-carousel class="rsa-incident-carousel" items=mock as |incident|}}
        <div class="rsa-content-card" style="width:350px"></div>
      {{/rsa-carousel}}
    </div>
  `);
  assert.equal($('.rsa-carousel__footer .dot:nth-child(1)').attr('class').indexOf('is-selected') !== -1, true, 'On initial load, the first navigational dot should be selected.');
  $('.rsa-carousel__arrow i.rsa-icon-arrow-right-12').trigger('click');
  assert.equal($('.rsa-carousel__footer .dot:nth-child(2)').attr('class').indexOf('is-selected') !== -1, true, 'After the right arrow is clicked, the second navigational dot should be selected.');
  $('.rsa-carousel__arrow i.rsa-icon-arrow-left-12').trigger('click');
  assert.equal($('.rsa-carousel__footer .dot:nth-child(1)').attr('class').indexOf('is-selected') !== -1, true, 'After the left arrow is clicked, the first navigational dot should be selected.');
});

test('The navigational dots react properly to user clicks.', function(assert) {
  this.set('mock', ['a','b','c','d','e','f','g','h','i','j']);
  this.render(hbs`
    <div class="constraintContainer" style="width:1440px">
      {{#rsa-carousel class="rsa-incident-carousel" items=mock as |incident|}}
        <div class="rsa-content-card" style="width:350px"></div>
      {{/rsa-carousel}}
    </div>
  `);
  assert.equal($('.rsa-carousel__footer .dot:nth-child(1)').attr('class').indexOf('is-selected') !== -1, true, 'On initial load, the first navigational dot should be selected.');
  $('.rsa-carousel__footer .dot:nth-child(2)').trigger('click');
  assert.equal($('.rsa-carousel__footer .dot:nth-child(2)').attr('class').indexOf('is-selected') !== -1, true, 'After the second navigational dot is clicked, the second navigational dot should be selected.');
});

test('The number of dots increases in accordance with the model receiving new elements.', function(assert) {
  this.set('mock', ['a','b','c','d','e','f','g','h','i','j']);
  this.render(hbs`
    <div class="constraintContainer" style="width:1440px">
      {{#rsa-carousel class="rsa-incident-carousel" items=mock as |incident|}}
        <div class="rsa-content-card" style="width:350px"></div>
      {{/rsa-carousel}}
    </div>
  `);
  assert.equal($('.rsa-carousel__footer .dot').length, 4, 'On initial load, with 10 items in the model there should be 4 navigational dots.');
  this.set('mock', ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t']);
  assert.equal($('.rsa-carousel__footer .dot').length, 7, 'After receiving 10 additional items, there should be 7 navigational dots.');
});

test('The carousel component can properly handle the multiple rows feature.', function(assert) {
  this.set('mock', ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t']);
  this.render(hbs`
    <div class="constraintContainer" style="width:1440px">
      {{#rsa-carousel class="rsa-incident-carousel" allowMultipleRows=true items=mock as |incident|}}
        <div class="rsa-content-card" style="width:350px"></div>
      {{/rsa-carousel}}
    </div>
  `);
  assert.equal($('.rsa-carousel__visible-items .rsa-content-card').length, 20, 'On initial load, with 20 items in the model and allowMultipleRows set to true there should be 20 items shown.');
  assert.equal($('.rsa-carousel__footer .dot').length, 0, 'On initial load, with 20 items in the model and allowMultipleRows set to true there should be 0 navigational dots shown.');
});