import Controller from '@ember/controller';

export default Controller.extend({
  toc: [
    {
      selector: '#multi-trigger',
      title: 'Multiple Triggers One Panel'
    },
    {
      selector: '#events',
      title: 'Events'
    },
    {
      selector: '#styles',
      title: 'Styles'
    },
    {
      selector: '#misc-config',
      title: 'Misc Configuration'
    },
    {
      selector: '#content-types',
      title: 'Content Types'
    },
    {
      selector: '#position-as-panel',
      title: 'Position as a panel'
    },
    {
      selector: '#position-as-popover',
      title: 'Position as a Popover'
    }
  ]
});