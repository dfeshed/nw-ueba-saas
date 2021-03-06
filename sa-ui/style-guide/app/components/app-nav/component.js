import Component from '@ember/component';

export default Component.extend({
  tagName: 'nav',
  classNames: 'app-nav',

  init() {
    this._super(arguments);

    this.guides = this.guides || [
      { name: 'Design Slack', link: 'https://rsadesign.slack.com' },
      { name: 'Engineering Slack', link: 'https://rsa-engineering.slack.com' },
      { name: 'GitHub', link: 'https://github.rsa.lab.emc.com/asoc/sa-ui' },
      { name: 'Jenkins', link: 'https://asoc-jenkins2.rsa.lab.emc.com/view/SA-UI/' },
      { name: 'Jira', link: 'https://bedfordjira.na.rsa.net' }
    ];

    // Define all new style guide nav sections here
    this.navSections = this.navSections || [
      {
        header: 'Application Components',
        category: 'demos.app',
        pages: [
          { name: 'Contextual Help', link: 'contextual-help' },
          { name: 'Flash Messages', link: 'flash-messages' },
          { name: 'Modals', link: 'modal' },
          { name: 'Page Titles', link: 'page-titles' },
          { name: 'Panels', link: 'panels' },
          { name: 'Panel Message', link: 'panel-message' },
          { name: 'Standard Errors', link: 'standard-errors' }
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
          { name: 'Y Axis', link: 'yAxis' },
          { name: 'Donut', link: 'donut' }
        ]
      },
      {
        header: 'Content Components',
        category: 'demos.content',
        pages: [
          { name: 'Badge Score', link: 'badgeScore' },
          { name: 'Context Menu', link: 'contextMenu' },
          { name: 'Datetime', link: 'datetime' },
          { name: 'Definition', link: 'definition' },
          { name: 'Label', link: 'label' },
          { name: 'Memory/File Size', link: 'memorySize' },
          { name: 'Tethered Panels', link: 'tetheredPanels' },
          { name: 'Page Layout', link: 'pageLayout' },
          { name: 'Risk Score', link: 'riskScore' },
          { name: 'Editable Text', link: 'editableText' },
          { name: 'Collapseable Actions', link: 'collapseableActions' },
          { name: 'Properties Panel', link: 'propertiesPanel' }
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
          { name: 'Button With Confirmation', link: 'button-with-confirmation' },
          { name: 'Checkboxes', link: 'checkboxes' },
          { name: 'Date/Time', link: 'datetime' },
          { name: 'Date/Time Range', link: 'date-time-range' },
          { name: 'Errors', link: 'errors' },
          { name: 'Radios', link: 'radios' },
          { name: 'Selects', link: 'selects' },
          { name: 'Sliders', link: 'sliders' },
          { name: 'Switches', link: 'switch' },
          { name: 'Textareas', link: 'textareas' },
          { name: 'Textareas (one-way binding)', link: 'textareas-oneway' },
          { name: 'Text Inputs', link: 'inputs' },
          { name: 'Text Inputs (one-way binding)', link: 'inputs-oneway' }
        ]
      },
      {
        header: 'Generic Components',
        category: 'demos',
        pages: [
          { name: 'Data Table', link: 'table' },
          { name: 'Icons', link: 'icons' },
          { name: 'Loader', link: 'loader' },
          { name: 'Gauge', link: 'gauge' },
          { name: 'RSA Logo', link: 'logo' },
          { name: 'Sandbox', link: 'sandbox' }
        ]
      },
      {
        header: 'Nav Components',
        category: 'demos.nav',
        pages: [
          { name: 'Link to Window', link: 'linkToWin' },
          { name: 'Tab', link: 'tab' },
          { name: 'Collapseable Navigation', link: 'collapseable-nav' }
        ]
      }
    ];
  }
});
