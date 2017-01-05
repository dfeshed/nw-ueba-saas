import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({
  tagName: 'nav',
  classNames: 'app-nav',
  guides: [
    { name: 'Design Slack', link: 'https://rsadesign.slack.com' },
    { name: 'Engineering Slack', link: 'https://rsa-engineering.slack.com' },
    { name: 'GitHub', link: 'https://github.rsa.lab.emc.com/asoc/sa-ui' },
    {
      name: 'Git Workflow',
      link: 'https://github.rsa.lab.emc.com/asoc/launch-libraries/blob/master/conventions/docs/workflows/workflows.md'
    },
    { name: 'Gold Standard Definition of Done', link: 'https://wiki.na.rsa.net/display/RAEPM/Clase+Azul+DoD' },
    { name: 'Jira', link: 'https://bedfordjira.na.rsa.net' },
    { name: 'NetWitness Jenkins', link: 'https://asoc-jenkins.rsa.lab.emc.com/job/sa-ui-master' },
    { name: 'NetWitness Nightly Build', link: 'https://10.31.125.99/' },
    { name: 'User Personas', link: 'https://wiki.na.rsa.net/display/ASOCPM/ASOC+User+Personas' },
    { name: 'UI Engineering Wiki', link: 'https://wiki.na.rsa.net/display/NextGenWeb/UI+Core+Client+Backlog' },
    { name: 'UX Design Kanban', link: 'http://mur.al/b0Mqew8v' },
    { name: 'UX Design Wiki', link: 'https://wiki.na.rsa.net/pages/viewpage.action?spaceKey=ASOCPM&title=SA11UX' }
  ],
  // Define all new style guide nav sections here
  navSections: [
    {
      header: 'Application Components',
      category: 'demos.app',
      pages: [
        { name: 'Contextual Help', link: 'contextual-help' },
        { name: 'Fatal Errors', link: 'fatal-error' },
        { name: 'Flash Messages', link: 'flash-messages' },
        { name: 'Layouts and Workflows', link: 'layout-manager' },
        { name: 'Modal', link: 'modal' }
      ]
    },
    {
      header: 'Charting Components',
      category: 'demos.chart',
      pages: [
        { name: 'Area Series', link: 'areaSeries' },
        { name: 'Chart', link: 'chart' },
        { name: 'Grid Lines', link: 'grids' },
        { name: 'Line Series', link: 'lineSeries' },
        { name: 'X Axis', link: 'xAxis' },
        { name: 'Y Axis', link: 'yAxis' }
      ]
    },
    {
      header: 'Content Components',
      category: 'demos.content',
      pages: [
        { name: 'Accordion', link: 'accordion' },
        { name: 'Badge Icon', link: 'badgeIcon' },
        { name: 'Badge Score', link: 'badgeScore' },
        { name: 'Card', link: 'card' },
        { name: 'Datetime', link: 'datetime' },
        { name: 'Definition', link: 'definition' },
        { name: 'IP Connections', link: 'ipConnections' },
        { name: 'Label', link: 'label' },
        { name: 'Memory/File Size', link: 'memorySize' },
        { name: 'Section Header', link: 'sectionHeader' },
        { name: 'Tethered Panels', link: 'tetheredPanels' }
      ]
    },
    {
      header: 'Design',
      category: 'design',
      pages: [
        { name: 'Colors', link: 'colors' },
        { name: 'Layers', link: 'layers' },
        { name: 'Opacity', link: 'opacity' },
        { name: 'Typography', link: 'typography' },
        { name: 'Whitespace', link: 'whitespace' }
      ]
    },
    {
      header: 'Form Components',
      category: 'demos.form',
      pages: [
        { name: 'Buttons', link: 'buttons' },
        { name: 'Checkboxes', link: 'checkboxes' },
        { name: 'Date/Time', link: 'datetime' },
        { name: 'Radios', link: 'radios' },
        { name: 'Selects', link: 'selects' },
        { name: 'Sliders', link: 'sliders' },
        { name: 'Textareas', link: 'textareas' },
        { name: 'Text Inputs', link: 'inputs' }
      ]
    },
    {
      header: 'Generic Components',
      category: 'demos',
      pages: [
        { name: 'Icons', link: 'icons' },
        { name: 'Loader', link: 'loader' },
        { name: 'RSA Logo', link: 'logo' }
      ]
    },
    {
      header: 'Nav Components',
      category: 'demos.nav',
      pages: [
        { name: 'Link List', link: 'linkList' },
        { name: 'Link to Window', link: 'linkToWin' },
        { name: 'Tab', link: 'tab' }
      ]
    },
    {
      header: 'RSA Data Table',
      category: 'demos',
      pages: [
        { name: 'Table', link: 'table' }
      ]
    }
  ]
});
