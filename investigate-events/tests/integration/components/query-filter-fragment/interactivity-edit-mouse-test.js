// /*
//  * These tests start with a pill, and test the
//  * effect of a single MOUSE event on the pill.
//  *
//  * Clicking, double clicking, clicking an x, etc.
//  *
//  * These tests DO NOT include what happens after
//  * the single event (editing) or keyboard events
//  * (pressing <- selecting pill)
//  *
//  */
// import { moduleForComponent, test } from 'ember-qunit';

// import { testSetupConfig, createTextPill } from './util';

// import { clickTrigger } from 'ember-power-select/test-support/helpers';

// moduleForComponent(
//   'query-filter-fragment',
//   'Integration | Component | query-filter-fragment interactivity-single-mouse-event',
//   testSetupConfig
// );

// test('clicking on editable pills meta will bring up meta dropdown', function(assert) {
//   const done = assert.async();
//   const $fragment = createTextPill(this);

//   $fragment.find('.meta').dblclick();
//   // const $powerSelectTrigger = $fragment.find('.ember-power-select-trigger');
//   // $powerSelectTrigger.focus();

//   const $input = $fragment.find('input');


//   $input.get(0).setSelectionRange(11, 11);
//   $fragment.trigger('click');

//   $input.get(0).setSelectionRange(2, 2);
//   $fragment.trigger('click');
//   // $fragment.trigger('click');
//   // clickTrigger('.rsa-query-fragment');
//   // $input.click();


//   // const rect = $input.get(0).getBoundingClientRect();
//   // console.log(rect)
//   // console.log(document.caretRangeFromPoint(rect.x + 5, rect.y + 100));
//   // console.log(document.elementFromPoint(7, 315))
//   // console.log(document.caretRangeFromPoint(7, 315))

//   // const e = new $.Event('click');
//   // e.pageX = 10;
//   // e.pageY = 315;
//   // $fragment.trigger(e);
// });
