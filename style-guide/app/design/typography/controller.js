import Ember from 'ember';
const { Controller } = Ember;

export default Controller.extend({
  toc: [
    {
      selector: '#typeface',
      title: 'Typeface'
    },
    {
      selector: '#styles',
      title: 'Styles'
    },
    {
      selector: '#sizes',
      title: 'Sizes'
    },
    {
      selector: '#weight',
      title: 'Weight'
    }
  ]
});