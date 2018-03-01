import Controller from '@ember/controller';

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