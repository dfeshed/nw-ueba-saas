import Route from '@ember/routing/route';

export default Route.extend({
  model() {
    return {
      title: 'Grids',
      description: 'Creates grid lines.',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-chart-grids.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/content/_chart.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-chart-grids.hbs',
      singleSeries: [
        [
          { x: 1412011600000, y: 57240797 },
          { x: 1412035200000, y: 40802921 },
          { x: 1412208000000, y: 54824229 },
          { x: 1412294400000, y: 62621099 },
          { x: 1412380800000, y: 60796090 },
          { x: 1412467200000, y: 46186237 },
          { x: 1412553600000, y: 53948266 },
          { x: 1412640000000, y: 63232914 },
          { x: 1412726400000, y: 52186213 },
          { x: 1412812800000, y: 3251360 }
        ]
      ]
    };
  }
});
